package com.example.puzzledroidkt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.puzzledroidkt.databinding.ActivityMenuBinding
import kotlin.concurrent.thread

class MenuActivity : AppCompatActivity() {
    val REQUEST_CODE = 200
    var imgPathList :ArrayList<String> = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.bAleatorio.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                openGalleryForImages()
            }
        })

        //TODO: Boton musica

        //TODO: Menu ActionBar

    }
    private fun openGalleryForImages() {

            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){

            // if multiple images are selected
            if (data?.getClipData() != null) {
                var count = data.clipData?.itemCount
                imgPathList.clear()
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    imgPathList.add(imageUri.toString())
                }
            } else if (data?.getData() != null) {
                // if single image is selected
                var imageUri: Uri = data.data!!
                imgPathList.clear()
                imgPathList.add(imageUri.toString())
            }
            for (img in imgPathList) {
                val intent = Intent(applicationContext, PuzzleActivity::class.java)
                intent.putExtra("imgPath", img)
                startActivity(intent)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onResume() {
        super.onResume()
        startService(Intent(this, MyMusicService::class.java))
    }

    override fun onPause() {
        super.onPause()
        stopService(Intent(this, MyMusicService::class.java))
    }
}