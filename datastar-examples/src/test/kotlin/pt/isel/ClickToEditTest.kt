package pt.isel

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pt.isel.infrastructure.SharedTestServers
import pt.isel.infrastructure.SharedTestServersExtension
import kotlin.test.assertEquals
import kotlin.use

@ExtendWith(SharedTestServersExtension::class)
class ClickToEditTest {
    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes via signals on HTML`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit-signals/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes via signals on HtmlFlow`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit-signals/htmlflow", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes on HTML`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit/html", serverType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ktor", "Http4k"])
    fun `click to edit user details and save changes on HtmlFlow`(serverType: String) {
        `click to edit user details and save changes`("/click-to-edit/htmlflow", serverType)
    }

    /**
     * Tests that clicking to edit user details allows editing, saving changes updates the details correctly,
     * canceling reverts to original details, and resetting restores default user details.
     */
    private fun `click to edit user details and save changes`(
        path: String,
        serverType: String,
    ) {
        val port = SharedTestServers.getPort(serverType)

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to the click-to-edit page
                val url = "http://localhost:$port$path"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page to be ready - buttons should be visible
                page.waitForSelector("#edit")
                page.waitForSelector("#reset")

                // Click the Edit button
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                // Modify user details
                page.getByLabel("First Name").fill("Alice")
                page.getByLabel("Last Name").fill("Smith")
                page.getByLabel("Email").fill("alice.smith@org.com")

                // Click the Save button
                page.locator("#save").click()

                // Verify updated details are displayed
                assertProfile(page, "Alice", "Smith", "alice.smith@org.com")

                // Click the Edit button again
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                // Modify user details again
                page.getByLabel("First Name").fill("Bob")
                page.getByLabel("Last Name").fill("Johnson")
                page.getByLabel("Email").fill("bob.jonhson@email.com")
                // Click the Cancel button
                page.locator("#cancel").click()

                // Verify original details are retained
                assertThat(page.locator("#edit")).isVisible()
                assertProfile(page, "Alice", "Smith", "alice.smith@org.com")

                // Click the Reset button and wait for the default details to be displayed
                page.locator("#reset").click()
                // Verify default user details are restored
                assertProfile(page, "John", "Doe", "joe@blow.com")

                // Verify that the reset operation completed successfully
                page.locator("#edit").click()
                page.waitForSelector("label:has-text('First Name')")

                assertThat(page.getByLabel("First Name")).hasValue("John")
                assertThat(page.getByLabel("Last Name")).hasValue("Doe")
                assertThat(page.getByLabel("Email")).hasValue("joe@blow.com")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    private fun assertProfile(page: Page, firstName: String, lastName: String, email: String) {
        assertThat(page.locator("p:has-text('First Name')")).hasText("First Name: $firstName")
        assertThat(page.locator("p:has-text('Last Name')")).hasText("Last Name: $lastName")
        assertThat(page.locator("p:has-text('Email')")).hasText("Email: $email")
    }
}
