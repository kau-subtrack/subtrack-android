package com.please.data.models.driver

// Sealed Class for States
sealed class DestinationState {
    data class Waiting(val message: String) : DestinationState()
    data class WaitingForOrders(val message: String) : DestinationState()
    data class NavigateToPickup(
        val destination: NextDestination,
        val route: RouteResponse
    ) : DestinationState()
    data class ReturnToHub(val route: RouteResponse) : DestinationState()
    object AtHub : DestinationState()
}