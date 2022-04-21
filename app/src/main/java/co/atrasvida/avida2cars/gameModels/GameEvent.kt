package co.atrasvida.avida2cars.gameModels

sealed class GameEvent {
    data class GameOver(val currentScore: Int, val bestScore: Int) : GameEvent()
    data class UpdateScore(val score: Int) : GameEvent()
}
