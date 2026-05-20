package org.springframework.samples.petclinic.system

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import java.time.LocalDate

fun jsonToPet(
    jsonObject: JsonObject,
    suffix: String,
    pets: PetRepository,
) = Pet().apply {
    name = jsonObject["name$suffix"]?.jsonPrimitive?.content?.ifBlank { null }
    birthDate =
        jsonObject["birthDate$suffix"]
            ?.jsonPrimitive
            ?.content
            ?.takeIf { it.isNotBlank() }
            ?.let { LocalDate.parse(it) }
    type = pets.findPetTypes().firstOrNull { it.name == jsonObject["type$suffix"]?.jsonPrimitive?.content }
}
