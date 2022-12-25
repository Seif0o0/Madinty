package com.madinaty.app.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.chibatching.kotpref.Kotpref
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.madinaty.app.R
import com.madinaty.app.kot_pref.UserInfo
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

        AppLocale.supportedLocales = listOf(
            Locale(getString(R.string.arabic_value)),
            Locale(getString(R.string.english_value))
        )
        AppLocale.desiredLocale = Locale(UserInfo.appLanguage)
        ViewPump.init(RewordInterceptor)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(AppLocale.wrap(base))
    }

    override fun getResources(): Resources {
        return AppLocale.wrap(baseContext).resources
    }

}