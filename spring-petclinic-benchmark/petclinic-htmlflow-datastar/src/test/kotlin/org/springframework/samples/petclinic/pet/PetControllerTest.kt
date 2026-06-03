package org.springframework.samples.petclinic.pet/*
@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.springframework.samples.petclinic.pet

import org.assertj.core.util.Lists
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.views.owners.OwnersDetails
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

*/
/**
 * Test class for the [PetController]
 *
 * @author Colin But
 *//*

const val TEST_OWNER_ID = 1
const val TEST_PET_ID = 1

@ExtendWith(SpringExtension::class)
@WebMvcTest(
    value = [(PetController::class)],
    includeFilters = arrayOf(ComponentScan.Filter(value = [(PetTypeFormatter::class)], type = FilterType.ASSIGNABLE_TYPE)),
)
class PetControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var pets: PetRepository

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @MockitoBean
    private lateinit var ownersDetails: OwnersDetails

    private lateinit var george: Owner
    private lateinit var hamsterType: PetType
    private lateinit var max: Pet

    @BeforeEach
    fun setup() {
        // Setup owner
        george = Owner()
        george.id = TEST_OWNER_ID
        george.firstName = "George"
        george.lastName = "Franklin"
        george.address = "110 W. Liberty St."
        george.city = "Madison"
        george.telephone = "6085551023"

        // Setup pet type
        hamsterType = PetType()
        hamsterType.id = 3
        hamsterType.name = "hamster"

        // Setup pet
        max = Pet()
        max.id = TEST_PET_ID
        max.name = "Max"
        max.type = hamsterType
        max.birthDate = LocalDate.of(2015, 2, 12)
        max.owner = george
        george.pets = mutableSetOf(max)

        // Mock repository calls
        given(this.pets.findPetTypes()).willReturn(Lists.newArrayList(hamsterType))
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george)
        given(this.pets.findById(TEST_PET_ID)).willReturn(max)

        // Mock view renders
        given(ownersDetails.petAddView.render(george)).willReturn("<tr id=\"pets-add\">Add Pet Form</tr>")
        given(ownersDetails.defaultPetsTableView.render(george)).willReturn("<tbody id=\"pets-table-body\">Pets Table</tbody>")
        given(ownersDetails.petEditRow.render(max)).willReturn("<tr id=\"row-pet-1\">Edit Pet Form</tr>")
        given(ownersDetails.petRow.render(max)).willReturn("<tr id=\"row-pet-1\">Pet Row</tr>")
    }

    @Test
    fun testInitCreationForm() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE))

        verify(ownersDetails.petAddView).render(george)
    }

    @Test
    fun testInitUpdateForm() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE))

        verify(ownersDetails.petEditRow).render(max)
    }

    @Test
    fun testInitUpdateFormSendsSignals() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)

        // Verify the pet was fetched
        verify(this.pets).findById(TEST_PET_ID)
    }

    @Test
    fun testProcessCreationFormSuccess() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","type":"hamster","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that the pet was saved
        verify(pets).save(any(Pet::class.java))
    }

    @Test
    fun testProcessCreationFormValidationErrorMissingName() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"type":"hamster","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that the edit row view is called when there are errors
        verify(ownersDetails.petEditRow).render(any(Pet::class.java))
        // Verify that the pet was NOT saved
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessCreationFormValidationErrorMissingType() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that validation failed and pet was not saved
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessCreationFormValidationErrorMissingBirthDate() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","type":"hamster"}"""),
            )
            .andExpect(status().isOk)

        // Verify that validation failed
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessUpdateFormSuccess() {
        mockMvc
            .perform(
                patch("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","type":"hamster","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that the pet was saved
        verify(pets).save(any(Pet::class.java))
        // Verify that the pet row view was called
        verify(ownersDetails.petRow).render(any(Pet::class.java))
    }

    @Test
    fun testProcessUpdateFormValidationErrorMissingName() {
        mockMvc
            .perform(
                patch("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"type":"hamster","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that the edit row view is called when there are errors
        verify(ownersDetails.petEditRow).render(any(Pet::class.java))
        // Verify that the pet was NOT saved
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessUpdateFormValidationErrorMissingType() {
        mockMvc
            .perform(
                patch("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that validation failed
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessUpdateFormValidationErrorMissingBirthDate() {
        mockMvc
            .perform(
                patch("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Betty","type":"hamster"}"""),
            )
            .andExpect(status().isOk)

        // Verify that validation failed
        verify(pets, never()).save(any(Pet::class.java))
    }

    @Test
    fun testProcessUpdateFormDuplicatePetName() {
        // Create another pet with the same name in the owner's collection
        val existingPet = Pet()
        existingPet.id = 2
        existingPet.name = "Max"
        existingPet.owner = george
        george.pets.add(existingPet)

        mockMvc
            .perform(
                patch("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Max","type":"hamster","birthDate":"2015-02-12"}"""),
            )
            .andExpect(status().isOk)

        // Verify that the duplicate name was caught and pet was not saved
        verify(pets, never()).save(any(Pet::class.java))
        verify(ownersDetails.petEditRow).render(any(Pet::class.java))
    }

    // ============ Cancellation Tests ============

    @Test
    fun testCancelCreation() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/new/cancel", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE))

        // Just verify the response is OK
        // The signals and element removals are sent via SSE
    }

    @Test
    fun testCancelUpdate() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/{petId}/edit/cancel", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE))

        // Verify the pet was fetched
        verify(pets).findById(TEST_PET_ID)
        // Verify the pet row view was called
        verify(ownersDetails.petRow).render(max)
    }

    // ============ Model Attribute Tests ============

    @Test
    fun testPopulatePetTypes() {
        mockMvc
            .perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
            .andExpect(status().isOk)

        // Verify that pet types were fetched
        verify(pets).findPetTypes()
    }
}
*/
