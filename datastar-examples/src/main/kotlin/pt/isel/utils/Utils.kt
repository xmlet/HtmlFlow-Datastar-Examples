package pt.isel.utils

import dev.datastar.kotlin.sdk.coroutines.Response
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Loads a resource file from the classpath and returns its content as a String.
 *
 * @param path The path to the resource file relative to the classpath.
 * @return The content of the resource file as a String.
 * @throws IllegalArgumentException if the resource is not found.
 */
fun loadResource(path: String): String =
    object {}
        .javaClass.classLoader
        .getResource(path)
        ?.readText() ?: throw IllegalArgumentException("Resource not found:$path")

/**
 * Get a resource absolute Path from the classpath and returns its.
 *
 * @param path The path to the resource file relative to the classpath.
 * @return The url to the resource file as a String.
 */
fun getResourcePath(path: String): Path =
    object {}
        .javaClass.classLoader
        .getResource(path)
        ?.let { url ->
            Paths.get(url.toURI())
        } ?: throw IllegalArgumentException("Resource not found:$path")

/**
 * Creates a `Response` implementation that interacts with java.io.Writer.
 *
 * @return A `Response` implementation for sending headers and writing data to the response.
 */
fun response(channel: ByteWriteChannel): Response =
    object : Response {
        override suspend fun sendConnectionHeaders(
            status: Int,
            headers: Map<String, List<String>>,
        ) {
            // Ktor already set status and Content-Type on respondBytesWriter.
        }

        override suspend fun write(text: String) {
            channel.writeStringUtf8(text)
        }

        override suspend fun flush() {
            channel.flush()
        }
    }
