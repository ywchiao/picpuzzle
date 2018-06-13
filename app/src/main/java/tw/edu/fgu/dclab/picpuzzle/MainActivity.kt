
package tw.edu.fgu.dclab.picpuzzle

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView

import java.io.IOException
import java.util.*

import kotlinx.android.synthetic.main.activity_main.*

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

data class Grid(val x: Float, val y: Float)

class MainActivity : AppCompatActivity() {
    private var bitmapTiles = arrayOfNulls<Bitmap>(9)
    private val imageTiles = arrayOfNulls<ImageView>(9)
    private val gridCells = arrayOfNulls<Grid>(9)

    private val tileMapping = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBitmapTiles()
        initImageTiles()

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (gridCells[0] == null) {
                    for (i in 0..8) {
                        val view = getViewByIdx(i)
                        gridCells[i] = Grid(view.x, view.y)
                    }
                }

                for (i in 0..10) {
                    val j = (0..9).random()

                    val k = tileMapping[j]
                    tileMapping[j] = tileMapping[(j + 2) % 9]
                    tileMapping[(j + 2) % 9] = k
                }

                rearrangeTiles()
            }
        })

        rearrangeTiles()
    }

    private fun findCell(x: Float, y: Float): Int {
        for ((i, cell) in gridCells.withIndex()) {
            val offY = 128
            val offX = 128
            val cellX = cell?.x ?: 0f
            val cellY = cell?.y ?: 0f

            if ((x > (cellX - offX)) && (x < (cellX + offX))) {
                if ((y > (cellY - offY)) && (y < cellY + offY)) {
                    return i
                }
            }
        }

        return -1
    }

    // Custom method to get assets folder image as bitmap
    private fun getBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(assets.open(fileName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getViewByIdx(id: Int): ImageView {
        val viewIDs = intArrayOf(R.id.imageView0, R.id.imageView1, R.id.imageView2, R.id.imageView3,
                R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8)

        return findViewById(viewIDs[id])
    }

    private fun initBitmapTiles() {
        val bitmap: Bitmap? = getBitmapFromAssets("fgu.jpg")

        val width: Int = (bitmap?.width ?: 0) / 3
        val height: Int = (bitmap?.height ?: 0) / 3

        for (i in bitmapTiles.indices) {
            val x: Int = (i % 3) * width
            val y: Int = (i / 3) * height

            bitmapTiles[i] = Bitmap.createBitmap(bitmap, x, y, width, height)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initImageTiles() {
        for (idx in 0..8) {
            val imageView = getViewByIdx(idx)

            imageTiles[idx] = imageView

            imageView.setOnTouchListener(object : OnTouchListener {
                internal var cellStart = 0 // Record the Cell index when the pressed down.
                internal var pointDown = PointF() // Record Mouse Position when pressed down.
                internal var pointStart = PointF() // Record Start Position of 'img'

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (gridCells[0] != null) {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                pointDown.set(event.x, event.y)
                                pointStart.set(imageView.x, imageView.y)
                                cellStart = findCell(imageView.x, imageView.y)
                            }

                            MotionEvent.ACTION_MOVE -> {
                                imageView.bringToFront()

                                imageView.x = (pointStart.x + event.x - pointDown.x)
                                imageView.y = (pointStart.y + event.y - pointDown.y)
                                pointStart.set(imageView.x, imageView.y)
                            }

                            MotionEvent.ACTION_UP -> {
                                val cellTarget = findCell(imageView.x, imageView.y)

                                if (cellTarget >= 0) {
                                    val cell: Int = tileMapping[cellTarget]

                                    tileMapping[cellTarget] = tileMapping[cellStart]
                                    tileMapping[cellStart] = cell

                                    rearrangeTiles()
                                }

                                imageView.x = gridCells[cellStart]?.x ?: 0f
                                imageView.y = gridCells[cellStart]?.y ?: 0f
                            }
                            else -> {
                            }
                        }
                    }// Nothing have to do

                    return true
                }
            })
        }
    }

    private fun rearrangeTiles() {
        for ((i, tile) in imageTiles.withIndex()) {
            val j = tileMapping[i]

            tile?.setImageBitmap(bitmapTiles[j])
        }
    }
}