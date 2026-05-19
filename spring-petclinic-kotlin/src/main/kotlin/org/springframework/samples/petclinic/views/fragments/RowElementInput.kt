package org.springframework.samples.petclinic.views.fragments

import org.xmlet.htmlapifaster.Dd
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.Tr
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataIndicator

internal fun Tr<*>.rowElementInput(
    signalName: String,
    type: EnumTypeInputType = EnumTypeInputType.TEXT,
) {
    td {
        input {
            attrType(type)
            dataBind(signalName)
            val fetching = dataIndicator("_fetching")
            dataAttr("disabled") { +fetching }
        }
    }
}

internal fun Dd<*>.rowElementInputInline(
    signalName: String,
    type: EnumTypeInputType = EnumTypeInputType.TEXT,
) {
    input {
        attrType(type)
        dataBind(signalName)
        val fetching = dataIndicator("_fetching")
        dataAttr("disabled") { +fetching }
    }
}
