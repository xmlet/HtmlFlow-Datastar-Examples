package pt.isel

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import pt.isel.ktor.demosKtorRouting
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExampleMetadataTest {
    @ParameterizedTest
    @CsvSource(
        "active-search, Active Search",
        "bulk-update, Bulk Update",
        "click-to-edit, Click to Edit",
        "click-to-edit-signals, Click to Edit via Signals",
        "click-to-load, Click to Load",
        "counter, Counter",
        "counter-signals, Counter via Signals",
        "delete-row, Delete Row",
        "edit-row, Edit Row",
        "file-upload, File Upload",
        "infinite-scroll, Infinite Scroll",
        "inline-validation, Inline Validation",
        "lazy-load, Lazy Load",
        "lazy-tabs, Lazy Tabs",
        "progress-bar, Progress Bar",
        "progressive-load, Progressive Load",
        "todo-mvc, Todo MVC",
        "complex-domain, Signal Complex Domain",
    )
    fun `example tabs identify their name and view implementation`(
        example: String,
        name: String,
    ) = testApplication {
        application { demosKtorRouting() }

        val implementations =
            if (example in listOf("counter", "complex-domain")) {
                listOf("html" to "HTML")
            } else {
                listOf("html" to "HTML", "htmlflow" to "HtmlFlow")
            }
        implementations.forEach { (path, label) ->
            val response = client.get("/$example/$path")
            assertEquals(HttpStatusCode.OK, response.status)
            assertMetadata(response.bodyAsText(), "$name — $label")
        }
    }

    @Test
    fun `index uses the HtmlFlow favicon and the image is served`() =
        testApplication {
            application { demosKtorRouting() }

            val response = client.get("/")
            assertEquals(HttpStatusCode.OK, response.status)
            assertMetadata(response.bodyAsText(), "Datastar Demos - Table of Contents")

            val favicon = client.get("/images/htmlflow-logo.png")
            assertEquals(HttpStatusCode.OK, favicon.status)
            assertTrue(favicon.headers["Content-Type"].orEmpty().startsWith("image/png"))
        }

    private fun assertMetadata(
        html: String,
        expectedTitle: String,
    ) {
        val head = html.substringAfter("<head>").substringBefore("</head>")
        assertTrue(head.contains("charset=\"UTF-8\""))
        val titles = Regex("<title>\\s*(.*?)\\s*</title>").findAll(head).map { it.groupValues[1] }.toList()
        assertEquals(listOf(expectedTitle), titles)
        assertTrue(
            Regex("<link[^>]*rel=\"icon\"[^>]*href=\"/images/htmlflow-logo.png\"[^>]*>").containsMatchIn(head),
            "Missing HtmlFlow favicon in $expectedTitle",
        )
    }
}
