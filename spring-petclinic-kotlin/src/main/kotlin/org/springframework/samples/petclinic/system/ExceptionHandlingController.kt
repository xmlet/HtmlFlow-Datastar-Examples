package org.springframework.samples.petclinic.system

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.samples.petclinic.views.error
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ExceptionHandlingController {
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleError(
        req: HttpServletRequest?,
        ex: Exception,
    ): ResponseEntity<String> =
        ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_HTML)
            .body(
                layout { error(ex) }.render(),
            )
}
