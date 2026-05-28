package org.springframework.samples.petclinic.owner

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
 * End-to-end tests for the Owner Details page and edit workflow.
 * Tests viewing owner details, editing information, saving changes, and canceling edits.
 */
@ExtendWith(SpringBootServerExtension::class)
class OwnerDetailsE2eTest {
    @Test
    fun `view owner details displays correct information`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate directly to an owner's details page (using owner ID 1)
                val url = "http://localhost:$port/owners/1"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready
                page.waitForSelector("h2")
                page.waitForSelector("table")

                // Verify that owner information is displayed
                val pageText = page.content()
                assert(pageText.contains("Owner Information")) { "Page should contain 'Owner Information'" }
                assert(pageText.contains("George")) { "Page should contain owner name 'George'" }
                assert(pageText.contains("Franklin")) { "Page should contain owner last name 'Franklin'" }

                // Verify the Edit Owner button is present
                val editButton = page.querySelector("button:has-text('Edit Owner')")
                assert(editButton != null) { "Edit Owner button should be present" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `edit owner information and save changes updates details`() {
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
                page.waitForSelector("button:has-text('Edit Owner')")
                page.waitForSelector("tbody#owner-table-body")

                // Click the Edit Owner button
                page.locator("button:has-text('Edit Owner')").click()

                // Wait a bit for the page to update
                page.waitForTimeout(500.0)

                // Wait for the edit form to appear with input fields (using data-bind: attributes)
                page.waitForSelector("input[data-bind\\:first-name]")
                page.waitForSelector("input[data-bind\\:last-name]")
                page.waitForSelector("input[data-bind\\:address]")
                page.waitForSelector("input[data-bind\\:city]")
                page.waitForSelector("input[data-bind\\:telephone]")

                // Get the current values
                val firstNameInput = page.locator("input[data-bind\\:first-name]")
                val lastNameInput = page.locator("input[data-bind\\:last-name]")
                val addressInput = page.locator("input[data-bind\\:address]")
                val cityInput = page.locator("input[data-bind\\:city]")
                val telephoneInput = page.locator("input[data-bind\\:telephone]")

                // Modify the owner information
                firstNameInput.fill("George2")
                lastNameInput.fill("Franklin2")
                addressInput.fill("Updated Address")
                cityInput.fill("Updated City")
                telephoneInput.fill("5555555555")

                // Click the Save button
                page.locator("#save-owner").click()

                // Wait for the form to be saved and update
                page.waitForFunction("!document.querySelector('input[data-bind\\\\:first-name]')")

                // Verify updated details are displayed
                val pageText = page.content()
                assert(pageText.contains("George2")) { "Page should contain updated name 'George2'" }
                assert(pageText.contains("Franklin2")) { "Page should contain updated last name 'Franklin2'" }
                assert(pageText.contains("Updated Address")) { "Page should contain updated address" }
                assert(pageText.contains("Updated City")) { "Page should contain updated city" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `cancel edit operation reverts changes without saving`() {
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
                page.waitForSelector("button:has-text('Edit Owner')")

                // Click the Edit Owner button
                page.locator("button:has-text('Edit Owner')").click()

                // Wait a bit for the page to update
                page.waitForTimeout(500.0)

                // Wait for the edit form to appear (using data-bind: attributes)
                page.waitForSelector("input[data-bind\\:first-name]")

                // Get original values
                val firstNameInput = page.locator("input[data-bind\\:first-name]")
                val originalFirstName = firstNameInput.inputValue()

                // Modify the owner information
                firstNameInput.fill("ModifiedName")
                page.locator("input[data-bind\\:last-name]").fill("ModifiedLastName")

                // Click the Cancel button
                page.locator("#cancel-edit").click()

                // Wait for the form to be hidden
                page.waitForSelector(
                    "input[data-bind\\:first-name]",
                    com.microsoft.playwright.Page
                        .WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN),
                )

                // Wait for the default view to show
                page.waitForTimeout(300.0)

                // Verify original details are retained
                val pageText = page.content()
                assert(pageText.contains(originalFirstName)) { "Page should contain original first name after cancel" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
