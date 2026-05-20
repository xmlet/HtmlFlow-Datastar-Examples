package org.springframework.samples.petclinic.infrastructure

import org.springframework.boot.SpringApplication
import org.springframework.samples.petclinic.PetClinicApplication
import org.springframework.web.context.WebApplicationContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Singleton that manages a running Spring Boot application server for E2E testing.
 * The server is started once and reused across all tests.
 */
object SpringBootApplicationServer {
    private var applicationContext: WebApplicationContext? = null
    private val port = AtomicInteger(0)

    @Synchronized
    fun startIfNeeded(): Int {
        if (applicationContext == null) {
            println("Starting Spring Boot application server for E2E tests...")

            val app = SpringApplication(PetClinicApplication::class.java)
            app.setAdditionalProfiles("test")

            // Find an available port
            val availablePort = findAvailablePort()
            app.setDefaultProperties(mapOf("server.port" to availablePort))

            val context = app.run() as WebApplicationContext
            applicationContext = context
            port.set(availablePort)

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(Thread {
                shutdown()
            })

            println("Spring Boot application server started on port $availablePort")
        }

        return port.get()
    }

    @Synchronized
    fun shutdown() {
        if (applicationContext != null) {
            println("Shutting down Spring Boot application server...")
            applicationContext = null
            port.set(0)
        }
    }

    private fun findAvailablePort(): Int {
        val socket = java.net.ServerSocket(0)
        val port = socket.localPort
        socket.close()
        return port
    }
}

