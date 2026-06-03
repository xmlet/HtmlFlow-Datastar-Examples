package org.springframework.samples.petclinic.owner

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.samples.petclinic.infrastructure.SpringBootApplicationServer
import org.springframework.samples.petclinic.infrastructure.SpringBootServerExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * End-to-end tests for the Owner form validation.
 * Tests form field validation, particularly telephone number validation.
 */
@ExtendWith(SpringBootServerExtension::class)
class OwnerFormValidationE2eTest {
    @Test
    fun `create owner with empty fields shows validation error`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to add owner page
                val url = "http://localhost:$port/owners/new"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the form to be ready
                page.waitForSelector("form#add-owner-form")

                // Fill some fields but leave address and telephone empty (to trigger validation error)
                page.locator("input[id='firstName']").fill("John")
                page.locator("input[id='lastName']").fill("Doe")
                page.locator("input[id='city']").fill("Portland")
                // Leave address and telephone empty

                // Click the Add Owner button
                page.locator("button:has-text('Add Owner')").click()

                // Wait for the error message to appear (error div should be visible)
                page.waitForSelector("div.error")

                // Verify the error message is displayed
                val errorDiv = page.querySelector("div.error")
                assertNotNull(errorDiv, "Error message should be displayed")

                val errorMessage = errorDiv.textContent()
                assert(errorMessage?.contains("Either fields are empty") == true) {
                    "Error message should mention empty fields"
                }

                // Verify the form is still visible (not saved)
                val form = page.querySelector("form#add-owner-form")
                assertNotNull(form, "Form should still be visible after validation error")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `invalid telephone shows validation error`() {
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

                // Wait for the edit form to appear (using data-bind: attributes)
                page.waitForSelector("input[data-bind\\:first-name]")
                page.waitForSelector("input[data-bind\\:telephone]")

                // Clear and set invalid telephone (more than 10 digits)
                val telephoneInput = page.locator("input[data-bind\\:telephone]")
                telephoneInput.fill("12345678901")

                // Click the Save button
                page.locator("#save-owner").click()

                // Wait for error message to appear
                page.waitForSelector("div.error")

                // Verify the error message is displayed
                val errorDiv = page.querySelector("div.error")
                assertNotNull(errorDiv, "Error message should be displayed")

                val errorMessage = errorDiv.textContent()
                assert(errorMessage?.contains("Either fields are empty or telephone number has other characters") == true) {
                    "Error message should mention empty fields or telephone error"
                }

                // Verify the edit form is still visible (not saved)
                val editForm = page.querySelector("input[data-bind\\:first-name]")
                assertNotNull(editForm, "Edit form should still be visible after validation error")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `valid telephone saves successfully`() {
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

                // Wait for the edit form to appear (using data-bind: attributes)
                page.waitForSelector("input[data-bind\\:first-name]")
                page.waitForSelector("input[data-bind\\:telephone]")

                // Set valid telephone (exactly 10 digits or less)
                val telephoneInput = page.locator("input[data-bind\\:telephone]")
                telephoneInput.fill("5551234567")

                // Click the Save button
                page.locator("#save-owner").click()

                // Wait for the form to disappear (successful save)
                page.waitForFunction("!document.querySelector('input[data-bind\\\\:first-name]')")

                // Verify no error message is displayed
                val errorDiv = page.querySelector("div.error")
                assert(errorDiv == null) { "No error message should be displayed for valid telephone" }

                // Verify we're back to the display view
                val pageText = page.content()
                assert(!pageText.contains("input[data-bind\\:first-name")) { "Edit form should be hidden after save" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}
