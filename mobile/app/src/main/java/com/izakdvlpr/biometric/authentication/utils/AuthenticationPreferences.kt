package com.izakdvlpr.biometric.authentication.utils

import android.content.Context

class AuthenticationPreferences(
  private val context: Context
) {
  private val AUTHENTICATION_PREFERENCES_KEY = "authentication"
  private val USERNAME_TO_LOGIN_KEY = "username_to_login"
  private val LOGIN_WITH_BIOMETRIC_KEY = "login_with_biometric"

  private val sharedPreferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_KEY, Context.MODE_PRIVATE)

  fun getLoginWithBiometric(): Boolean {
    return sharedPreferences.getBoolean(LOGIN_WITH_BIOMETRIC_KEY, false)
  }

  fun setLoginWithBiometric(state: Boolean) {
    sharedPreferences.edit().putBoolean(LOGIN_WITH_BIOMETRIC_KEY, state).apply()
  }

  fun getUsernameToLogin(): String? {
    return sharedPreferences.getString(USERNAME_TO_LOGIN_KEY, null)
  }

  fun setUsernameToLogin(username: String) {
    sharedPreferences.edit().putString(USERNAME_TO_LOGIN_KEY, username).apply()
  }
}