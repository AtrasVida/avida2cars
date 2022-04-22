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

    var positionOfCarsInRoad : PositionOfCarsInRoad = PositionOfCarsInRoad.Left // 0 = left , 1 = right
        set(value) {
            field = value
            changeSide()
        }


    var offsetFirst = 0f
    var offsetSecond = 0f

    fun init(offsetOfCarInRoad: Float, top: Float, color: Int, position: PositionOfCarsInRoad) {
        setImageResource(R.mipmap.car)

        this.color = color

        offsetFirst = offsetOfCarInRoad * 1
        offsetSecond = offsetOfCarInRoad * 3

        size = offsetOfCarInRoad

        centerX = if (position == PositionOfCarsInRoad.Left) offsetFirst else offsetSecond
        centerY = top

        this.positionOfCarsInRoad = position

    }

    lateinit var thread: Thread

    var isMoving = false

    private fun changeSide() {

        if (isMoving) return

        isMoving = true

        thread = Thread {
            while (isMoving) {
                if (positionOfCarsInRoad == PositionOfCarsInRoad.Left) {
                    if (centerX == offsetFirst) {
                        rotation = 0f
                        isMoving = false

                    } else {
                        centerX -= 10
                        rotation = -30f
                    }
                } else {

                    if (centerX == offsetSecond) {
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