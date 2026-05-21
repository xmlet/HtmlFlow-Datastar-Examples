/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.pet

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.blocking.ServerSentEventGenerator
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.system.adapterResponse
import org.springframework.samples.petclinic.system.jsonToPet
import org.springframework.samples.petclinic.views.owners.OwnersDetails
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Antoine Rey
 *
 * @author Paulo Carvalho
 * @author Leonel Correia
 */

@Controller
class PetController(
    val pets: PetRepository,
    val owners: OwnerRepository,
    private val ownersDetails: OwnersDetails,
) {
    private val json = Json { ignoreUnknownKeys = true }

    @ModelAttribute("types")
    fun populatePetTypes(): Collection<PetType> = this.pets.findPetTypes()

    @ModelAttribute("owner")
    fun findOwner(
        @PathVariable("ownerId") ownerId: Int,
    ): Owner = owners.findById(ownerId)

    @InitBinder("owner")
    fun initOwnerBinder(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id")
    }

    @InitBinder("pet")
    fun initPetBinder(dataBinder: WebDataBinder) {
        dataBinder.validator = PetValidator()
    }

    @GetMapping(Routes.PET_NEW)
    fun initCreation(owner: Owner): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            generator.patchElements(
                ownersDetails.petAddView.render(owner),
                PatchElementsOptions(
                    selector = "#pets-table-body",
                    mode = ElementPatchMode.Prepend,
                ),
            )
            stream.flush()
        }

    @PostMapping(Routes.PET_NEW, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun processCreation(
        owner: Owner,
        @RequestBody datastarBody: String,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val jsonObject = json.parseToJsonElement(datastarBody).jsonObject
            val pet = jsonToPet(jsonObject, "New", pets)
            val errors = BeanPropertyBindingResult(pet, "pet")
            PetValidator().validate(pet, errors)
            if (!errors.hasErrors()) {
                owner.addPet(pet)
                pets.save(pet)
                val newOwner = owners.findById(owner.id!!)
                val newPet = newOwner.pets.find { it.name == pet.name && it.birthDate == pet.birthDate }
                generator.patchSignals(resetPetSignals("New"))
                generator.patchElements(
                    ownersDetails.petRow.render(newPet),
                    PatchElementsOptions(
                        selector = "#pets-add",
                        mode = ElementPatchMode.Replace,
                    ),
                )
            }
            stream.flush()
        }

    @GetMapping(Routes.PET_NEW_CANCEL)
    fun cancelCreation(): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            generator.patchSignals(resetPetSignals("New"))
            generator.patchElements(
                "",
                PatchElementsOptions(
                    selector = "#pets-add",
                    mode = ElementPatchMode.Remove,
                ),
            )
            stream.flush()
        }

    @GetMapping(Routes.PET_EDIT)
    fun initUpdate(
        @PathVariable petId: Int,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val pet = pets.findById(petId)
            generator.patchSignals(
                """
                {
                 "name$petId": "${pet.name}",
                 "birthDate$petId": "${pet.birthDate}",
                 "type$petId": "${pet.type}"
                }
                """.trimIndent(),
            )
            generator.patchElements(ownersDetails.petEditRow.render(pet))
            stream.flush()
        }

    @PatchMapping(Routes.PET_EDIT, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun processUpdate(
        @PathVariable("petId") petId: Int,
        owner: Owner,
        @RequestBody datastarBody: String,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val jsonObject = json.parseToJsonElement(datastarBody).jsonObject
            val pet = jsonToPet(jsonObject, petId.toString(), pets)
            pet.id = petId
            val errors = BeanPropertyBindingResult(pet, "pet")
            PetValidator().validate(pet, errors)
            if (StringUtils.hasLength(pet.name) && pet.isNew && owner.getPet(pet.name!!, true) != null) {
                errors.rejectValue("name", "duplicate", "already exists")
            }
            if (errors.hasErrors()) {
                generator.patchElements(ownersDetails.petEditRow.render(pet))
            } else {
                val ownerPet = owner.pets.first { it.id == pet.id }
                owner.addPet(pet)
                pets.save(pet)
                ownerPet.name = pet.name
                ownerPet.birthDate = pet.birthDate
                ownerPet.type = pet.type
                generator.patchElements(ownersDetails.petRow.render(pet))
                generator.patchSignals(resetPetSignals(petId.toString()))
            }
            stream.flush()
        }

    @GetMapping(Routes.PET_EDIT_CANCEL)
    fun cancelUpdate(
        @PathVariable("petId") petId: Int,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val pet = pets.findById(petId)
            generator.patchElements(ownersDetails.petRow.render(pet))
            generator.patchSignals(resetPetSignals(petId.toString()))
            stream.flush()
        }

    private fun resetPetSignals(suffix: String) =
        """
        {
            "name$suffix": null,
            "birthDate$suffix": null,
            "type$suffix": null
        }
        """.trimIndent()
}
