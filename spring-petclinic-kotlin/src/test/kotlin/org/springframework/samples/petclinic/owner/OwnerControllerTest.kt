@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.springframework.samples.petclinic.owner

import org.assertj.core.util.Lists
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.pet.PetType
import org.springframework.samples.petclinic.views.owners.OwnersCreate
import org.springframework.samples.petclinic.views.owners.OwnersDetails
import org.springframework.samples.petclinic.views.owners.OwnersFind
import org.springframework.samples.petclinic.views.owners.OwnersList
import org.springframework.samples.petclinic.visit.Visit
import org.springframework.samples.petclinic.visit.VisitRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

/**
 * Test class for [OwnerController]
 *
 * @author Colin But
 */
@ExtendWith(SpringExtension::class)
@WebMvcTest(OwnerController::class)
class OwnerControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @MockitoBean
    private lateinit var visits: VisitRepository

    @MockitoBean
    private lateinit var pets: PetRepository

    @MockitoBean
    private lateinit var ownersDetails: OwnersDetails

    @MockitoBean
    private lateinit var ownersFind: OwnersFind

    @MockitoBean
    private lateinit var ownersList: OwnersList

    @MockitoBean
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
        max.id = 1
        max.type = dog
        max.name = "Max"
        max.birthDate = LocalDate.now()
        george.pets = mutableSetOf(max)
        given(owners.findById(TEST_OWNER_ID)).willReturn(george)
        val visit = Visit()
        visit.date = LocalDate.now()
        given(this.visits.findByPetId(max.id!!)).willReturn(Collections.singleton(visit))
        given(owners.findByLastName("")).willReturn(Lists.newArrayList(george))
    }

    //TODO
    @Test
    fun testInitCreationForm() {
        mockMvc
            .perform(get("/owners/new"))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    //TODO
    @Test
    fun testProcessCreationFormSuccess() {
        mockMvc
            .perform(
                post("/owners/new")
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("address", "123 Caramel Street")
                    .param("city", "London")
                    .param("telephone", "01316761638"),
            ).andExpect(status().is3xxRedirection)
    }

    //TODO
    @Test
    fun testProcessCreationFormHasErrors() {
        mockMvc
            .perform(
                post("/owners/new")
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("city", "London"),
            ).andExpect(status().isOk)
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    @Test
    fun testInitFindForm() {
        mockMvc
            .perform(get(Routes.OWNERS_FIND))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("Find Owners")))
            .andExpect(
                content()
                    .string(containsString("<form class=\"form-horizontal\" id=\"search-owner-form\" action=\"/owners\" method=\"get\">")),
            ).andExpect(content().string(containsString("<label class=\"col-sm-2 control-label\">")))
            .andExpect(content().string(containsString("<input class=\"fom\" type=\"text\" name=\"lastName\"")))
            .andExpect(content().string(containsString("First Name")))
            .andExpect(content().string(containsString("Last Name")))
            .andExpect(content().string(containsString("Pets")))
            .andExpect(content().string(containsString("<tbody id=\"owners-table\">")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<tr onclick=\"window.location='/owners/1'\" style=\"cursor: pointer;\" onmouseover=\"this.style.backgroundColor='#f5f5f5'\" onmouseout=\"this.style.backgroundColor='' \">",
                        ),
                    ),
            ).andExpect(content().string(containsString("George")))
            .andExpect(content().string(containsString("Franklin")))
            .andExpect(content().string(containsString("[Max]")))
            .andExpect(content().string(containsString("Find Owner")))
            .andExpect(content().string(containsString("<a class=\"btn btn-primary\" href=\"/owners/new\">")))
            .andExpect(content().string(containsString("Add Owner")))
    }

    @Test
    fun testProcessFindFormSuccess() {
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

    //TODO
    @Test
    fun testProcessFindFormByLastName() {
        given(owners.findByLastName(george.lastName)).willReturn(Lists.newArrayList(george))
        mockMvc
            .perform(
                get("/owners")
                    .param("lastName", "Franklin"),
            ).andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/$TEST_OWNER_ID"))
    }

    //TODO
    @Test
    fun testProcessFindFormNoOwnersFound() {
        mockMvc
            .perform(
                get("/owners")
                    .param("lastName", "Unknown Surname"),
            ).andExpect(status().isOk)
            .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
            .andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
            .andExpect(view().name("owners/findOwners"))
    }

    //TODO
    @Test
    fun testInitUpdateOwnerForm() {
        mockMvc
            .perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", hasProperty<Any>("lastName", `is`("Franklin"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("firstName", `is`("George"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("address", `is`("110 W. Liberty St."))))
            .andExpect(model().attribute("owner", hasProperty<Any>("city", `is`("Madison"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("telephone", `is`("6085551023"))))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    //TODO
    @Test
    fun testProcessUpdateOwnerFormSuccess() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/edit", TEST_OWNER_ID)
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("address", "123 Caramel Street")
                    .param("city", "London")
                    .param("telephone", "01616291589"),
            ).andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/owners/{ownerId}"))
    }

    //TODO
    @Test
    fun testProcessUpdateOwnerFormHasErrors() {
        mockMvc
            .perform(
                post("/owners/{ownerId}/edit", TEST_OWNER_ID)
                    .param("firstName", "Joe")
                    .param("lastName", "Bloggs")
                    .param("city", "London"),
            ).andExpect(status().isOk)
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"))
    }

    //TODO
    @Test
    fun testShowOwner() {
        mockMvc
            .perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk)
            .andExpect(model().attribute("owner", hasProperty<Any>("lastName", `is`("Franklin"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("firstName", `is`("George"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("address", `is`("110 W. Liberty St."))))
            .andExpect(model().attribute("owner", hasProperty<Any>("city", `is`("Madison"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("telephone", `is`("6085551023"))))
            .andExpect(model().attribute("owner", hasProperty<Any>("pets", not<Any>(empty<Any>()))))
            .andExpect(view().name("owners/ownerDetails"))

        Mockito.verify(this.visits).findByPetId(1)
    }

    companion object {
        private const val TEST_OWNER_ID = 1
    }
}
