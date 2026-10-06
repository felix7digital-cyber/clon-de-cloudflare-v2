package com.cfadmin.pro.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStore(context: Context) {

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "cf_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveAccountId(id: String) {
        prefs.edit().putString(KEY_ACCOUNT, id).apply()
    }

    fun getAccountId(): String? = prefs.getString(KEY_ACCOUNT, null)

    fun saveAccountName(name: String) {
        prefs.edit().putString(KEY_ACCOUNT_NAME, name).apply()
    }

    fun getAccountName(): String? = prefs.getString(KEY_ACCOUNT_NAME, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "api_token"
        const val KEY_ACCOUNT = "account_id"
        const val KEY_ACCOUNT_NAME = "account_name"
    }
}
