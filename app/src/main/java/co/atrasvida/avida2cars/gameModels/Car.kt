package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.widget.AppCompatImageView
import co.atrasvida.avida2cars.R

class Car(context: Context) : AppCompatImageView(context) {

    var size = 0f

    var centerX = 0f
        set(value) {
            field = value
            handler.post { revalidate() }

        }


    var centerY = 0f

    var color = 0
        set(value) {
            field = value
            setColorFilter(field, PorterDuff.Mode.MULTIPLY)
        }

    var side = 0 // 0 = left , 1 = right
        set(value) {
            field = value
            changeSide()
        }


    var place1 = 0f
    var place2 = 0f

    fun init(widthFactor: Float, top: Float, color: Int, side: Int) {
        setImageResource(R.mipmap.car)

        this.color = color

        place1 = widthFactor * 1
        place2 = widthFactor * 3

        size = widthFactor

        centerX = if (side == 0) place1 else place2
        centerY = top

        this.side = side

    }

    lateinit var thread: Thread

    var isMoving = false

    private fun changeSide() {

        if (isMoving) return

        isMoving = true

        thread = Thread {
            while (isMoving) {
                if (side == 0) {
                    if (centerX == place1) {
                        rotation = 0f
                        isMoving = false

                    } else {
                        centerX -= 10
                        rotation = -30f
                    }
                } else {

                    if (centerX == place2) {
                        rotation = 0f
                        isMoving = false
                    } else {
                        centerX += 10
                        rotation = +30f
                    }
                }

                try {
                    Thread.sleep(10)
                } catch (e: Exception) {

                }
            }
        }

        thread.start()

    }

    private fun revalidate() {
        layout(
            (centerX - size / 2).toInt(),
            (centerY - size / 2).toInt(),
            (centerX + size / 2).toInt(),
            (centerY + size / 2).toInt()
        )
    }
}