import express, { NextFunction, Request, Response } from 'express'
import cors from 'cors'
import { PrismaClient, User } from '@prisma/client';
import { sign, verify, TokenExpiredError, JwtPayload } from 'jsonwebtoken'

declare global {
  namespace Express {
    interface Request {
      userId: string
    }
  }
}

const prisma = new PrismaClient();
const app = express()

const sessionConfig = {
  secret: 'keyboard cat',
  tokenTypes: {
    sessionToken: {
      expiresIn: '1d'
    },
    refreshToken: {
      key: 'REFRESH_TOKEN',
      expiresIn: '1d'
    },
    fingerprintToken: {
      key: 'FINGERPRINT_TOKEN',
      expiresIn: '1d'
    },
  }
}

app.use(express.json())
app.use(cors())

app.post('/users', async (request, response) => {
  const { name, email, password } = request.body;
  
  const emailAlreadyExists = await prisma.user.findFirst({ where: { email } })
  
  if (emailAlreadyExists) {
    return response.sendStatus(400)
  }
  
  await prisma.user.create({ 
    data: { 
      name,
      email, 
      password: await Bun.password.hash(password)
    }
  })
  
  return response.sendStatus(201)
});

app.post('/sessions/login', async (request, response) => {
  const { email, password, stay } = request.body;
  
  const user = await prisma.user.findFirst({ 
    where: { email },
    select: {
      id: true,
      name: true,
      email: true,
      password: true
    }
  })
  
  if (!user) {
    return response.sendStatus(400)
  }
  
  const isPasswordMatch = await Bun.password.verify(password, user.password);

  if (!isPasswordMatch) {
    return response.sendStatus(400)
  }
  
  const token = sign(
    {}, 
    sessionConfig.secret,
    {
      subject: user.id,
      expiresIn: sessionConfig.tokenTypes.sessionToken.expiresIn,
    },
  )
  
  const data = {
    token,
    refreshToken: null,
    user: { ...user } as Partial<User>
  }
  
  delete data.user.password
  
  if (stay) {  
    const refreshTokenFound = await prisma.token.findFirst({ 
      where: { 
        type: sessionConfig.tokenTypes.refreshToken.key,
        user_id: user.id 
      }
    })
    
    if (refreshTokenFound) {
      await prisma.token.delete({ 
        where: { id: refreshTokenFound.id } 
      })
    }
    
    const refreshToken = sign(
      {}, 
      sessionConfig.secret,
      { 
        expiresIn: sessionConfig.tokenTypes.refreshToken.expiresIn
      }
    )
    
    await prisma.token.create({
      data: {
        type: sessionConfig.tokenTypes.refreshToken.key,
        token: refreshToken,
        user_id: user.id,
      }
    })
    
    Object.assign(data, { refreshToken });
  }
  
  return response.json(data)
});

app.post('/sessions/login/fingerprint', async (request, response) => {
  const fingerprintTokenFound = await prisma.token.findFirst({
    where: { 
      type: sessionConfig.tokenTypes.fingerprintToken.key,
      token: request.body.fingerprintToken
    }
  })
  
  if (!fingerprintTokenFound) {
    return response.sendStatus(400)
  }
  
  try {
    verify(fingerprintTokenFound.token, sessionConfig.secret)
  } catch (err) {
    if (err instanceof TokenExpiredError) {
      return response.sendStatus(403)
    }

    return response.sendStatus(403)
  }
  
  await prisma.token.delete({ 
    where: { id: fingerprintTokenFound.id } 
  })
  
  const user = await prisma.user.findFirst({ 
    where: { id: fingerprintTokenFound.user_id },
    select: { id: true }
  })

  if (!user) {
    return response.sendStatus(400)
  }
  
  const fingerprintToken = sign(
    {},
    sessionConfig.secret,
    { 
      expiresIn: sessionConfig.tokenTypes.fingerprintToken.expiresIn
    }
  )
  
  await prisma.token.create({
    data: {
      type: sessionConfig.tokenTypes.fingerprintToken.key,
      token: fingerprintToken,
      user_id: user.id,
    }
  })
  
  const token = sign(
    {},
    sessionConfig.secret,
    {
      subject: user.id,
      expiresIn: sessionConfig.tokenTypes.sessionToken.expiresIn,
    },
  )
  
  const refreshTokenFound = await prisma.token.findFirst({ 
    where: { 
      type: sessionConfig.tokenTypes.refreshToken.key,
      user_id: user.id 
    }
  })
  
  if (refreshTokenFound) {
    await prisma.token.delete({ 
      where: { id: refreshTokenFound.id } 
    })
  }
  
  const refreshToken = sign(
    {}, 
    sessionConfig.secret,
    { 
      expiresIn: sessionConfig.tokenTypes.refreshToken.expiresIn
    }
  )
  
  await prisma.token.create({
    data: {
      type: sessionConfig.tokenTypes.refreshToken.key,
      token: refreshToken,
      user_id: user.id,
    }
  })

  return response.json({ 
    token,
    fingerprintToken,
    refreshToken
  })
});

async function authencationMiddleware(request: Request, response: Response, next: NextFunction) {
  const authorization = request.headers.authorization
  
  if (!authorization) {
    return response.sendStatus(403)
  }
  
  const [tokenType, token] = authorization.split(/[ \t]+/)

  if (tokenType.toLowerCase() !== 'bearer') {
    return response.sendStatus(403)
  }
  
  try {
    const { sub: userId } = verify(token, sessionConfig.secret) as JwtPayload

    const user = await prisma.user.findFirst({ where: { id: userId } })

    if (!user) {
      return response.sendStatus(403)
    }

    request.userId = user.id

    return next()
  } catch (err) {
    if (err instanceof TokenExpiredError) {
      return response.sendStatus(403)
    }

    return response.sendStatus(403)
  }
}

app.post('/sessions/verify', authencationMiddleware, async (request, response) => {
  return response.sendStatus(200)
});

app.post('/sessions/refresh', async (request, response) => {
  const refreshTokenFound = await prisma.token.findFirst({ 
    where: { 
      type: sessionConfig.tokenTypes.refreshToken.key,
      token: request.body.refreshToken
    }
  })
  
  if (!refreshTokenFound) {
    return response.sendStatus(400) 
  }
  
  await prisma.token.delete({ 
    where: { id: refreshTokenFound.id } 
  })
  
  const refreshToken = sign(
    {},
    sessionConfig.secret,
    {
      expiresIn: sessionConfig.tokenTypes.refreshToken.expiresIn
    }
  )
  
  await prisma.token.create({
    data: {
      type: sessionConfig.tokenTypes.refreshToken.key,
      token: refreshToken,
      user_id: refreshTokenFound.user_id,
    }
  })
  
  const token = sign(
    {},
    sessionConfig.secret,
    {
      subject: refreshTokenFound.user_id,
      expiresIn: sessionConfig.tokenTypes.sessionToken.expiresIn
    }
  )
  
  const data = { 
    token,
    fingerprintToken: null,
    refreshToken,
  }
  
  const fingerprintTokenFound = await prisma.token.findFirst({ 
    where: { 
      type: sessionConfig.tokenTypes.fingerprintToken.key,
      user_id: refreshTokenFound.user_id
    }
  })
  
  if (fingerprintTokenFound) {
    await prisma.token.delete({ 
      where: { id: fingerprintTokenFound.id } 
    })
    
    const fingerprintToken = sign(
      {},
      sessionConfig.secret,
      { 
        expiresIn: sessionConfig.tokenTypes.fingerprintToken.expiresIn
      }
    )
    
    await prisma.token.create({
      data: {
        type: sessionConfig.tokenTypes.fingerprintToken.key,
        token: fingerprintToken,
        user_id: refreshTokenFound.user_id,
      }
    })
    
    Object.assign(data, { fingerprintToken });
  }

  return response.json(data)
});

app.post('/sessions/fingerprint', authencationMiddleware, async (request, response) => {
  const userId = request.userId
  
  const user = await prisma.user.findFirst({ 
    where: { id: userId },
    select: { id: true }
  })

  if (!user) {
    return response.sendStatus(400)
  }
  
  const fingerprintTokenFound = await prisma.token.findFirst({
    where: { 
      type: sessionConfig.tokenTypes.fingerprintToken.key,
      user_id: user.id
    }
  })
  
  if (fingerprintTokenFound) {
    await prisma.token.delete({ 
      where: { id: fingerprintTokenFound.id } 
    })
  }
  
  const fingerprintToken = sign(
    {},
    sessionConfig.secret,
    { 
      expiresIn: sessionConfig.tokenTypes.fingerprintToken.expiresIn
    }
  )
  
  await prisma.token.create({
    data: {
      type: sessionConfig.tokenTypes.fingerprintToken.key,
      token: fingerprintToken,
      user_id: user.id,
    }
  })

  return response.json({ fingerprintToken })
});

app.post('/me', authencationMiddleware, async (request, response) => {
  const userId = request.userId
  
  const user = await prisma.user.findFirst({ 
    where: { id: userId },
    select: {
      id: true,
      name: true,
      email: true,
    }
  })

  if (!user) {
    return response.sendStatus(400)
  }
  
  return response.json({ user })
});

app.listen(3333, () => console.log('server started on 3333'));