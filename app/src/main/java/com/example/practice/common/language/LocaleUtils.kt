package com.example.practice.common.language

import android.content.Context
import android.os.Build
import java.util.*

fun Context.updateLocale(locale: Locale): Context {
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createConfigurationContext(config)
    } else {
        resources.updateConfiguration(config, resources.displayMetrics)
        this
    }
}
