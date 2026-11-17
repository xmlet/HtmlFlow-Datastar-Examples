package pt.isel

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import pt.isel.ktor.demosKtorRouting
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SourceLinksTest {
    @ParameterizedTest
    @CsvSource(
        "active-search, ActiveSearch, ActiveSearch",
        "bulk-update, BulkUpdate, BulkUpdate",
        "click-to-edit, ClickToEdit, ClickToEdit",
        "click-to-edit-signals, ClickToEditViaSignals, ClickToEditViaSignals",
        "click-to-load, ClickToLoad, ClickToLoad",
        "counter, Counter, CounterView",
        "counter-signals, CounterViaSignals, CounterViaSignals",
        "delete-row, DeleteRow, DeleteRow",
        "edit-row, EditRow, EditRow",
        "file-upload, FileUpload, FileUpload",
        "infinite-scroll, InfiniteScroll, InfiniteScroll",
        "inline-validation, InlineValidation, InlineValidation",
        "lazy-load, LazyLoad, LazyLoad",
        "lazy-tabs, LazyTabs, LazyTabs",
        "progress-bar, ProgressBar, ProgressBar",
        "progressive-load, ProgressiveLoad, ProgressiveLoad",
        "todo-mvc, TodoMvc, TodoMvc",
    )
    fun `example description links to its views and Ktor server`(
        example: String,
        server: String,
        view: String,
    ) = testApplication {
        application { demosKtorRouting() }

        val response = client.get("/$example/description")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertSourceLinks(
            body,
            "resources/public/html/$example.html",
            "kotlin/pt/isel/views/htmlflow/$view.kt",
            "kotlin/pt/isel/ktor/Demo$server.kt",
        )
    }

    @Test
    fun `complex domain links to the HTML and Ktor code it serves`() =
        testApplication {
            application { demosKtorRouting() }

            val response = client.get("/complex-domain/html")
            assertEquals(HttpStatusCode.OK, response.status)
            assertSourceLinks(
                response.bodyAsText(),
                "resources/public/html/signal-complex-domain.html",
                "kotlin/pt/isel/ktor/DemoSignalComplexDomain.kt",
            )
        }

    private fun assertSourceLinks(
        body: String,
        vararg paths: String,
    ) {
        val root = "https://github.com/xmlet/HtmlFlow-Datastar-Examples/blob/main/datastar-examples/src/main/"
        assertTrue(body.contains("Source code:"))
        assertTrue(body.contains("Ktor server"))
        assertFalse(body.contains("${root}kotlin/pt/isel/http4k/"))
        paths.forEach { path ->
            assertTrue(body.contains("href=\"$root$path\""), "Missing source link: $path")
            assertTrue(Files.isRegularFile(Path.of("src/main", path)), "Source file does not exist: $path")
        }
        assertEquals(paths.size, Regex(Regex.escape(root)).findAll(body).count())
    }
}
