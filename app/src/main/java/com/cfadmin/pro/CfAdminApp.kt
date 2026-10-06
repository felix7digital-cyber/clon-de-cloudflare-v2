package com.cfadmin.pro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cfadmin.pro.data.auth.AuthState
import com.cfadmin.pro.ui.components.AppBottomBar
import com.cfadmin.pro.ui.components.AppTopBar
import com.cfadmin.pro.ui.components.FabMenu
import com.cfadmin.pro.ui.components.ToastHost
import com.cfadmin.pro.ui.components.ToastHostState
import com.cfadmin.pro.ui.nav.CfNavGraph
import com.cfadmin.pro.ui.nav.Section
import com.cfadmin.pro.ui.screens.login.LoginScreen

@Composable
fun CfAdminApp(appViewModel: AppViewModel = viewModel()) {
    val authState by appViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is AuthState.LoggedOut -> {
            LoginScreen()
        }
        is AuthState.LoggedIn -> {
            MainShell(appViewModel)
        }
    }
}

@Composable
private fun MainShell(appViewModel: AppViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Section.OVERVIEW.route

    val darkTheme by appViewModel.darkTheme.collectAsState()
    val recent by appViewModel.recent.collectAsState()
    val pinned by appViewModel.pinned.collectAsState()
    val accountName by appViewModel.accountName.collectAsState()

    val toastHostState = remember { ToastHostState() }
    var fabMenuOpen by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopBar(
                    accountName = accountName,
                    isDark = darkTheme,
                    onToggleTheme = { appViewModel.toggleTheme() },
                    onLogout = { appViewModel.logout() }
                )
            },
            bottomBar = {
                AppBottomBar(
                    currentRoute = currentRoute,
                    recent = recent,
                    pinned = pinned,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Section.OVERVIEW.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            },
            floatingActionButton = {
                val rotation by animateFloatAsState(
                    targetValue = if (fabMenuOpen) 135f else 0f,
                    label = "fabRotation"
                )
                FloatingActionButton(
                    onClick = { fabMenuOpen = !fabMenuOpen },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.rotate(rotation)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Menu")
                }
            }
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Aquí pasamos el MISMO navController al NavGraph
                CfNavGraph(navController = navController)
            }
        }

        AnimatedVisibility(
            visible = fabMenuOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { fabMenuOpen = false }
            )
        }

        AnimatedVisibility(
            visible = fabMenuOpen,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 88.dp)
        ) {
            FabMenu(
                currentRoute = currentRoute,
                pinned = pinned,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Section.OVERVIEW.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    fabMenuOpen = false
                },
                onTogglePin = { appViewModel.togglePin(it) }
            )
        }

        ToastHost(toastHostState)
    }
}