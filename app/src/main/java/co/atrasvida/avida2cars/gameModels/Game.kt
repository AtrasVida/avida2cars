package co.atrasvida.avida2cars.gameModels

class Game {
    private var score = 0

    fun getScore() = score

    var roads: ArrayList<GameRoad> = arrayListOf()

    fun onEvent(event: (GameEvent) -> Unit) {
        for (road in roads) {
            road.onEvent { roadEvent ->
                when(roadEvent){
                    RoadEvent.GameOver -> {
                        event.invoke(GameEvent.GameOver)
                        stopGame()
                    }
                    is RoadEvent.OnScoreCallBack -> {
                        event.invoke(GameEvent.OnScoreCallBack(++score))
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

    private fun stopGame() {
        for (road in roads) {
            road.stopGame()
        }
    }


}