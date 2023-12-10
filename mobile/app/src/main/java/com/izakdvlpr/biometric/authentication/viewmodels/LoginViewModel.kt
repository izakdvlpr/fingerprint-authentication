package com.izakdvlpr.biometric.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izakdvlpr.biometric.authentication.events.LoginEvent
import com.izakdvlpr.biometric.authentication.states.LoginState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
  val state by lazy { MutableStateFlow(LoginState()) }
  val event by lazy { MutableSharedFlow<LoginEvent>() }

  fun resetState() {
    state.value = LoginState()
  }

  fun setUsernameValue(usernameValue: String) {
    state.update { it.copy(usernameValue = usernameValue) }

    validateUsername(username = usernameValue)
  }

  private fun updateUsernameError(usernameError: String?) {
    state.update { it.copy(usernameError = usernameError) }
  }

  private fun validateUsername(username: String): Boolean {
    val usernameIsRequired = username.isBlank()

    if (usernameIsRequired) {
      updateUsernameError("O nome de usuário é obrigatório.")

      return true
    }

    updateUsernameError(null)

    return false
  }

  fun setPasswordValue(passwordValue: String) {
    state.update { it.copy(passwordValue = passwordValue) }

    validatePassword(password = passwordValue)
  }

  private fun updatePasswordError(passwordError: String?) {
    state.update { it.copy(passwordError = passwordError) }
  }

  private fun validatePassword(password: String): Boolean {
    val passwordIsRequired = password.isBlank()
    val passwordMinCharacters = password.length < 6

    if (passwordIsRequired) {
      updatePasswordError("A senha é obrigatória.")

      return true
    }

    if (passwordMinCharacters) {
      updatePasswordError("A senha deve ter pelo menos 6 caracteres.")

      return true
    }

    updatePasswordError(null)

    return false
  }

  fun validateFields() {
    viewModelScope.launch {
      val usernameValue = state.value.usernameValue
      val passwordValue = state.value.passwordValue

      val usernameValidation = validateUsername(usernameValue)
      val passwordValidation = validatePassword(passwordValue)

      val validationHasError = listOf(
        usernameValidation,
        passwordValidation
      ).any { it }

      if (validationHasError) {
        return@launch
      }

      event.emit(LoginEvent.FieldsValidated)
    }
  }
}