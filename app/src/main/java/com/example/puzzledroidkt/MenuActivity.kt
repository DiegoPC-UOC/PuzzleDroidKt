package com.example.puzzledroidkt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.puzzledroidkt.databinding.ActivityMenuBinding
import kotlin.concurrent.thread

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Lista de las imagenes dentro de la carpeta assets
        val imgList: Array<String>? = binding.root.context.assets.list("img/")
        //Lista de objetos PuzzleImage que se mostrara en el recyclerView
        val imgs: ArrayList<PuzzleImage> = ArrayList()
        //Asigna a cada elemento del recyclerView el comportamieto
        val puzzleImagesAdapter = PuzzleImagesAdapter(emptyList()) { puzzleImage ->
            Toast //Variable de Prueba para comprobar la informacion para la siguiente activity
                .makeText(this, puzzleImage.image, Toast.LENGTH_SHORT)
                .show()
            //Cambio a la actividad del puzzle pasando la direccion de la imagen a usar
            val intent = Intent(applicationContext, PuzzleActivity::class.java)
            intent.putExtra("imgPath", puzzleImage.image)
            startActivity(intent)
        }
        //RecyclerView
        binding.rvPuzzleImages.adapter = puzzleImagesAdapter
        //Crea un objeto para el adapter por cada imagen en la carpeta assets
        //Asignandole la direccion de la imagen
        thread {
            if (imgList != null) {
                for (img in imgList)
                    imgs.add(PuzzleImage("file:///android_asset/img/$img"))
            }
            runOnUiThread{
                //Asigna los objetos al adapter
                puzzleImagesAdapter.puzzleImages = imgs
                //Notifica los cambios al adapter para refrescar el recyclerView
                puzzleImagesAdapter.notifyDataSetChanged()
            }
        }
        //TODO: Binding ranking recycledview

        }
}