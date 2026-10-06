package com.cfadmin.pro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cfadmin.pro.data.auth.AuthState
import com.cfadmin.pro.data.prefs.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val container = (app as CfAdminApplication).container
    private val prefs = UserPrefs(app)

    val authState: StateFlow<AuthState> = container.authRepository.state

    private val _accountName = MutableStateFlow<String?>(null)
    val accountName: StateFlow<String?> = _accountName.asStateFlow()

    val darkTheme: StateFlow<Boolean> = prefs.darkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val recent: StateFlow<List<String>> = prefs.recent
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val pinned: StateFlow<List<String>> = prefs.pinned
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf("overview", "pages"))

    val domain: StateFlow<String> = prefs.domain
        .stateIn(viewModelScope, SharingStarted.Eagerly, "midominio.com")

    init {
        _accountName.value = container.authRepository.getAccountName()
        viewModelScope.launch {
            if (authState.value is AuthState.LoggedIn && _accountName.value == null) {
                try {
                    val accounts = container.authRepository.api.listAccounts()
                    val first = accounts.result?.firstOrNull()
                    if (first != null) {
                        container.authRepository.saveAccount(first.id, first.name)
                        _accountName.value = first.name
                    }
                } catch (e: Exception) {
                    // Silenciar; se reintentara al abrir Pages
                }
            }
        }
    }

    fun toggleTheme() = viewModelScope.launch {
        prefs.setDarkTheme(!darkTheme.value)
    }

    fun pushRecent(route: String) = viewModelScope.launch {
        val current = recent.value.filter { it != route }
        prefs.setRecent((listOf(route) + current).take(5))
    }

    fun togglePin(route: String) = viewModelScope.launch {
        val current = pinned.value
        prefs.setPinned(if (route in current) current - route else current + route)
    }

    fun setDomain(d: String) = viewModelScope.launch { prefs.setDomain(d) }

    fun logout() {
        container.authRepository.logout()
        _accountName.value = null
    }
}
