package co.atrasvida.avida2cars.gameModels

sealed class GameEvent {
    object GameOver : GameEvent()
    data class OnScoreCallBack(val score : Int) : GameEvent()
}
