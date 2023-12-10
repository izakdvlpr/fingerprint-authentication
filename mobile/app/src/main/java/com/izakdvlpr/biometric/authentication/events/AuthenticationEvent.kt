package com.izakdvlpr.biometric.authentication.events

sealed class AuthenticationEvent {
  object UserLoggedIn : AuthenticationEvent()
  object UserLoggedOut : AuthenticationEvent()
}