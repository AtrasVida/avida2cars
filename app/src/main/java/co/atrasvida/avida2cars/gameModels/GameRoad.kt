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
    private val mainScope = MainScope()

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
                car.changeSide()
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
            RoadRunway.Left
        )

    }

    //var mainThread = createNewThread()
    private suspend fun mainThread() = mainScope.launch {
        while (isRunning) {
            handler.post { setNewState() }
            delay(threadSleep)
        }
    }

    private suspend fun speedThread() = mainScope.launch {
        while (isRunning) {
            delay(20000L)
            threadSleep--
        }
    }
    //var speedTread = createNewSpeedThread()

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

    fun setNewState() {
        for (hurdle in hurdles) {

            hurdle.refreshTop(hurdle.getCenterY() + verticalMoveStep)

            if (hurdle.getCenterY() > height) {
                hurdle.refreshTop(hurdle.size * -1f)
                hurdle.refreshIsScore()

            }

            val verticalDistance = car.getCenterY() - hurdle.getCenterY()
            val horizontalDistance = car.getCenterY() - hurdle.getCenterY()

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
        //threadSleep = 10

        mainScope.launch {
            if (mainThread().isActive) {
                mainThread().cancel()
                speedThread().cancel()
            }
            mainThread().join()
            speedThread().join()
        }
//        if (mainThread.isAlive) {
//            mainThread.interrupt()
//            speedTread.interrupt()
//        }
//
//        mainThread = createNewThread()
//        mainThread.start()
//
//        speedTread = createNewSpeedThread()
//        speedTread.start()

    }


    /**
     * by calling this function we rest
     */
    fun stopGame() {
        isRunning = false
        mainScope.launch {
            mainThread().cancel()
            speedThread().cancel()
        }
//        mainThread.interrupt()
//        speedTread.interrupt()
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

            hurdle.setCenterY((i * hurdleDistance - height / 2).toFloat())

            hurdle.size = offsetOfCarInRoad / 3

            hurdle.refreshIsScore()
            hurdle.refreshTop(hurdle.getCenterY())

            addView(hurdle)

        }

    }
}