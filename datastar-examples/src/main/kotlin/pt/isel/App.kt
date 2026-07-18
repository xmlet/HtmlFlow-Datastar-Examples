package pt.isel

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.EncodeDefault
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.http4k.demosHttp4kRouting
import pt.isel.ktor.demosKtorRouting
import java.lang.System.getenv

private val logger = LoggerFactory.getLogger("MultiServerDemo")

enum class ServerType {
    KTOR,
    HTTP4K,
    BOTH,
}

fun main() {
    logger.info("Starting servers...")
    val server =
        getenv("SERVER_TYPE")
            ?.uppercase()
            ?.let(ServerType::valueOf)
            ?: ServerType.BOTH

    when (server) {
        ServerType.KTOR -> {
            startKtor(server)
        }

        ServerType.HTTP4K -> {
            startHttp4k(server)
        }

        else -> {
            startKtor(server)
            startHttp4k(server)
        }
    }
}

fun readPort(default: Int): Int = getenv("PORT")?.toInt() ?: default

fun startKtor(kind: ServerType) {
    val port = if (kind == ServerType.BOTH) 8080 else readPort(8080)
    val ktorServer = embeddedServer(Netty, port) { demosKtorRouting() }
    logger.info("Ktor running on http://localhost:$port")
    Runtime.getRuntime().addShutdownHook(
        Thread {
            logger.info("Shutting down Ktor...")
            ktorServer.stop(1000, 2000)
        },
    )
    ktorServer.start(wait = true)
}

fun startHttp4k(kind: ServerType) {
    val port = if (kind == ServerType.BOTH) 8070 else readPort(8070)
    val http4kServer = demosHttp4kRouting.asServer(Jetty(port)).start()
    logger.info("http4k running on http://localhost:$port")
    Runtime.getRuntime().addShutdownHook(
        Thread {
            logger.info("Shutting down Http4k...")
            http4kServer.stop()
        },
    )
}
