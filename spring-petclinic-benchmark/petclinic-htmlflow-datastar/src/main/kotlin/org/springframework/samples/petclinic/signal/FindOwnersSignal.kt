package org.springframework.samples.petclinic.signal

import kotlinx.serialization.Serializable

@Serializable
data class FindOwnersSignal(
    val lastName: String = "",
)
