package com.cfadmin.pro.data.auth

import com.cfadmin.pro.data.api.CloudflareApi
import com.cfadmin.pro.data.api.CloudflareClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    data object Loading : AuthState()
    data object LoggedOut : AuthState()
    data class LoggedIn(val token: String) : AuthState()
}

class AuthRepository(private val tokenStore: TokenStore) {

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private var _api: CloudflareApi? = null

    val api: CloudflareApi
        get() = _api ?: error("No hay API client. Inicia sesion primero.")

    init {
        val stored = tokenStore.getToken()
        if (!stored.isNullOrBlank()) {
            _api = CloudflareClient.create(stored)
            _state.value = AuthState.LoggedIn(stored)
        } else {
            _state.value = AuthState.LoggedOut
        }
    }

    suspend fun login(rawToken: String): Result<Unit> {
        val token = rawToken.trim()
        if (token.isBlank()) {
            return Result.failure(IllegalArgumentException("Token vacio"))
        }
        return try {
            val newApi = CloudflareClient.create(token)
            val response = newApi.verifyToken()
            if (!response.success) {
                val msg = response.errors.firstOrNull()?.message
                    ?: "Token rechazado por Cloudflare"
                return Result.failure(Exception(msg))
            }
            _api = newApi
            tokenStore.saveToken(token)
            _state.value = AuthState.LoggedIn(token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenStore.clear()
        _api = null
        _state.value = AuthState.LoggedOut
    }

    fun saveAccount(id: String, name: String) {
        tokenStore.saveAccountId(id)
        tokenStore.saveAccountName(name)
    }

    fun getAccountId(): String? = tokenStore.getAccountId()
    fun getAccountName(): String? = tokenStore.getAccountName()
}
