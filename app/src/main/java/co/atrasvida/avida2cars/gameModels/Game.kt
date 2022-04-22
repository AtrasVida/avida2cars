package co.atrasvida.avida2cars.gameModels

import android.content.SharedPreferences
import co.atrasvida.avida2cars.GameSharedPrefHelper

class Game(private val gameSP: GameSharedPrefHelper) {
    private var score = 0

    var roads: ArrayList<GameRoad> = arrayListOf()

    fun onEvent(event: (GameEvent) -> Unit) {
        for (road in roads) {
            road.onEvent { roadEvent ->
                when (roadEvent) {
                    RoadEvent.GameOver -> {
                        saveScore(score)
                        event.invoke(
                            GameEvent.GameOver(
                                currentScore = score,
                                bestScore = getBestScore()
                            )
                        )
                        stopGame()
                    }
                    is RoadEvent.UpdateScore -> {
                        event.invoke(GameEvent.UpdateScore(++score))
                    }
                }
            }
        }
    }


    fun restartOrPlayGame() {
        score = 0
        for (road in roads) {
            road.restartOrPlayGame()
        }
    }

    /**
     * by calling this function we stop game
     *
     * @see GameRoad.stopGame
     */
    private fun stopGame() {
        for (road in roads) {
            road.stopGame()
        }
    }

    /**
     * In this method
     * We always store the user's current score in [SharedPreferences] using [GameSharedPrefHelper]
     * Then, by checking the following condition,
     * ## if (gameSP.score > gameSP.bestScore) {
     * ##      gameSP.bestScore = score
     * ## }
     * if the current score is greater than the previous user's previous best score, we will save it in [SharedPreferences]
     */
    private fun saveScore(score: Int) {
        gameSP.score = score
        if (gameSP.score > gameSP.bestScore) {
            gameSP.bestScore = score
        }
    }

    /**
     * return user best score
     *
     * @see GameSharedPrefHelper.bestScore
     */
    private fun getBestScore(): Int {
        return gameSP.bestScore
    }
}