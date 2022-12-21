package com.example.puzzledroidkt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * Mantiene activo splash screen
         */
        screenSplash.setKeepOnScreenCondition {true}
        //Si el servicio no esta activo
        //Activa el servicio
        //TODO: Confirmar permisos

        //TODO: Comprobar login - para la 3a parte

        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()

    }
}