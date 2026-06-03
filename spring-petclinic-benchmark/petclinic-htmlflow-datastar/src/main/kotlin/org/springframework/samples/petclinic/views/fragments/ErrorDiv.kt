package org.springframework.samples.petclinic.views.fragments

import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.label

internal fun Div<*>.errorDiv(errorMsg: String) {
    div {
        attrId("error")
        attrClass("error")
        label {
            text(errorMsg)
        }
    }
}
