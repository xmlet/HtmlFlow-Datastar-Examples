package org.springframework.samples.petclinic.system

import dev.datastar.kotlin.sdk.blocking.Response
import java.io.OutputStream
import java.io.OutputStreamWriter

fun adapterResponse(stream: OutputStream): Response =
    object : Response {
        private val writer = OutputStreamWriter(stream)

        override fun sendConnectionHeaders(
            status: Int,
            headers: Map<String, List<String>>,
        ) = Unit

        override fun write(text: String) = writer.write(text)

        override fun flush() = writer.flush()
    }
