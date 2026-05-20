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
package org.springframework.samples.petclinic.owner

import dev.datastar.kotlin.sdk.ElementPatchMode
import dev.datastar.kotlin.sdk.PatchElementsOptions
import dev.datastar.kotlin.sdk.blocking.ServerSentEventGenerator
import jakarta.validation.Valid
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.signal.FindOwnersSignal
import org.springframework.samples.petclinic.signal.OwnerEditSignal
import org.springframework.samples.petclinic.system.DatastarSignal
import org.springframework.samples.petclinic.system.adapterResponse
import org.springframework.samples.petclinic.views.owners.ERROR_MSG
import org.springframework.samples.petclinic.views.owners.OwnersCreate
import org.springframework.samples.petclinic.views.owners.OwnersDetails
import org.springframework.samples.petclinic.views.owners.OwnersFind
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.net.URI

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Antoine Rey
 */
@Controller
class OwnerController(
    val owners: OwnerRepository,
    val visits: VisitRepository,
    val pets: PetRepository,
    private val ownersDetails: OwnersDetails,
) {
    val ownersFind = OwnersFind()
    val ownersCreate = OwnersCreate()

    private val json = Json { ignoreUnknownKeys = true }

    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("id")
    }

    @GetMapping(Routes.OWNERS_NEW)
    fun initCreationForm(): ResponseEntity<String> = ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(ownersCreate.view.render())

    @PostMapping(Routes.OWNERS_NEW)
    fun processCreationForm(
        @Valid owner: Owner,
        result: BindingResult,
    ): ResponseEntity<String> =
        if (result.hasErrors()) {
            result.reject("createError", ERROR_MSG)
            ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(ownersCreate.errorView.render(ERROR_MSG))
        } else {
            owners.save(owner)
            ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(Routes.ownerId(owner.id)))
                .build()
        }

    @GetMapping(Routes.OWNERS_FIND_RESULT, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getSearchOwners(
        @DatastarSignal signal: FindOwnersSignal,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val owners = owners.findByLastName(signal.lastName)
            generator.patchElements(ownersFind.activeSearchOwnerRowsFragment.render(owners))
            stream.flush()
        }

    @GetMapping(Routes.OWNERS)
    fun processFindForm(): ResponseEntity<String> =
        ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(ownersFind.view.render(owners.findByLastName("")))

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return the view
     */
    @GetMapping(Routes.OWNERS_ID)
    fun showOwner(
        @PathVariable("ownerId") ownerId: Int,
    ): ResponseEntity<String> {
        val owner = this.owners.findById(ownerId)
        for (pet in owner.getPets()) {
            pet.visits = visits.findByPetId(pet.id!!)
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(ownersDetails.view.render(owner))
    }

    @GetMapping(Routes.OWNERS_EDIT)
    fun editRow(
        @PathVariable("ownerId") ownerId: Int,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val owner = owners.findById(ownerId)
            generator.patchElements(ownersDetails.editOwnerTableView.render(owner))
            generator.patchSignals(
                """
                {
                 "firstName": "${owner.firstName}",
                 "lastName": "${owner.lastName}",
                 "address": "${owner.address}",
                 "city": "${owner.city}",
                 "telephone": "${owner.telephone}"
                }
                """.trimIndent(),
            )
            stream.flush()
        }

    @PatchMapping(Routes.OWNERS_EDIT, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun patchOwnerEdit(
        @PathVariable("ownerId") ownerId: Int,
        @RequestBody datastarBody: String,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val editedOwner = json.decodeFromString<OwnerEditSignal>(datastarBody)
            if (!editedOwnerIsValid(editedOwner)) {
                generator.patchElements(
                    "",
                    PatchElementsOptions(
                        "#error",
                        ElementPatchMode.Remove,
                    ),
                )
                generator.patchElements(
                    ownersDetails.errorEditOwnerView.render(ERROR_MSG),
                    PatchElementsOptions(
                        selector = "#owner-table-body",
                        ElementPatchMode.Append,
                    ),
                )
                generator.patchSignals("""{"_editing": true}""")
            } else {
                val owner = owners.findById(ownerId)
                owner.firstName = editedOwner.firstName
                owner.lastName = editedOwner.lastName
                owner.address = editedOwner.address
                owner.city = editedOwner.city
                owner.telephone = editedOwner.telephone
                owners.save(owner)
                generator.patchElements(ownersDetails.defaultOwnerTableView.render(owner))
                generator.patchSignals(resetOwnerSignals())
            }
            stream.flush()
        }

    @GetMapping(Routes.OWNERS_EDIT_CANCEL, produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun cancelEdit(
        @PathVariable("ownerId") ownerId: Int,
    ): StreamingResponseBody =
        StreamingResponseBody { stream ->
            val response = adapterResponse(stream)
            val generator = ServerSentEventGenerator(response)
            val owner = owners.findById(ownerId)
            generator.patchElements(ownersDetails.defaultOwnerTableView.render(owner))
            generator.patchSignals(resetOwnerSignals())
            stream.flush()
        }

    private fun resetOwnerSignals() =
        """
        {
         "firstName": null,
         "lastName": null,
         "address": null,
         "city": null,
         "telephone": null
        }
        """.trimIndent()

    private fun editedOwnerIsValid(editedOwner: OwnerEditSignal): Boolean =
        editedOwner.city.isNotEmpty() &&
            editedOwner.firstName.isNotEmpty() &&
            editedOwner.lastName.isNotEmpty() &&
            editedOwner.address.isNotEmpty() &&
            editedOwner.telephone.matches(Regex("^\\d{1,10}$"))
}
