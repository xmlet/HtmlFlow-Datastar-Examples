package org.springframework.samples.petclinic.pet

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.WaitForSelectorState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.samples.petclinic.infrastructure.SpringBootApplicationServer
import org.springframework.samples.petclinic.infrastructure.SpringBootServerExtension
import kotlin.test.assertEquals

/**
 * End-to-end tests for editing existing pets.
 * Tests the pet edit form, updating pet information, and canceling edits.
 */
@ExtendWith(SpringBootServerExtension::class)
class PetEditE2eTest {
    @Test
    fun `edit pet form displays with current pet data`() {
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

                // Wait for the page to be ready with at least one pet
                page.waitForSelector("button#pet-edit")

                // Click on the first pet edit button
                page.locator("button#pet-edit").first().click()

                // Wait a bit for the form to appear
                page.waitForTimeout(500.0)

                // The edit form should now be displayed (check for edit buttons)
                val saveEditButton = page.querySelector("button#save-edit-pet")
                val cancelEditButton = page.querySelector("button#cancel-pet")

                assert(saveEditButton != null) { "Save edit button should be present" }
                assert(cancelEditButton != null) { "Cancel button should be present" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `edit pet information and save updates pet`() {
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
                page.waitForSelector("button#pet-edit")

                // Get the first pet ID
                val petRow = page.querySelector("tr[id^='row-pet-']")
                val petId = petRow?.getAttribute("id")?.substringAfter("row-pet-")

                // Click on the first pet edit button
                page.locator("button#pet-edit").first().click()

                // Wait for the form to be ready
                page.waitForTimeout(500.0)

                // Find the input fields using the pet ID as suffix
                val nameInput = page.locator("input[data-bind\\:name$petId]")
                val birthDateInput = page.locator("input[data-bind\\:birth-date$petId]")

                // Modify the pet information
                nameInput.fill("UpdatedPetName")
                birthDateInput.fill("2019-06-10")

                // Click the Save button
                page.locator("button#save-edit-pet").click()

                // Wait for the form to be saved
                page.waitForFunction("!document.querySelector('input[data-bind\\\\:name$petId]')")

                // Wait a bit for the page to update
                page.waitForTimeout(300.0)

                // Verify updated details are displayed
                val pageText = page.content()
                assert(pageText.contains("UpdatedPetName")) { "Page should contain updated pet name 'UpdatedPetName'" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `cancel pet edit reverts changes without saving`() {
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
                page.waitForSelector("button#pet-edit")

                // Get the first pet's current name
                val petRow = page.querySelector("tr[id^='row-pet-']")

                // Get the pet ID
                val petId = petRow?.getAttribute("id")?.substringAfter("row-pet-")

                // Click on the first pet edit button
                page.locator("button#pet-edit").first().click()

                // Wait for the form to appear
                page.waitForTimeout(500.0)

                // Find the input fields using the pet ID as suffix
                val nameInput = page.locator("input[data-bind\\:name$petId]")

                // Store the original value
                val originalName = nameInput.inputValue()

                // Modify the pet information
                nameInput.fill("TemporaryName")

                // Click the Cancel button
                page.locator("button#cancel-pet").click()

                // Wait for the form to be hidden
                page.waitForSelector(
                    "input[data-bind\\:name$petId]",
                    com.microsoft.playwright.Page
                        .WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN),
                )

                // Wait a bit for the page to update
                page.waitForTimeout(300.0)

                // Verify the pet name was NOT changed
                val pageText = page.content()
                assert(pageText.contains(originalName)) { "Page should still contain original pet name after cancel" }
                assert(!pageText.contains("TemporaryName")) { "Temporary name should not appear after cancel" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}

