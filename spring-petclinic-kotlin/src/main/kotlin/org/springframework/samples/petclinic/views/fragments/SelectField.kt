package org.springframework.samples.petclinic.views.fragments

import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.option
import org.xmlet.htmlapifaster.select
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.attributes.dataBind

fun Div<*>.partialSelectField(
    id: String,
    data: Iterable<*>,
    selected: String,
    signal: Signal<Any?>,
) = div {
    attrClass("form-group")
    label {
        attrClass("col-sm-2 control-label")
        text("Type")
    }
    div {
        attrClass("col-sm-10")
        select {
            attrId(id)
            attrName(id)
            dataBind(signal)
            data.forEach { item ->
                option {
                    attrValue(item.toString())
                    if (item.toString() == selected) {
                        attrSelected(true)
                    }
                    text(item.toString())
                }
            }
        }
        span {
            attrClass("glyphicon glyphicon-ok form-control-feedback")
            addAttr("aria-hidden", "true")
        }
    }
}
