package co.atrasvida.avida2cars.gameModels

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
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

    var leftSide = 0f
    var rightSide = 0f

    var color = 0

    var isRunning = false

    var verticalMoveStep = 0f

    private var eventListener: (RoadEvent) -> Unit = {}

    fun onEvent(eventListener: (RoadEvent) -> Unit) {
        this.eventListener = eventListener
    }

    var threadSleep = 10L

    fun init() {
        car = Car(context)
        addView(car)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            if (event.action == MotionEvent.ACTION_DOWN)
                car.positionOfCarsInRoad =
                    if (car.positionOfCarsInRoad == PositionOfCarsInRoad.Left) PositionOfCarsInRoad.Right else PositionOfCarsInRoad.Left
        }
        return super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        offsetOfCarInRoad = width / 4f

        leftSide = offsetOfCarInRoad * 1
        rightSide = offsetOfCarInRoad * 3

        verticalMoveStep = height / 500f

        car.init(
            offsetOfCarInRoad,
            height - offsetOfCarInRoad * 2,
            color,
            PositionOfCarsInRoad.Left
        )

    }

    var mainThread = createNewThread()
    var speedTread = createNewSpeedThread()

    private fun createNewThread() = Thread {

        while (isRunning) {

            handler.post { setNewState() }

            try {
                Thread.sleep(threadSleep)
            } catch (e: Exception) {
            }
        }

    }

    fun createNewSpeedThread() = Thread {
        while (isRunning) {
            try {
                Thread.sleep(20000)
                threadSleep--
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun setNewState() {
        for (hurdle in hurdles) {

            hurdle.refreshTop(hurdle.centerY + verticalMoveStep)

            if (hurdle.centerY > height) {
                hurdle.refreshTop(hurdle.size * -1f)
                hurdle.refreshIsScore()

            }

            val verticalDistance = car.centerY - hurdle.centerY
            val horizontalDistance = car.centerX - hurdle.centerX

            val carHalfSize = car.size / 2
            val hurdleHalfSize = hurdle.size / 2

            if (verticalDistance < carHalfSize * -1) {
                if (hurdle.isScore && !hurdle.isUsed) {
                    ///  lose
                    eventListener.invoke(RoadEvent.GameOver)
                }
            } else if (abs(verticalDistance) < carHalfSize && abs(horizontalDistance) < hurdleHalfSize) {
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
        threadSleep = 10

        if (mainThread.isAlive) {
            mainThread.interrupt()
            speedTread.interrupt()
        }

        mainThread = createNewThread()
        mainThread.start()

        speedTread = createNewSpeedThread()
        speedTread.start()

    }


    /**
     * by calling this function we rest
     */
    fun stopGame() {
        isRunning = false
        mainThread.interrupt()
        speedTread.interrupt()
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

            hurdle.positionOfCarsInRoad =
                if (Random().nextBoolean()) PositionOfCarsInRoad.Left else PositionOfCarsInRoad.Right

            hurdle.centerY = (i * hurdleDistance - height / 2).toFloat()

            hurdle.size = offsetOfCarInRoad / 3

            hurdle.refreshIsScore()
            hurdle.refreshTop(hurdle.centerY)

            addView(hurdle)

        }

    }
}