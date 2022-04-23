package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

class GameRoad : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    var hurdles = arrayListOf<Hurdle>()

    private lateinit var car: Car

    var offsetOfCarInRoad = 0f
    var carSize = 0f
    var hurdleSize = 0f

    var leftSide = 0f
    var rightSide = 0f

    var color = 0

    var isRunning = false

    var verticalMoveStep = 0f

    private var eventListener: (RoadEvent) -> Unit = {}

    fun onEvent(eventListener: (RoadEvent) -> Unit) {
        this.eventListener = eventListener
    }

    fun init() {
        car = Car(context)
        addView(car)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            if (event.action == MotionEvent.ACTION_DOWN)
                car.changeSide()
        }
        return super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        offsetOfCarInRoad = width / 4f
        carSize = width / 4f
        hurdleSize = width / 4f

        leftSide = offsetOfCarInRoad * 1
        rightSide = offsetOfCarInRoad * 3

        verticalMoveStep = height / 500f

        car.init(
            offsetOfCarInRoad,
            carSize = carSize,
            height - offsetOfCarInRoad * 2,
            color,
            RoadRunway.Left
        )

    }

    fun setNewState() {
        for (hurdle in hurdles) {

            hurdle.refreshTop(hurdle.getCenterY() + verticalMoveStep)

            if (hurdle.getCenterY() > height) {
                hurdle.refreshTop(hurdleSize * -1f)
                hurdle.refreshIsScore()

            }

            val verticalDistance = car.getCenterY() - hurdle.getCenterY()
            val horizontalDistance = car.getCenterX() - hurdle.getCenterX()

            val carTouchSeverity = carSize / 1.7
            val hurdleTouchSeverity = hurdleSize / 1.7

            if (verticalDistance < carTouchSeverity * -1) {
                if (hurdle.isScore && !hurdle.isUsed) {
                    ///  lose
                    eventListener.invoke(RoadEvent.GameOver)
                }
            } else if (abs(verticalDistance) < carTouchSeverity && abs(horizontalDistance) < hurdleTouchSeverity) {
                if (hurdle.isScore && !hurdle.isUsed) {
                    /// score
                    hurdle.setAsUsed()
                    eventListener.invoke(RoadEvent.UpdateScore)
                } else if (!hurdle.isScore) {
                    // lose
                    eventListener.invoke(RoadEvent.GameOver)
                }
            }

        }

    }


    fun restartOrPlayGame() {
        initHurdles()
        isRunning = true
    }


    /**
     * by calling this function we rest
     */
    fun stopGame() {
        isRunning = false
    }


    private fun initHurdles() {

        if (hurdles.size > 0)
            for (hurdle in hurdles) {
                removeView(hurdle)
            }

        hurdles = arrayListOf()

        val maxHurdles = 4
        val hurdleDistance = height / maxHurdles

        for (i in 0 until maxHurdles) {
            val hurdle = Hurdle(context)

            hurdles.add(hurdle)

            hurdle.color = color

            hurdle.offsetOfCarInRoad = offsetOfCarInRoad

            hurdle.roadRunway =
                if (Random().nextBoolean()) RoadRunway.Left else RoadRunway.Right

            hurdle.size = hurdleSize

            hurdle.refreshIsScore()

            val centerY = ((i * hurdleDistance - height / 2).toFloat())
            hurdle.refreshTop(centerY)

            addView(hurdle)

        }

    }
}