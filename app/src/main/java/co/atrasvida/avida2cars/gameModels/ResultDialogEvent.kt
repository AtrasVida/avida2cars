package co.atrasvida.avida2cars.gameModels

sealed class ResultDialogEvent {
    data class MustRestGame(val value : Boolean) : ResultDialogEvent()
}
