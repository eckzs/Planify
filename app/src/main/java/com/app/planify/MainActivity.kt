package com.app.planify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.planify.logic.utils.EmailLinkAuthHandler
import com.app.planify.ui.theme.PlanifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmailLinkAuthHandler.handleIntent(this, intent)
        enableEdgeToEdge()
        setContent {
            PlanifyTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        EmailLinkAuthHandler.handleIntent(this, intent)
    }
}
