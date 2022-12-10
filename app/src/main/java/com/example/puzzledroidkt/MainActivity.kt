package com.example.puzzledroidkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screenSplash.setKeepOnScreenCondition {true}

        //TODO: Confirmar permisos

        //TODO: Comprobar login - para la 3a parte

        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()

    }
}