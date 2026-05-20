package org.springframework.samples.petclinic.views

import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.img

fun Div<*>.welcome() {
    div {
        h2 { text("Welcome") }
        div {
            attrClass("row")
            div {
                attrClass("col-md-12")
                img {
                    attrClass("img-responsive")
                    attrSrc("/resources/images/pets.png")
                }
            }
        }
    }
}
