package com.izakdvlpr.biometric.authentication.events

sealed class LoginEvent {
  object FieldsValidated : LoginEvent()
}