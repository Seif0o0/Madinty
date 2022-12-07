package com.madinaty.app.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.chibatching.kotpref.Kotpref
import dagger.hilt.android.HiltAndroidApp
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import java.util.*

@HiltAndroidApp
class MadintyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)

        AppLocale.supportedLocales = listOf(Locale("ar"), Locale("en"))
        ViewPump.init(RewordInterceptor)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(AppLocale.wrap(base))
    }
    override fun getResources(): Resources {
        return AppLocale.wrap(baseContext).resources
    }

}