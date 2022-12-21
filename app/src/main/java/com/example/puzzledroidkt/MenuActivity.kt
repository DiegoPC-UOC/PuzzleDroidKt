package com.example.puzzledroidkt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.puzzledroidkt.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}