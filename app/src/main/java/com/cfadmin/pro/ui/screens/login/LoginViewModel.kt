package com.cfadmin.pro.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cfadmin.pro.CfAdminApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val token: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val showToken: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val authRepository = (app as CfAdminApplication).container.authRepository

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onTokenChange(value: String) {
        _state.update { it.copy(token = value, error = null) }
    }

    fun toggleShowToken() {
        _state.update { it.copy(showToken = !it.showToken) }
    }

    fun submit() {
        val current = _state.value
        if (current.loading) return

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = authRepository.login(current.token)
            result.fold(
                onSuccess = {
                    _state.update { it.copy(loading = false, error = null) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }
}
