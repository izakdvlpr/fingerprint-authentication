package com.izakdvlpr.biometric.authentication.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.izakdvlpr.biometric.authentication.events.AuthenticationEvent
import com.izakdvlpr.biometric.authentication.states.AuthenticationState
import com.izakdvlpr.biometric.authentication.states.LoginState
import com.izakdvlpr.biometric.authentication.utils.AuthenticationPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel constructor (
  application: Application
) : AndroidViewModel(application) {
  @SuppressLint("StaticFieldLeak")
  private val context = application as Context

  val state by lazy { MutableStateFlow(AuthenticationState()) }
  val event by lazy { MutableSharedFlow<AuthenticationEvent>() }

  val authenticationPreferences = AuthenticationPreferences(context)

  init {
    setLoginWithBiometric(authenticationPreferences.getLoginWithBiometric())
  }

  fun setLoginWithBiometric(loginWithBiometricValue: Boolean) {
    state.update { it.copy(loginWithBiometricValue = loginWithBiometricValue) }

    authenticationPreferences.setLoginWithBiometric(loginWithBiometricValue)
  }

  fun signIn(username: String, password: String? = null) {
    viewModelScope.launch {
      authenticationPreferences.setUsernameToLogin(username)

      event.emit(AuthenticationEvent.UserLoggedIn)
    }
  }

  fun signOut() {
    viewModelScope.launch {
      event.emit(AuthenticationEvent.UserLoggedOut)
    }
  }
}