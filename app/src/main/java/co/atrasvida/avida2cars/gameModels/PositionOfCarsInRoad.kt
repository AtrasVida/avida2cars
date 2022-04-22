package co.atrasvida.avida2cars.gameModels

sealed class PositionOfCarsInRoad {
    object Left : PositionOfCarsInRoad()
    object Right : PositionOfCarsInRoad()
}
