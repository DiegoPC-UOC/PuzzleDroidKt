package com.example.puzzledroidkt

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.puzzledroidkt.GestureDetectGridView.OnSwipeListener
import com.example.puzzledroidkt.databinding.ActivityPuzzleBinding
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.*


enum class SwipeDirections {
    UP, DOWN, LEFT, RIGHT
}

class PuzzleActivity : AppCompatActivity() {

    private val columns = 3
    private val dimensions = columns * columns
    private var boardColumnWidth = 0
    private var boardColumnHeight = 0
    private var imgPath :String = ""
    private val tileListIndexes = mutableListOf<Int>()
    private var initTime : Long = 0
    private var finishTime : Long = 0
    private lateinit var binding : ActivityPuzzleBinding
    private lateinit var mp : MediaPlayer
    private val isSolved: Boolean
        get() {
            var solved = false
            for (i in tileListIndexes.indices) {
                if (tileListIndexes[i] == i) {
                    solved = true
                } else {
                    solved = false
                    break
                }
            }
            return solved
        }
    /**
     * Comportamiento del programa onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgPath = intent.getStringExtra("imgPath")!!
        init()
        scrambleTileBoard()
        setTileBoardDimensions()
        initTime = currentTimeMillis()

    }
    /**
     * Comportamiento del programa onResume
     */
    override fun onResume() {
        super.onResume()
        if (MyMusicService.isRuning)
            startService(Intent(this, MyMusicService::class.java))
    }
    /**
     * Comportamiento del programa onPause
     */
    override fun onPause() {
        super.onPause()
        if (MyMusicService.isRuning)
            stopService(Intent(this, MyMusicService::class.java))
    }

    /**
     * Asigna las piezas al layout
     */
    private fun init() {
        binding.gestureDetectGridView.apply {
            numColumns = columns
            setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(direction: SwipeDirections, position: Int) {
                    lifecycleScope.launch{
                        tileSound()
                    }
                    moveTiles(direction, position)
                }
            })
        }
        tileListIndexes += 0 until dimensions
    }
    /**
     * Reproduce el sonido del movimiento de las piezas
     */
    private fun tileSound(){
        mp = MediaPlayer.create(binding.root.context,R.raw.arrow)
        mp.setOnPreparedListener { mp.start() }
        mp.setOnCompletionListener { mp.release() }
    }
    /**
     * Mezcla las piezas del puzle para el layout
     */
    private fun scrambleTileBoard() {
        var index: Int
        var tempIndex: Int
        val random = Random()

        for (i in tileListIndexes.size - 1 downTo 1) {
            index = random.nextInt(i + 1)
            tempIndex = tileListIndexes[index]
            tileListIndexes[index] = tileListIndexes[i]
            tileListIndexes[i] = tempIndex
        }
    }
    /**
     * Determina la dimension del tablero
     */
    private fun setTileBoardDimensions() {
        val observer = binding.gestureDetectGridView.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.gestureDetectGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val displayWidth = binding.gestureDetectGridView.measuredWidth
                val displayHeight = binding.gestureDetectGridView.measuredHeight

                boardColumnWidth = displayWidth / columns
                boardColumnHeight = displayHeight / columns

                displayTileBoard()
            }
        })
    }
    /**
     * Muestra por las piezas en el layout
     */
    private fun displayTileBoard() {
        val tileImages = mutableListOf<ImageView>()
        var tileImage: ImageView
        val pieces: ArrayList<Bitmap> = splitImage(imgPath)

        tileListIndexes.forEach { i ->
            tileImage = ImageView(this)
            tileImage.setImageBitmap(pieces[i])
            tileImages.add(tileImage)
        }
        binding.gestureDetectGridView.adapter = TileImageAdapter(tileImages, boardColumnWidth, boardColumnHeight)
    }
    /**
     * Divide la imagen varias partes
     * Recibe la direccion de la imagen a dividir
     * Devuelve un ArrayList con los diferentes Bitmap de la imagen
     */
    private fun splitImage(imgPath: String):ArrayList<Bitmap>{
        val pieces = ArrayList<Bitmap>(dimensions)
        val rows = columns
        val cols = columns

        Glide
            .with(this)
            .load(imgPath)
            .into(binding.imageView)

        val image : Bitmap = binding.imageView.drawToBitmap()

        val w = image.width
        val h = image.height

        val pieceWidth = w/ rows
        val pieceHeight = h/ cols

        var yCoord = 0
        for (row in 0 until  columns) {
            var xCoord = 0
            for (col in 0 until  columns){
                pieces.add(Bitmap.createBitmap(image, xCoord, yCoord, pieceWidth, pieceHeight))
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pieces
    }
    /**
     * Muestra un Toast por pantalla
     * Recibe el texto a mostrar
     */
    private fun displayToast(@StringRes textResId: Int) {
        Toast.makeText(this, getString(textResId), Toast.LENGTH_SHORT).show()
    }
    /**
     * Comprueba el movimiento de las piezas y llama a la funcion de cambio de posiciion
     * Recibe las direcciones predefinidas y la posicion de la pieza a mover
     */
    private fun moveTiles(direction: SwipeDirections, position: Int) {
        // Upper-left-corner tile
        if (position == 0) {
            when (direction) {
                SwipeDirections.RIGHT -> swapTile(position, 1)
                SwipeDirections.DOWN -> swapTile(position, columns)
                else -> displayToast(R.string.invalid_move)
            }
            // Upper-center tiles
        } else if (position > 0 && position < columns - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> swapTile(position, columns)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
            // Upper-right-corner tile
        } else if (position == columns - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> swapTile(position, columns)
                else -> displayToast(R.string.invalid_move)
            }
            // Left-side tiles
        } else if (position > columns - 1 && position < dimensions - columns && position % columns == 0) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -columns)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                SwipeDirections.DOWN -> swapTile(position, columns)
                else -> displayToast(R.string.invalid_move)
            }
            // Right-side AND bottom-right-corner tiles
        } else if (position == columns * 2 - 1 || position == columns * 3 - 1) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -columns)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> {
                    // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                    // right-corner tile.
                    if (position <= dimensions - columns - 1) {
                        swapTile(position, columns)
                    } else {
                        displayToast(R.string.invalid_move)
                    }
                }
                else -> displayToast(R.string.invalid_move)
            }
            // Bottom-left corner tile
        } else if (position == dimensions - columns) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -columns)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
            // Bottom-center tiles
        } else if (position < dimensions - 1 && position > dimensions - columns) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -columns)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
            // Center tiles
        } else {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -columns)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> swapTile(position, columns)
            }
        }
    }

    /**
     * Intercambia la informacion de la posicion de las piezas
     * Recibe la posicion actual de la pieza y la posicion a cambiar
     */
    private fun swapTile(currentPosition: Int, swap: Int) {
        val newPosition = tileListIndexes[currentPosition + swap]
        tileListIndexes[currentPosition + swap] = tileListIndexes[currentPosition]
        tileListIndexes[currentPosition] = newPosition
        displayTileBoard()

        if (isSolved) {
            finishTime = currentTimeMillis() - initTime
            //addToCalendar(finishTime)
            onAlertDialog(binding.root)
        }
    }
    /**
     * Muestra un aiálogo indicando el tiempo tardado
     * Recibe una vista para usar su contexto
     */
    private fun onAlertDialog(view: View) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(view.context)

        // set title
        builder.setTitle("Finalizado!!")
        var min = ((finishTime / 1000)  / 60).toString()
        if (min.length<2)
            min = "0$min"
        var sec = ((finishTime / 1000)  % 60).toString()
        if (sec.length<2)
            sec = "0$sec"
        //set content area
        builder.setMessage("Lo has conseguido.\nHas tardado: $min:$sec ")

        //set negative button
        builder.setPositiveButton(
            "Volver") { _, _ ->
//            if(finishTime<60000) {
//                showNotification()
//            }
            // User clicked Update Now button
            //Toast.makeText(this, "$finishTime",Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.show()
    }
//    fun showNotification() {
//        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("YOUR_CHANNEL_ID",
//                "YOUR_CHANNEL_NAME",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            channel.description = "YOUR_NOTIFICATION_CHANNEL_DESCRIPTION"
//            mNotificationManager.createNotificationChannel(channel)
//        }
//        val mBuilder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
//            .setSmallIcon(R.mipmap.ic_launcher) // notification icon
//            .setContentTitle("Wow, que rápido") // title for notification
//            .setContentText("Has tardado menos de 1 minuto!!!")// message for notification
//            .setAutoCancel(true) // clear notification after click
//        val intent = Intent(applicationContext, PuzzleActivity::class.java)
//        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        mBuilder.setContentIntent(pi)
//        mNotificationManager.notify(0, mBuilder.build())
//    }
//    private fun addToCalendar(finishTime:Long){
//        val cr = contentResolver
//        val values = ContentValues()
//
//        values.put(CalendarContract.Events.CALENDAR_ID, 3);
//        values.put(CalendarContract.Events.TITLE, "Nueva Puntuacion")
//        values.put(CalendarContract.Events.DESCRIPTION, finishTime.toString())
//        values.put(CalendarContract.Events.DTSTART, currentTimeMillis())
//        values.put(CalendarContract.Events.DTEND, currentTimeMillis())
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid")
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        var uri1 : Uri? = Uri.parse("content://com.android.calendar/events")
//        var uri : Uri? = uri1?.let { binding.root.context.contentResolver.insert(it, values) }
//        Log.e("RemindersTest", uri.toString());
//    }
}
