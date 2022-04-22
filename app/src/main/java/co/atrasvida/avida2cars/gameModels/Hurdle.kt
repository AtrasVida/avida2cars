package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.graphics.PorterDuff
import androidx.appcompat.widget.AppCompatImageView
import co.atrasvida.avida2cars.R
import java.util.*

class Hurdle(context: Context) : AppCompatImageView(context) {

    var color = 0
        set(value) {
            field = value
            setColorFilter(field, PorterDuff.Mode.MULTIPLY)
        }

    var isScore = false

    var offsetOfCarInRoad = 0f

    private var centerX = 0f
    fun getCenterX() = centerX

    private var centerY = 0f
    fun getCenterY() = centerY
    fun setCenterY(y: Float) {
        centerY = y
    }

    var roadRunway: RoadRunway = RoadRunway.Left

    var size = 0f

    var isUsed = false

    fun refreshIsScore() {
        isScore = Random().nextBoolean()

        setImageResource(if (isScore) R.mipmap.circle else R.mipmap.rect)

        reUse()
    }

    fun refreshTop(top: Float) {
        this.centerY = top

        val position = if (roadRunway == RoadRunway.Left) 1 else 3

        centerX = position * offsetOfCarInRoad

        layout(
            (centerX - size).toInt(),
            (centerY - size).toInt(),
            (centerX + size).toInt(),
            (centerY + size).toInt()
        )
    }

    fun setAsUsed() {
        visibility = INVISIBLE
        isUsed = true
    }

    fun reUse() {
        visibility = VISIBLE
        isUsed = false
    }
}