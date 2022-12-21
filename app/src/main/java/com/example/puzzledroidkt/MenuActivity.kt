package com.example.puzzledroidkt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.puzzledroidkt.databinding.ActivityMenuBinding
import java.io.File
import kotlin.concurrent.thread


class MenuActivity : AppCompatActivity() {
    //private val musicCode = 300
    private val galleryCode = 200
    private val cameraCode = 100
    private var selectedImgPathList :ArrayList<String> = ArrayList()
    private lateinit var cameraImgPath : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startService(Intent(this,MyMusicService::class.java))

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

        //Comportamiento boton camara
        binding.bCamara.setOnClickListener { openCamara() }
        //Comportamiento boton seleccion imagen
        binding.bAleatorio.setOnClickListener { openGalleryForImages() }
//        //Boton musica
//        binding.bMusica.setOnClickListener { selectMusic() }
    }
//    private fun selectMusic(){
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "audio/*"
//        startActivityForResult(intent, musicCode)
//    }
    private fun createImageFile():File{
        val fname = "img_${System.currentTimeMillis()}"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(fname, ".jpg",storageDir)
        cameraImgPath = image.absolutePath
        return image
    }
    private fun openCamara(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Solo Funciona en mi movil
        // if(pictureIntent.resolveActivity(getPackageManager()) != null){
        val photoFile : File = createImageFile()
        val photoUri : Uri =
            FileProvider.getUriForFile(this,
                "com.example.puzzledroidkt.provider",photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(pictureIntent, cameraCode)
        //}
    }
    private fun openGalleryForImages() {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, galleryCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == galleryCode){

            // if multiple images are selected
            if (data?.clipData != null) {
                val count = data.clipData?.itemCount
                selectedImgPathList.clear()
                for (i in 0 until count!!) {
                    val imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    selectedImgPathList.add(imageUri.toString())
                }
            } else if (data?.data != null) {
                // if single image is selected
                val imageUri: Uri = data.data!!
                selectedImgPathList.clear()
                selectedImgPathList.add(imageUri.toString())
            }
            for (img in selectedImgPathList) {
                val intent = Intent(applicationContext, PuzzleActivity::class.java)
                intent.putExtra("imgPath", img)
                startActivity(intent)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraCode){
            val intent = Intent(applicationContext, PuzzleActivity::class.java)
            intent.putExtra("imgPath", cameraImgPath)
            startActivity(intent)
        }
//        if (resultCode == Activity.RESULT_OK && requestCode == musicCode){
//            val musicPath = data?.data.toString()
//            Log.e("Directorio", musicPath);
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.music_switch -> {
                if (MyMusicService.isRuning) {
                    stopService(Intent(this, MyMusicService::class.java))
                    MyMusicService.isRuning = false
                    true
                } else {
                    startService(Intent(this, MyMusicService::class.java))
                    MyMusicService.isRuning = true
                    true
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        if (MyMusicService.isRuning)
            startService(Intent(this, MyMusicService::class.java))
    }

    override fun onPause() {
        super.onPause()
        if (MyMusicService.isRuning)
            stopService(Intent(this, MyMusicService::class.java))
    }
}