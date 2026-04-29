package org.springframework.samples.petclinic.views.fragments

import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.attributes.dataBind

fun Div<*>.partialInputField(
    label: String,
    id: String,
    value: Any,
    signal: Signal<Any?>,
    type: EnumTypeInputType = EnumTypeInputType.TEXT,
) {
    div {
        attrClass("form-group")
        label {
            attrClass("col-sm-2 control-label")
            text(label)
        }
        div {
            attrClass("col-sm-10")
            div {
                input {
                    attrClass("form-control")
                    attrType(type)
                    attrId(id)
                    attrName(id)
                    dataBind(signal)
                    attrValue(value.toString())
                }
            }
            span {
                attrClass("fa fa-ok form-control-feedback")
                addAttr("aria-hidden", "true")
            }
        }
    }
}
