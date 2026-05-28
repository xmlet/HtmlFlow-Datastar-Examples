@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.springframework.samples.petclinic.owner

import org.assertj.core.util.Lists
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.pet.PetType
import org.springframework.samples.petclinic.views.owners.OwnersCreate
import org.springframework.samples.petclinic.views.owners.OwnersDetails
import org.springframework.samples.petclinic.views.owners.OwnersFind
import org.springframework.samples.petclinic.visit.Visit
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

/**
 * Test class for [OwnerController]
 *
 * @author Colin But
 */
@ExtendWith(SpringExtension::class)
@WebMvcTest(OwnerController::class)
@Import(OwnersDetails::class, OwnersFind::class, OwnersCreate::class)
class OwnerControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @MockitoBean
    private lateinit var visits: VisitRepository

    @MockitoBean
    private lateinit var pets: PetRepository

    @Autowired
    private lateinit var ownersDetails: OwnersDetails

    @Autowired
    private lateinit var ownersFind: OwnersFind

    @Autowired
    private lateinit var ownersCreate: OwnersCreate

    private lateinit var george: Owner

    @BeforeEach
    fun setup() {
        george = Owner()
        george.id = TEST_OWNER_ID
        george.firstName = "George"
        george.lastName = "Franklin"
        george.address = "110 W. Liberty St."
        george.city = "Madison"
        george.telephone = "6085551023"
        val max = Pet()
        val dog = PetType()
        dog.name = "dog"
        max.id = 1
        max.type = dog
        max.name = "Max"
        max.birthDate = LocalDate.now()
        max.owner = george
        george.pets = mutableSetOf(max)
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george)
        val visit = Visit()
        visit.date = LocalDate.now()
        visit.description = "Visit checkup routine."
        visit.petId = max.id
        given(this.visits.findByPetId(max.id!!)).willReturn(mutableSetOf(visit))
        given(owners.findByLastName("")).willReturn(Lists.newArrayList(george))
        given(pets.findPetTypes()).willReturn(Lists.newArrayList(dog))
    }

    @Test
    fun testInitCreationForm() {
        mockMvc
            .perform(get(Routes.OWNERS_NEW))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("<form class=\"form-horizontal\" id=\"add-owner-form\" method=\"post\">")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "div data-signals:first-name=\"\" data-signals:last-name=\"\" data-signals:address=\"\" data-signals:city=\"\" data-signals:telephone=\"\" class=\"form-group has-feedback\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"firstName\" name=\"firstName\" data-bind:first-name=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"lastName\" name=\"lastName\" data-bind:last-name=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"address\" name=\"address\" data-bind:address=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"city\" name=\"city\" data-bind:city=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"telephone\" name=\"telephone\" data-bind:telephone=\"\" value=\"\">",
                        ),
                    ),
            )
    }

    @Test
    fun testProcessCreationFormSuccess() {
        mockMvc
            .perform(
                post(Routes.OWNERS_NEW)
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("address", "123 Caramel Street")
                    .param("city", "London")
                    .param("telephone", "01316761638"),
            ).andExpect(status().is3xxRedirection)
    }

    @Test
    fun testProcessCreationFormHasErrors() {
        mockMvc
            .perform(
                post(Routes.OWNERS_NEW)
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("city", "London"),
            ).andExpect(status().isOk)
            .andExpect(content().string(containsString("<form class=\"form-horizontal\" id=\"add-owner-form\" method=\"post\">")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "div data-signals:first-name=\"\" data-signals:last-name=\"\" data-signals:address=\"\" data-signals:city=\"\" data-signals:telephone=\"\" class=\"form-group has-feedback\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"firstName\" name=\"firstName\" data-bind:first-name=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"lastName\" name=\"lastName\" data-bind:last-name=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"address\" name=\"address\" data-bind:address=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"city\" name=\"city\" data-bind:city=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"telephone\" name=\"telephone\" data-bind:telephone=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "Either fields are empty or telephone number has other characters.",
                        ),
                    ),
            )
    }

    @Test
    fun testInitFind() {
        mockMvc
            .perform(get(Routes.OWNERS))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Find Owners")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"fom\" type=\"text\" name=\"lastName\" placeholder=\"Find Owners\" data-bind:last-name=\"\" data-on:input__debounce.200ms=\"@get('/owners/find/result')\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("<table id=\"owners\" class=\"table table-striped\">")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString("Address")))
            .andExpect(content().string(containsString("City")))
            .andExpect(content().string(containsString("Telephone")))
            .andExpect(content().string(containsString("Pets")))
            .andExpect(content().string(containsString("<tbody id=\"results-table\">")))
            .andExpect(content().string(containsString("<a href=\"/owners/1\">")))
            .andExpect(content().string(containsString("George Franklin")))
            .andExpect(content().string(containsString("110 W. Liberty St.")))
            .andExpect(content().string(containsString("Madison")))
            .andExpect(content().string(containsString("6085551023")))
            .andExpect(content().string(containsString("Max")))
            .andExpect(content().string(containsString("<a class=\"btn btn-primary\" href=\"/owners/new\">")))
            .andExpect(content().string(containsString("Add Owner")))
    }

    @Test
    fun testProcessFindFormInit() {
        given(owners.findByLastName("")).willReturn(Lists.newArrayList(george, Owner()))
        mockMvc
            .perform(get(Routes.OWNERS))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Owners")))
            .andExpect(content().string(containsString("<table id=\"owners\" class=\"table table-striped\">")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString("Address")))
            .andExpect(content().string(containsString("City")))
            .andExpect(content().string(containsString("Telephone")))
            .andExpect(content().string(containsString("Pets")))
            .andExpect(content().string(containsString("<a href=\"/owners/1\">")))
            .andExpect(content().string(containsString("George Franklin")))
            .andExpect(content().string(containsString("110 W. Liberty St.")))
            .andExpect(content().string(containsString("Madison")))
            .andExpect(content().string(containsString("6085551023")))
            .andExpect(content().string(containsString("Max")))
    }

    @Test
    fun testShowOwner() {
        given(owners.findById(TEST_OWNER_ID)).willReturn(george)
        mockMvc
            .perform(get(Routes.OWNERS_ID, TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Owner Information")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString("${george.firstName} ${george.lastName}")))
            .andExpect(content().string(containsString("Address")))
            .andExpect(content().string(containsString(george.address)))
            .andExpect(content().string(containsString("City")))
            .andExpect(content().string(containsString(george.city)))
            .andExpect(content().string(containsString("Telephone")))
            .andExpect(content().string(containsString(george.telephone)))
            .andExpect(
                content()
                    .string(
                        containsString(
                            $$"<button data-attr:disabled=\"$_editing\" data-on:click=\"$_editing = true; @get('/owners/$${george.id}/edit')\" class=\"btn btn-primary\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Edit Owner")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            $$"data-on:click=\"$_editing = true; @get('/owners/1/pets/new')\"",
                        ),
                    ),
            ).andExpect(content().string(containsString("Add New Pet")))
            .andExpect(content().string(containsString("Pets and Visits")))
            .andExpect(content().string(containsString("<tbody id=\"pets-table-body\">")))
            .andExpect(content().string(containsString("<tr id=\"row-pet-${george.pets.first().id}\">")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString(george.pets.first().name)))
            .andExpect(content().string(containsString("Birth Date")))
            .andExpect(content().string(containsString("Type")))
            .andExpect(
                content().string(
                    containsString(
                        george.pets
                            .first()
                            .type
                            .toString(),
                    ),
                ),
            ).andExpect(content().string(containsString("<table class=\"visits-table\">")))
            .andExpect(content().string(containsString("Visit Date")))
            .andExpect(content().string(containsString("Description")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            $$"<button id=\"pet-edit\" class=\"btn btn-primary\" data-on:click=\"$_editing = true; @get('/owners/$${george.id}/pets/$${george.pets.first().id}/edit')\" data-indicator:_fetching=\"\" data-attr:disabled=\"$_fetching || $_editing\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<a class=\"btn btn-primary\" href=\"/owners/${george.id}/pets/${george.pets.first().id}/visits/new\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Add Visit")))
    }

    companion object {
        private const val TEST_OWNER_ID = 1
    }
}
