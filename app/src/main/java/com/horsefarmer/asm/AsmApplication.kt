package com.horsefarmer.asm

import android.app.Application
import android.content.Context

class AsmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @JvmStatic
        lateinit var context: Context
    }
}