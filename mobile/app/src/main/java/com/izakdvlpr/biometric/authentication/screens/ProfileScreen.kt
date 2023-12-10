package com.izakdvlpr.biometric.authentication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izakdvlpr.biometric.authentication.viewmodels.AuthenticationViewModel

@Composable
fun ProfileScreen(authenticationViewModel: AuthenticationViewModel) {
  val authenticationState = authenticationViewModel.state.collectAsState().value

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(30.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Text(
        text = buildAnnotatedString {
          append("You are logged in as ")
          append("\"")
          withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(authenticationViewModel.authenticationPreferences.getUsernameToLogin())
          }
          append("\".")
        }
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
          checked = authenticationState.loginWithBiometricValue,
          onCheckedChange = { authenticationViewModel.setLoginWithBiometric(it) }
        )

        Text(text = "Login with biometric")
      }

      Button(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
        onClick = { authenticationViewModel.signOut() },
      ) {
        Text(text = "Sign Out")
      }
    }
  }
}

//val (username, setUsername) = remember { mutableStateOf("") }
//val (password, setPassword) = remember { mutableStateOf("") }
//val (isBiometricEnabled, setBiometricEnabled) = remember { mutableStateOf(false) }
//
//val isBiometricPromptIsAvailable = remember {
//  when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
//    BiometricManager.BIOMETRIC_SUCCESS -> true
//    else -> false
//  }
//}
//
//val isSupportToBiometric = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
//
//
//OutlinedTextField(
//modifier = Modifier.fillMaxWidth(),
//label = { Text(text = "Username") },
//value = username,
//keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//onValueChange = { setUsername(it) },
//keyboardActions = KeyboardActions(
//onDone = {
//
//}
//)
//)
//
//OutlinedTextField(
//modifier = Modifier.fillMaxWidth(),
//label = { Text(text = "Password") },
//value = password,
//keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//onValueChange = { setPassword(it) },
//keyboardActions = KeyboardActions(
//onDone = {
//
//}
//)
//)

//package com.izakdvlpr.biometric.authentication
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.compose.setContent
//import androidx.biometric.BiometricManager
//import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
//import androidx.biometric.BiometricPrompt
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.FragmentActivity
//import com.izakdvlpr.biometric.authentication.theme.BiometricAuthenticationTheme
//
//class MainActivity : FragmentActivity() {
//  override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//
//    setContent {
//      BiometricAuthenticationTheme {
//        Surface(
//          modifier = Modifier.fillMaxSize(),
//          color = MaterialTheme.colorScheme.background
//        ) {
//          BiometricScreen()
//        }
//      }
//    }
//  }
//}
//
//@Composable
//fun BiometricScreen() {
//  val context = LocalContext.current
//  val activity = LocalContext.current as FragmentActivity
//  val executor = ContextCompat.getMainExecutor(activity)
//
//  val biometricManager = remember { BiometricManager.from(context) }
//
//  val AUTHENTICATION_PREFERENCES_KEY = "authentication"
//  val USERNAME_KEY = "username"
//  val BIOMETRIC_KEY = "biometric"
//
//  val sharedPreferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_KEY, Context.MODE_PRIVATE)
//
//  val (username, setUsername) = remember { mutableStateOf("") }
//  val (password, setPassword) = remember { mutableStateOf("") }
//  val (isBiometricToAccess, setBiometricToAccess) = remember { mutableStateOf(false) }
//
//  val isBiometricPromptIsAvailable = remember {
//    when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
//      BiometricManager.BIOMETRIC_SUCCESS -> true
//      else -> false
//    }
//  }
//
//  Column(
//    modifier = Modifier
//      .fillMaxSize()
//      .padding(30.dp),
//    horizontalAlignment = Alignment.CenterHorizontally,
//    verticalArrangement = Arrangement.Center
//  ) {
//    Column(
//      verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//      OutlinedTextField(
//        modifier = Modifier.fillMaxWidth(),
//        label = { Text(text = "Username") },
//        value = username,
//        onValueChange = { setUsername(it) }
//      )
//      OutlinedTextField(
//        modifier = Modifier.fillMaxWidth(),
//        label = { Text(text = "Password") },
//        value = password,
//        onValueChange = { setPassword(it) }
//      )
//
//      Button(
//        modifier = Modifier
//          .fillMaxWidth()
//          .height(50.dp),
//        onClick = {  },
//      ) {
//        Text(text = "Sign In")
//      }
//    }
//  }
//}
//
//private fun authenticateWithBiometric(context: Context, activity: FragmentActivity) {
//  val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
//    .setTitle("Autenticação por biometria obrigatória")
//    .setSubtitle("Verifique sua identidade")
//    .setDescription("Autenticar para prosseguir")
//    .setAllowedAuthenticators(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
//    .build()
//
//  val biometricPrompt = BiometricPrompt(
//    activity,
//    ContextCompat.getMainExecutor(activity),
//    object : BiometricPrompt.AuthenticationCallback() {
//      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//        super.onAuthenticationError(errorCode, errString)
//
//        Toast
//          .makeText(context, "Error!", Toast.LENGTH_LONG)
//          .show()
//      }
//
//      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//        super.onAuthenticationSucceeded(result)
//
//        Log.d("auth", result.toString())
//
//        Toast
//          .makeText(context, "Succeeded!", Toast.LENGTH_LONG)
//          .show()
//      }
//
//      override fun onAuthenticationFailed() {
//        super.onAuthenticationFailed()
//
//        Toast
//          .makeText(context, "Failed!", Toast.LENGTH_LONG)
//          .show()
//      }
//    }
//  )
//
//  biometricPrompt.authenticate(biometricPromptInfo)
//}