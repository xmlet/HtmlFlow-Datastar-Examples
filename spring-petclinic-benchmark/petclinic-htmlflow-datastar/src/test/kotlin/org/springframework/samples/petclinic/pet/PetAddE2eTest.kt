package org.springframework.samples.petclinic.pet

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.samples.petclinic.infrastructure.SpringBootApplicationServer
import org.springframework.samples.petclinic.infrastructure.SpringBootServerExtension
import kotlin.test.assertEquals

/**
 * End-to-end tests for adding new pets.
 * Tests the pet creation form and saving new pets.
 */
@ExtendWith(SpringBootServerExtension::class)
class PetAddE2eTest {
    @Test
    fun `add new pet form displays with save and cancel buttons`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to owner details page
                val url = "http://localhost:$port/owners/1"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready
                page.waitForSelector("button:has-text('Add New Pet')")

                // Click the Add New Pet button
                page.locator("button:has-text('Add New Pet')").click()

                // Wait for the form to appear
                page.waitForTimeout(500.0)

                // Wait for the pet add form to appear with input fields
                page.waitForSelector("input[data-bind\\:name-new]")

                // Verify that action buttons are disabled while form is open
                val editPetButton = page.locator("button:has-text('Edit Pet')")
                val editOwnerButton = page.locator("button:has-text('Edit Owner')")
                val addNewPetButton = page.locator("button:has-text('Add New Pet')")

                assert(editPetButton.isDisabled) { "Edit Pet button should be disabled while adding pet" }
                assert(editOwnerButton.isDisabled) { "Edit Owner button should be disabled while adding pet" }
                assert(addNewPetButton.isDisabled) { "Add New Pet button should be disabled while form is open" }

                // Verify form elements are present
                val saveButton = page.querySelector("button#save-pet")
                val cancelButton = page.querySelector("button#cancel-pet")

                assert(saveButton != null) { "Save Pet button should be present" }
                assert(cancelButton != null) { "Cancel Pet button should be present" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `save new pet with valid data adds pet to table`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to owner details page
                val url = "http://localhost:$port/owners/1"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready
                page.waitForSelector("button:has-text('Add New Pet')")

                // Get the initial pet count
                val initialPetRows = page.querySelectorAll("tr[id^='row-pet-']").size

                // Click the Add New Pet button
                page.locator("button:has-text('Add New Pet')").click()

                // Wait for the form to appear
                page.waitForSelector("input[data-bind\\:name-new]")

                // Verify that action buttons are disabled while form is open
                val editPetButton = page.locator("button:has-text('Edit Pet')")
                val editOwnerButton = page.locator("button:has-text('Edit Owner')")
                val addNewPetButton = page.locator("button:has-text('Add New Pet')")

                assert(editPetButton.isDisabled) { "Edit Pet button should be disabled while adding pet" }
                assert(editOwnerButton.isDisabled) { "Edit Owner button should be disabled while adding pet" }
                assert(addNewPetButton.isDisabled) { "Add New Pet button should be disabled while form is open" }

                // Fill in the pet form
                val nameInput = page.locator("input[data-bind\\:name-new]")
                val birthDateInput = page.locator("input[data-bind\\:birth-date-new]")
                val typeSelect = page.locator("select[data-bind\\:type-new]")

                nameInput.fill("TestPet")
                birthDateInput.fill("2020-01-15")
                typeSelect.selectOption("cat")

                // Click the Save Pet button
                page.locator("button#save-pet").click()

                // Wait for the form to be removed
                page.waitForFunction("!document.querySelector('input[data-bind\\\\:-new]')")

                // Wait a bit for the page to update
                page.waitForTimeout(300.0)

                // Verify the pet was added
                val finalPetRows = page.querySelectorAll("tr[id^='row-pet-']").size
                assertEquals(initialPetRows + 1, finalPetRows, "Should have one more pet after saving")

                // Verify the new pet appears in the table
                val pageText = page.content()
                assert(pageText.contains("TestPet")) { "Page should contain the new pet name 'TestPet'" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `cancel adding pet removes form without saving`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to owner details page
                val url = "http://localhost:$port/owners/1"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready
                page.waitForSelector("button:has-text('Add New Pet')")

                // Get the initial pet count
                val initialPetRows = page.querySelectorAll("tr[id^='row-pet-']").size

                // Click the Add New Pet button
                page.locator("button:has-text('Add New Pet')").click()

                // Wait for the form to appear
                page.waitForSelector("input[data-bind\\:name-new]")

                // Verify that action buttons are disabled while form is open
                val editPetButton = page.locator("button:has-text('Edit Pet')")
                val editOwnerButton = page.locator("button:has-text('Edit Owner')")
                val addNewPetButton = page.locator("button:has-text('Add New Pet')")

                assert(editPetButton.isDisabled) { "Edit Pet button should be disabled while adding pet" }
                assert(editOwnerButton.isDisabled) { "Edit Owner button should be disabled while adding pet" }
                assert(addNewPetButton.isDisabled) { "Add New Pet button should be disabled while form is open" }

                // Fill in the pet form
                val nameInput = page.locator("input[data-bind\\:name-new]")
                nameInput.fill("TestPet")

                // Click the Cancel Pet button
                page.locator("button#cancel-pet").click()

                // Wait for the form to be removed
                page.waitForFunction("!document.querySelector('input[data-bind\\\\:-new]')")

                // Verify the pet was NOT added
                val finalPetRows = page.querySelectorAll("tr[id^='row-pet-']").size
                assertEquals(initialPetRows, finalPetRows, "Pet count should remain the same after cancel")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}

