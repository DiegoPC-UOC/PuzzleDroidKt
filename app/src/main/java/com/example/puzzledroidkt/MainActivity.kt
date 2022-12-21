package com.example.puzzledroidkt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
enum class ProviderType{
    GOOGLE
}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * Mantiene activo splash screen
         */
        screenSplash.setKeepOnScreenCondition {true}

        val sh = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        if(!sh.getBoolean("isLogin",false)){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}