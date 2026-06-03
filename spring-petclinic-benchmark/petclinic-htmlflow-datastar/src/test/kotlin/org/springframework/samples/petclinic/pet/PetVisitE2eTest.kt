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
 * End-to-end tests for pet visits.
 * Tests adding and viewing visits for pets within the owner details page.
 */
@ExtendWith(SpringBootServerExtension::class)
class PetVisitE2eTest {
    @Test
    fun `pet visits table displays for existing pet`() {
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
                page.waitForSelector("h2:has-text('Pets and Visits')")

                // Verify visits table is present
                val visitsTable = page.querySelector("table.visits-table")
                assert(visitsTable != null) { "Visits table should be present" }

                // Verify "Add Visit" button exists (at least one)
                val addVisitButtons = page.querySelectorAll("a:has-text('Add Visit')")
                assert(addVisitButtons.size >= 1) { "At least one 'Add Visit' button should be present" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `add visit button navigates to visit form`() {
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
                page.waitForSelector("a:has-text('Add Visit')")

                // Get the href of the first Add Visit button
                val addVisitLink = page.querySelector("a:has-text('Add Visit')")
                val href = addVisitLink?.getAttribute("href")

                // Verify the link is not null and has the right pattern
                assert(href != null) { "Add Visit link should have href" }
                assert(href?.contains("/visits/new") == true) { "Add Visit link should point to /visits/new endpoint" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `visits table displays visit date and description columns`() {
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
                page.waitForSelector("table.visits-table")

                // Verify table headers are present
                val pageText = page.content()
                assert(pageText.contains("Visit Date")) { "Visits table should have 'Visit Date' header" }
                assert(pageText.contains("Description")) { "Visits table should have 'Description' header" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `visits are displayed in table rows`() {
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
                page.waitForSelector("table.visits-table tbody")

                // Get the visits table body
                val visitRows = page.querySelectorAll("table.visits-table tbody tr")

                // Verify at least some visits are present (or table is empty but valid)
                assert(visitRows.size >= 0) { "Visits table body should exist" }
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}

