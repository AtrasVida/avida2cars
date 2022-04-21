package co.atrasvida.avida2cars.gameModels

class Game {

    var onLoseCallBack: () -> Unit = {}

    var onScoreCallBack: (Int) -> Unit = {}

    private var score = 0

    fun getScore() = score

    var roads: ArrayList<GameRoad> = arrayListOf()
        set(value) {
            field = value
            setCallBacks()
        }

    private fun setCallBacks() {
        for (road in roads) {
            road.onLoseCallBack = {
                onLoseCallBack()
                stopGame()
            }
            road.onScoreCallBack = {
                onScoreCallBack(++score)
            }
        }
    }


    // FIXME: seperate start and resetGame
    fun startGame() {
        score = 0
        for (road in roads) {
            road.startGame()
        }
    }

    fun stopGame() {
        for (road in roads) {
            road.stopGame()
        }
    }


}