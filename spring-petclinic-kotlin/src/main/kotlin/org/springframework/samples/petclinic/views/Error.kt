package org.springframework.samples.petclinic.views

import htmlflow.dyn
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.img
import org.xmlet.htmlapifaster.p

fun Div<*>.error(ex: Exception) {
    img {
        attrClass("img-responsive")
        attrSrc("/resources/images/pets.png")
    }
    h2 { text("Something happened...") }
    p { text(ex.message) }
}
