package co.atrasvida.avida2cars.gameModels

class Game {
    private var score = 0

    fun getScore() = score

    var roads: ArrayList<GameRoad> = arrayListOf()

    fun onEvent(event : (GameEvent) -> Unit) {
        for (road in roads) {
            road.onLoseCallBack = {
                event.invoke(GameEvent.GameOver)
                stopGame()
            }
            road.onScoreCallBack = {
                event.invoke(GameEvent.OnScoreCallBack(++score))
            }
        }
    }


    fun restartOrPlayGame() {
        score = 0
        for (road in roads) {
            road.restartOrPlayGame()
        }
    }

    private fun stopGame() {
        for (road in roads) {
            road.stopGame()
        }
    }


}