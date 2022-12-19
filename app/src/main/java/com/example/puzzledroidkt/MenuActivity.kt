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
        if (!MyMusicService.isRuning) {
            startService(Intent(this, MyMusicService::class.java))
        }
        val imgList: Array<String>? = binding.root.context.assets.list("img/")
        val imgs: ArrayList<PuzzleImage> = ArrayList<PuzzleImage>()
        val puzzleImagesAdapter = PuzzleImagesAdapter(emptyList()) { puzzleImage ->
            Toast //Variable de Prueba para comprobar la informacion para la siguiente activity
                .makeText(this, puzzleImage.image, Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(applicationContext, PuzzleActivity::class.java)
            intent.putExtra("imgPath", puzzleImage.image)
            startActivity(intent)
        }
        binding.rvPuzzleImages.adapter = puzzleImagesAdapter
        thread {
            if (imgList != null) {
                for (img in imgList)
                    imgs.add(PuzzleImage("file:///android_asset/img/$img"))
            }
            runOnUiThread{
                puzzleImagesAdapter.puzzleImages = imgs
                puzzleImagesAdapter.notifyDataSetChanged()
            }
        }


        //TODO: Binding ranking recycledview

        //TODO: Boton camara

        //TODO: Boton imagen aleatoria

        //TODO: Boton musica

        //TODO: Menu ActionBar

    }
    override fun onResume() {
        super.onResume()
        if (!MyMusicService.isRuning) {
            startService(Intent(this,MyMusicService::class.java))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyMusicService::class.java))
    }
}