package org.springframework.samples.petclinic.signal

import kotlinx.serialization.Serializable

@Serializable
data class OwnerEditSignal(
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val telephone: String = "",
)

