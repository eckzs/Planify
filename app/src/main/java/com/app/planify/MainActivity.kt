package com.app.planify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.app.planify.logic.utils.AppPrefs
import com.app.planify.logic.utils.AppSettings
import com.app.planify.logic.utils.EmailLinkAuthHandler
import com.app.planify.ui.theme.PlanifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPrefs.load(this)
        EmailLinkAuthHandler.handleIntent(this, intent)
        enableEdgeToEdge()
        setContent {
            val isDark = AppSettings.isDarkTheme ?: isSystemInDarkTheme()
            val baseDensity = LocalDensity.current
            val fontScale = AppSettings.fontScale.scale

            CompositionLocalProvider(
                LocalDensity provides Density(baseDensity.density, fontScale)
            ) {
                PlanifyTheme(darkTheme = isDark) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        EmailLinkAuthHandler.handleIntent(this, intent)
    }
}
