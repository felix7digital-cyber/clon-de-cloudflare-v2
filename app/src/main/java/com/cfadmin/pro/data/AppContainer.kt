package com.cfadmin.pro.data

import android.content.Context
import com.cfadmin.pro.data.auth.AuthRepository
import com.cfadmin.pro.data.auth.TokenStore
import com.cfadmin.pro.data.repository.PagesRepository

class AppContainer(context: Context) {
    val tokenStore: TokenStore = TokenStore(context)
    val authRepository: AuthRepository = AuthRepository(tokenStore)
    val pagesRepository: PagesRepository = PagesRepository(authRepository)
}
