package co.atrasvida.avida2cars.gameModels

sealed class RoadRunway {
    object Left : RoadRunway()
    object Right : RoadRunway()
}
