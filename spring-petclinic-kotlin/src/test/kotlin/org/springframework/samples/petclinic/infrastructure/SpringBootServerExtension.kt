package org.springframework.samples.petclinic.infrastructure

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * JUnit 5 Extension that ensures the Spring Boot application server is running before tests execute.
 * The server is started once and reused across all tests in the extension context.
 */
class SpringBootServerExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        // Trigger initialization of the application server
        SpringBootApplicationServer.startIfNeeded()
    }
}

