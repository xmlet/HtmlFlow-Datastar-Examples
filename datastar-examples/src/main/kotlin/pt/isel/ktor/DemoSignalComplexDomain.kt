package pt.isel.ktor

import dev.datastar.kotlin.sdk.ServerSentEventGenerator
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.response.respondTextWriter
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import jakarta.ws.rs.Path
import pt.isel.utils.loadResource
import pt.isel.utils.response

private val html = loadResource("public/html/signal-complex-domain.html")

fun Route.demoSignalComplexDomain() {
    route("/complex-domain") {
        get("/html", RoutingContext::getComplexDomainHtmlFlow)
        put("/switch-user", RoutingContext::switchUser)
        put("/increase-age", RoutingContext::updateUserAge)
    }
}

private val people =
    mutableListOf(
        Person("Jonh", 18),
        Person("Mary", 25),
        Person("Alice", 30),
    )
var currentUserIdx = 0

suspend fun RoutingContext.getComplexDomainHtmlFlow() {
    call.respondText(html, ContentType.Text.Html)
}

@Path("/complex-domain/switch-user")
suspend fun RoutingContext.switchUser() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        if (currentUserIdx == people.size - 1) {
            currentUserIdx = 0
        } else {
            currentUserIdx++
        }
        val generator = ServerSentEventGenerator(response(this))
        generator.patchSignals(
            "{person: {name: '${people[currentUserIdx].name}', age: ${people[currentUserIdx].age}}}",
        )
    }
}

@Path("/complex-domain/increase-age")
suspend fun RoutingContext.updateUserAge() {
    call.respondTextWriter(
        status = OK,
        contentType = ContentType.Text.EventStream,
    ) {
        val generator = ServerSentEventGenerator(response(this))
        generator.patchSignals(
            "{person : { age: ${people[currentUserIdx].age + 1}}}",
        )

        people[currentUserIdx] = people[currentUserIdx].copy(age = people[currentUserIdx].age + 1)
    }
}

data class Person(
    val name: String,
    val age: Int,
)
