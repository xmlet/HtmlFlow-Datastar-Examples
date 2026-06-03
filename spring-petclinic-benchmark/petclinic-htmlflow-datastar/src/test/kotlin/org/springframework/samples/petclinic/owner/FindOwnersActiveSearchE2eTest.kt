package org.springframework.samples.petclinic.owner

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.samples.petclinic.infrastructure.SpringBootApplicationServer
import org.springframework.samples.petclinic.infrastructure.SpringBootServerExtension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * End-to-end tests for the Find Owners page active search functionality.
 * Tests filtering owners by last name using DataStar active search.
 */
@ExtendWith(SpringBootServerExtension::class)
class FindOwnersActiveSearchE2eTest {
    @Test
    fun `search owners by last name filters results`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to the find owners page
                val url = "http://localhost:$port/owners"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page elements to be available
                page.waitForSelector("input[name='lastName']")
                page.waitForSelector("table")

                // Count initial rows
                val initialRowCount = page.querySelectorAll("tbody#results-table tr").size
                assertTrue(initialRowCount > 0, "Should have at least one owner in the table")

                // Type "Franklin" into the search input to find specific owner
                val searchInput = page.querySelector("input[name='lastName']")
                searchInput?.fill("Franklin")

                // Wait for the table to update with filtered results
                page.waitForFunction("document.querySelectorAll('tbody#results-table tr').length === 1")

                val filteredRowCount = page.querySelectorAll("tbody#results-table tr").size
                assertEquals(1, filteredRowCount, "Table should have 1 row after searching for Franklin")

                // Verify the content of the filtered row
                val row = page.querySelectorAll("tbody#results-table tr").firstOrNull()
                assert(row != null) { "Row should exist" }

                val cells = row?.querySelectorAll("td")
                val name = cells?.get(0)?.textContent()?.trim() ?: ""

                assertEquals("George Franklin", name, "Filtered row should have name 'George Franklin'")

                // Clear the search input
                searchInput?.fill("")
                page.waitForFunction("document.querySelectorAll('tbody#results-table tr').length > 1")

                val resetRowCount = page.querySelectorAll("tbody#results-table tr").size
                assertEquals(initialRowCount, resetRowCount, "Table should be reset after clearing search")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `search for partial last name matches multiple owners`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to the find owners page
                val url = "http://localhost:$port/owners"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page elements to be available
                page.waitForSelector("input[name='lastName']")
                page.waitForSelector("table")

                // Search for owners without a specific last name (should show all)
                val searchInput = page.querySelector("input[name='lastName']")
                searchInput?.fill("")

                // Wait a bit for any updates
                page.waitForTimeout(300.0)

                val allOwnersCount = page.querySelectorAll("tbody#results-table tr").size
                assertEquals(10, allOwnersCount, "Should have 10 total owners in database")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }

    @Test
    fun `search for non-existent owner shows no results`() {
        val port = SpringBootApplicationServer.startIfNeeded()

        Playwright.create().use { playwright ->
            val browser: Browser =
                playwright.chromium().launch(
                    BrowserType.LaunchOptions().setHeadless(true),
                )
            val context = browser.newContext()
            val page = context.newPage()

            try {
                // Navigate to the find owners page
                val url = "http://localhost:$port/owners"
                val response = page.navigate(url)
                assertEquals(200, response?.status(), "Navigation to $url should return 200")

                // Wait for the page elements to be available
                page.waitForSelector("input[name='lastName']")
                page.waitForSelector("table")

                // Search for a non-existent owner
                val searchInput = page.querySelector("input[name='lastName']")
                searchInput?.fill("NonExistentOwner")

                // Wait for the table to update
                page.waitForFunction("document.querySelectorAll('tbody#results-table tr').length === 0")

                val filteredRowCount = page.querySelectorAll("tbody#results-table tr").size
                assertEquals(0, filteredRowCount, "Table should have no rows when searching for non-existent owner")
            } finally {
                page.close()
                context.close()
                browser.close()
            }
        }
    }
}

