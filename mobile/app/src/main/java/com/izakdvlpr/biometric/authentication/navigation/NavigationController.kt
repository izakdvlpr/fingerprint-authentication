package com.izakdvlpr.biometric.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.izakdvlpr.biometric.authentication.events.AuthenticationEvent
import com.izakdvlpr.biometric.authentication.screens.ProfileScreen
import com.izakdvlpr.biometric.authentication.screens.LoginScreen
import com.izakdvlpr.biometric.authentication.viewmodels.AuthenticationViewModel
import com.izakdvlpr.biometric.authentication.viewmodels.LoginViewModel

@Composable
fun NavigationController() {
  val navController = rememberNavController()

  val authenticationViewModel: AuthenticationViewModel = viewModel()
  val loginViewModel: LoginViewModel = viewModel()

  LaunchedEffect(key1 = Unit) {
    authenticationViewModel.event.collect { event ->
      when (event) {
        is AuthenticationEvent.UserLoggedIn -> {
          loginViewModel.resetState()

          navController.navigate(NavigationRoutes.profile)
        }

        is AuthenticationEvent.UserLoggedOut -> {
          navController.navigate(NavigationRoutes.login)
        }
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = NavigationRoutes.login
  ) {
    composable(route = NavigationRoutes.login) {
      LoginScreen(
        navController = navController,
        authenticationViewModel = authenticationViewModel,
        loginViewModel = loginViewModel
      )
    }

    composable(route = NavigationRoutes.profile) {
      ProfileScreen(authenticationViewModel = authenticationViewModel)
    }
  }
}