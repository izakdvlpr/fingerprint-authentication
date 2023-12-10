package com.izakdvlpr.biometric.authentication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.izakdvlpr.biometric.authentication.events.AuthenticationEvent
import com.izakdvlpr.biometric.authentication.events.LoginEvent
import com.izakdvlpr.biometric.authentication.viewmodels.AuthenticationViewModel
import com.izakdvlpr.biometric.authentication.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
  navController: NavHostController,
  authenticationViewModel: AuthenticationViewModel,
  loginViewModel: LoginViewModel
) {
  val isLoginWithBiometric = authenticationViewModel.authenticationPreferences.getLoginWithBiometric()
  val usernameToLogin = authenticationViewModel.authenticationPreferences.getUsernameToLogin()

  val loginState = loginViewModel.state.collectAsState().value

  LaunchedEffect(key1 = loginState) {
    loginViewModel.event.collect { event ->
      when (event) {
        is LoginEvent.FieldsValidated -> {
          authenticationViewModel.signIn(
            username = loginState.usernameValue,
            password = loginState.passwordValue
          )
        }
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(30.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      if (isLoginWithBiometric && usernameToLogin != null) {
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "Username") },
          value = usernameToLogin,
          enabled = false,
          onValueChange = {  }
        )

        Button(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          onClick = {  },
        ) {
          Text(text = "Sign In")
        }
      } else {
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "Username") },
          value = loginState.usernameValue,
          isError = loginState.usernameError != null,
          onValueChange = { loginViewModel.setUsernameValue(it) }
        )

        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "Password") },
          value = loginState.passwordValue,
          isError = loginState.passwordError != null,
          visualTransformation = PasswordVisualTransformation(),
          onValueChange = { loginViewModel.setPasswordValue(it) }
        )

        Button(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          onClick = { loginViewModel.validateFields() },
        ) {
          Text(text = "Sign In")
        }
      }
    }
  }
}