package com.example.puzzledroidkt

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.bumptech.glide.Glide
import com.example.puzzledroidkt.GestureDetectGridView.OnSwipeListener
import com.example.puzzledroidkt.databinding.ActivityPuzzleBinding
import java.lang.System.currentTimeMillis
import java.util.*

enum class SwipeDirections {
    UP, DOWN, LEFT, RIGHT
}

class PuzzleActivity : AppCompatActivity() {

        companion object {
            private const val TOTAL_COLUMNS = 2
            private const val DIMENSIONS = TOTAL_COLUMNS * TOTAL_COLUMNS

            private var boardColumnWidth = 0
            private var boardColumnHeight = 0
            
            private var imgPath :String = ""

        }

        private val tileListIndexes = mutableListOf<Int>()
        private var initTime : Long = 0
        private var finishTime : Long = 0
        private lateinit var binding : ActivityPuzzleBinding


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

        private fun init() {
            binding.gestureDetectGridView.apply {
                numColumns = TOTAL_COLUMNS
                setOnSwipeListener(object : OnSwipeListener {
                    override fun onSwipe(direction: SwipeDirections, position: Int) {
                        moveTiles(direction, position)

                    }
                })
            }

            tileListIndexes += 0 until DIMENSIONS

        }

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

        private fun setTileBoardDimensions() {
            val observer = binding.gestureDetectGridView.viewTreeObserver
            observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.gestureDetectGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val displayWidth = binding.gestureDetectGridView.measuredWidth
                    val displayHeight = binding.gestureDetectGridView.measuredHeight

                    boardColumnWidth = displayWidth / TOTAL_COLUMNS
                    boardColumnHeight = displayHeight / TOTAL_COLUMNS

                    displayTileBoard()
                }
            })
        }

        /**
         * Used for both init and every time a new swap move is made by the user.
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
        private fun splitImage(imgPath: String):ArrayList<Bitmap>{
            val pieces = ArrayList<Bitmap>(DIMENSIONS)
            val rows = TOTAL_COLUMNS
            val cols = TOTAL_COLUMNS

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
            for (row in 0 until  TOTAL_COLUMNS) {
                var xCoord = 0
                for (col in 0 until  TOTAL_COLUMNS){
                    pieces.add(Bitmap.createBitmap(image, xCoord, yCoord, pieceWidth, pieceHeight))
                    xCoord += pieceWidth
                }
                yCoord += pieceHeight
            }
            return pieces
        }
        private fun displayToast(@StringRes textResId: Int) {
            Toast.makeText(this, getString(textResId), Toast.LENGTH_SHORT).show()
        }

        private fun moveTiles(direction: SwipeDirections, position: Int) {
            // Upper-left-corner tile
            if (position == 0) {
                when (direction) {
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                    else -> displayToast(R.string.invalid_move)
                }
                // Upper-center tiles
            } else if (position > 0 && position < TOTAL_COLUMNS - 1) {
                when (direction) {
                    SwipeDirections.LEFT -> swapTile(position, -1)
                    SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    else -> displayToast(R.string.invalid_move)
                }
                // Upper-right-corner tile
            } else if (position == TOTAL_COLUMNS - 1) {
                when (direction) {
                    SwipeDirections.LEFT -> swapTile(position, -1)
                    SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                    else -> displayToast(R.string.invalid_move)
                }
                // Left-side tiles
            } else if (position > TOTAL_COLUMNS - 1 && position < DIMENSIONS - TOTAL_COLUMNS && position % TOTAL_COLUMNS == 0) {
                when (direction) {
                    SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                    else -> displayToast(R.string.invalid_move)
                }
                // Right-side AND bottom-right-corner tiles
            } else if (position == TOTAL_COLUMNS * 2 - 1 || position == TOTAL_COLUMNS * 3 - 1) {
                when (direction) {
                    SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                    SwipeDirections.LEFT -> swapTile(position, -1)
                    SwipeDirections.DOWN -> {
                        // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                        // right-corner tile.
                        if (position <= DIMENSIONS - TOTAL_COLUMNS - 1) {
                            swapTile(position, TOTAL_COLUMNS)
                        } else {
                            displayToast(R.string.invalid_move)
                        }
                    }
                    else -> displayToast(R.string.invalid_move)
                }
                // Bottom-left corner tile
            } else if (position == DIMENSIONS - TOTAL_COLUMNS) {
                when (direction) {
                    SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    else -> displayToast(R.string.invalid_move)
                }
                // Bottom-center tiles
            } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - TOTAL_COLUMNS) {
                when (direction) {
                    SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                    SwipeDirections.LEFT -> swapTile(position, -1)
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    else -> displayToast(R.string.invalid_move)
                }
                // Center tiles
            } else {
                when (direction) {
                    SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                    SwipeDirections.LEFT -> swapTile(position, -1)
                    SwipeDirections.RIGHT -> swapTile(position, 1)
                    else -> swapTile(position, TOTAL_COLUMNS)
                }
            }
        }

        private fun swapTile(currentPosition: Int, swap: Int) {
            val newPosition = tileListIndexes[currentPosition + swap]
            tileListIndexes[currentPosition + swap] = tileListIndexes[currentPosition]
            tileListIndexes[currentPosition] = newPosition
            displayTileBoard()

            if (isSolved) {
                finishTime = currentTimeMillis() - initTime
                onAlertDialog(binding.root)


            }
        }
    private fun onAlertDialog(view: View) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(view.context)

        // set title
        builder.setTitle("Finalizado!!")

        //set content area
        builder.setMessage("Lo has conseguido.") //TODO: Mostrar los segundos (y minutos si es necesario)

        //set negative button
        builder.setPositiveButton(
            "Volver") { dialog, id ->
            // User clicked Update Now button
            Toast.makeText(this, ((finishTime / 1000)  / 60).toString()+":"+((finishTime / 1000)  % 60).toString(),Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.show()
    }
}
