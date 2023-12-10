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