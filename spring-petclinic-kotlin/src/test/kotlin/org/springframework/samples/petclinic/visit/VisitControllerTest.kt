package org.springframework.samples.petclinic.visit

import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.owner.OwnerRepository
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.pet.PetType
import org.springframework.samples.petclinic.views.visits.CreateOrUpdateVisitForm
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

/**
 * Test class for [VisitController]
 * Tests the pet visit form rendering and submission
 *
 * @author Colin But
 */
@ExtendWith(SpringExtension::class)
@WebMvcTest(VisitController::class)
@Import(CreateOrUpdateVisitForm::class)
class VisitControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var visits: VisitRepository

    @MockitoBean
    private lateinit var pets: PetRepository

    @MockitoBean
    private lateinit var owners: OwnerRepository

    @Autowired
    private lateinit var createOrUpdateVisitForm: CreateOrUpdateVisitForm

    private lateinit var max: Pet
    private lateinit var george: Owner

    @BeforeEach
    fun init() {
        george = Owner()
        george.id = TEST_OWNER_ID
        george.firstName = "George"
        george.lastName = "Franklin"
        george.address = "110 W. Liberty St."
        george.city = "Madison"
        george.telephone = "6085551023"
        max = Pet()
        val dog = PetType()
        dog.name = "dog"
        max.id = TEST_PET_ID
        max.type = dog
        max.name = "Max"
        max.birthDate = LocalDate.now()
        max.owner = george
        george.pets = mutableSetOf(max)
        val visit = Visit()
        visit.id = 1
        visit.date = LocalDate.now()
        visit.description = "Visit checkup routine."
        visit.petId = max.id
        max.visits = mutableSetOf(visit)

        given(pets.findById(TEST_PET_ID)).willReturn(max)
    }

    @Test
    fun `init new visit form displays pet and visit form`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get(Routes.visitNew(TEST_OWNER_ID, TEST_PET_ID)))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("New Visit")))
            .andExpect(content().string(containsString("Pet")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString("Birth Date")))
            .andExpect(content().string(containsString("Type")))
            .andExpect(content().string(containsString("Owner")))
            .andExpect(content().string(containsString(max.name)))
            .andExpect(content().string(containsString("Max")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "dog",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(containsString("${george.firstName} ${george.lastName}")),
            ).andExpect(content().string(containsString("Date")))
            .andExpect(content().string(containsString("Description")))
            .andExpect(content().string(containsString("Previous Visits")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<input type=\"hidden\" name=\"petId\" value=\"$TEST_PET_ID\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Add Visit")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<form class=\"form-horizontal\" data-signals:date=\"\" data-signals:description=\"\" method=\"post\" action=\"/owners/$TEST_OWNER_ID/pets/$TEST_PET_ID/visits/new\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"date\" id=\"date\" name=\"date\" data-bind:date=\"\" value=\"${LocalDate.now()}\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"description\" name=\"description\" data-bind:description=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Previous Visits")))
            .andExpect(content().string(containsString("Visit checkup routine.")))
    }

    @Test
    fun `process new visit form success redirects to owner details`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(Routes.VISIT_NEW, TEST_OWNER_ID, TEST_PET_ID)
                    .param("date", "2025-05-15")
                    .param("description", "Annual checkup"),
            ).andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl(Routes.ownerId(TEST_OWNER_ID)))
    }

    @Test
    fun `process new visit form validation error displays form again`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(Routes.VISIT_NEW, TEST_OWNER_ID, TEST_PET_ID)
                    .param("date", "2025-05-15"),
                // Missing required description parameter
            ).andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(containsString("New Visit")))
            .andExpect(content().string(containsString("Pet")))
            .andExpect(content().string(containsString("Name")))
            .andExpect(content().string(containsString("Birth Date")))
            .andExpect(content().string(containsString("Type")))
            .andExpect(content().string(containsString("Owner")))
            .andExpect(content().string(containsString(max.name)))
            .andExpect(content().string(containsString("Max")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "dog",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(containsString("${george.firstName} ${george.lastName}")),
            ).andExpect(content().string(containsString("Date")))
            .andExpect(content().string(containsString("Description")))
            .andExpect(content().string(containsString("Previous Visits")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<input type=\"hidden\" name=\"petId\" value=\"$TEST_PET_ID\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Add Visit")))
            .andExpect(
                content()
                    .string(
                        containsString(
                            "<form class=\"form-horizontal\" data-signals:date=\"\" data-signals:description=\"\" method=\"post\" action=\"/owners/$TEST_OWNER_ID/pets/$TEST_PET_ID/visits/new\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"date\" id=\"date\" name=\"date\" data-bind:date=\"\" value=\"${LocalDate.now()}\">",
                        ),
                    ),
            ).andExpect(
                content()
                    .string(
                        containsString(
                            "<input class=\"form-control\" type=\"text\" id=\"description\" name=\"description\" data-bind:description=\"\" value=\"\">",
                        ),
                    ),
            ).andExpect(content().string(containsString("Previous Visits")))
            .andExpect(content().string(containsString("Visit checkup routine.")))
            .andExpect(content().string(containsString("<div id=\"error\" class=\"error\">")))
            .andExpect(content().string(containsString("Missing fields.")))
    }

    companion object {
        private const val TEST_OWNER_ID = 1
        private const val TEST_PET_ID = 1
    }
}