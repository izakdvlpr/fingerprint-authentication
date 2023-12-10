package com.izakdvlpr.biometric.authentication.states

data class LoginState(
  val usernameValue: String = "",
  val usernameError: String? = null,
  val passwordValue: String = "",
  val passwordError: String? = null,
)