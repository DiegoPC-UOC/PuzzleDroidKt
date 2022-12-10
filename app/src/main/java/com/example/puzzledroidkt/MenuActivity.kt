package com.example.puzzledroidkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.puzzledroidkt.databinding.ActivityMainBinding
import com.example.puzzledroidkt.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // TODO: Listar imagenes en la carpeta assets/img
        val imgList = binding.root.context.assets.list("/img")
        //for (img in imgList)
        for (img in imgList!!){
            PuzzleImage("file:///android_asset/img/")
        }
        binding.rvPuzzleImages.adapter = PuzzleImagesAdapter(
            //TODO: Por cada imagen en la carpeta, crear un PuzzleImage con la ruta
            listOf(
                PuzzleImage("file:///android_asset/img/photo1.jpg"),
                PuzzleImage("file:///android_asset/img/photo2.jpg"),
                PuzzleImage("file:///android_asset/img/photo3.jpg"),
                PuzzleImage("file:///android_asset/img/photo4.jpg"),
                PuzzleImage("file:///android_asset/img/photo5.jpg")
            )
        ) {
            //TODO: Intent hacia PuzzleActivity pasando la ruta a la imagen
            Toast
                .makeText(this, "hola", Toast.LENGTH_SHORT)
                .show()
        }

        //TODO: Binding ranking recycledview

        //TODO: Boton camara

        //TODO: Boton imagen aleatoria

        //TODO: Boton musica

        //TODO: Menu ActionBar

    }
}