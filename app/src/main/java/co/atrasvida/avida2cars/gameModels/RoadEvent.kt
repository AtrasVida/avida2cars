package co.atrasvida.avida2cars.gameModels

sealed class RoadEvent {
    object GameOver : RoadEvent()
    object UpdateScore : RoadEvent()
}
