package com.cfadmin.pro

import android.app.Application
import com.cfadmin.pro.data.AppContainer

class CfAdminApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
