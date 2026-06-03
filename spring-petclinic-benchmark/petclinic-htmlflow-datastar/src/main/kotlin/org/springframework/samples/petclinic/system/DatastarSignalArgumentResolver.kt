package org.springframework.samples.petclinic.system

import kotlinx.serialization.json.Json
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class DatastarSignalArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.hasParameterAnnotation(DatastarSignal::class.java)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val datastarJson = webRequest.getParameter("datastar") ?: return null
        val parameterType = parameter.parameterType

        val companion = Class.forName(parameterType.name + $$"$Companion").kotlin.objectInstance
        val serializerMethod = companion!!::class.java.methods.find { it.name == "serializer" }
        val serializer = serializerMethod!!.invoke(companion)

        val decodeMethod =
            Json::class.java.methods.find {
                it.name == "decodeFromString" && it.parameterCount == 2
            }

        val json = Json { ignoreUnknownKeys = true }
        return decodeMethod!!.invoke(json, serializer, datastarJson)
    }
}
