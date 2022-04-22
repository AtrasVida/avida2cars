package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.widget.AppCompatImageView
import co.atrasvida.avida2cars.R
import kotlinx.coroutines.*

class Car(context: Context) : AppCompatImageView(context) {
    private val mainScope = MainScope()

    var size = 0f

    private var centerX = 0f
    public fun getCenterX() = centerX

    private var centerY = 0f
    public fun getCenterY() = centerY

    var color = 0
        set(value) {
            field = value
            setColorFilter(field, PorterDuff.Mode.MULTIPLY)
        }

    private var roadRunway: RoadRunway =
        RoadRunway.Left // 0 = left , 1 = right


    var offsetFirst = 0f
    var offsetSecond = 0f

    fun init(offsetOfCarInRoad: Float, top: Float, color: Int, positionRunway: RoadRunway) {
        setImageResource(R.mipmap.car)

        this.color = color

        offsetFirst = offsetOfCarInRoad * 1
        offsetSecond = offsetOfCarInRoad * 3

        size = offsetOfCarInRoad

        centerX = if (positionRunway == RoadRunway.Left) offsetFirst else offsetSecond
        centerY = top

        this.roadRunway = positionRunway

    }

    var isMoving = false

    fun changeSide() {
        isMoving = false
        if (roadRunway == RoadRunway.Left) moveToRight() else moveToLeft()
    }

    private fun moveToLeft() {
        if (isMoving) return

        isMoving = true
        roadRunway = RoadRunway.Left

        mainScope.launch {
            while (isMoving && roadRunway == RoadRunway.Left) {
                if (centerX == offsetFirst) {
                    rotation = 0f
                    isMoving = false
                } else {
                    centerX -= 10
                    rotation = -30f
                }
                revalidate()
                delay(10)
            }
        }
    }

    fun moveToRight() {
        if (isMoving) return

        isMoving = true
        roadRunway = RoadRunway.Right

        mainScope.launch {
            while (isMoving && roadRunway == RoadRunway.Right) {
                if (centerX == offsetSecond) {
                    rotation = 0f
                    isMoving = false
                } else {
                    centerX += 10
                    rotation = +30f
                }
                revalidate()
                delay(10)
            }
        }
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