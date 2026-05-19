package org.springframework.samples.petclinic.views.owners

import htmlflow.HtmlView
import htmlflow.dyn
import org.springframework.samples.petclinic.views.fragments.errorDiv
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.samples.petclinic.views.fragments.partialInputField
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumMethodType
import org.xmlet.htmlapifaster.EnumTypeButtonType
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.form
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlflow.datastar.attributes.dataSignal

@Component
class OwnersCreate {
    val view: HtmlView<Any> = layout { ownersCreate() }

    val errorView: HtmlView<Any> =
        layout {
            dyn { errorMsg: String ->
                ownersCreate(errorMsg)
            }
        }

    private fun Div<*>.ownersCreate(errorMsg: String = "") {
        h2 { text("Owner") }
        form {
            attrClass("form-horizontal")
            attrId("add-owner-form")
            attrMethod(EnumMethodType.POST)
            div {
                val firstName = dataSignal("first-name")
                val lastName = dataSignal("last-name")
                val address = dataSignal("address")
                val city = dataSignal("city")
                val telephone = dataSignal("telephone")
                attrClass("form-group has-feedback")
                partialInputField(
                    "First Name",
                    "firstName",
                    "",
                    firstName,
                )
                partialInputField(
                    "Last Name",
                    "lastName",
                    "",
                    lastName,
                )
                partialInputField(
                    "Address",
                    "address",
                    "",
                    address,
                )
                partialInputField(
                    "City",
                    "city",
                    "",
                    city,
                )
                partialInputField(
                    "Telephone",
                    "telephone",
                    "",
                    telephone,
                )
                if (errorMsg.isNotEmpty()) {
                    errorDiv(errorMsg)
                }
            }
            div {
                attrClass("form-group")
                div {
                    attrClass("col-sm-offset-2 col-sm-10")
                    button {
                        attrClass("btn btn-primary")
                        attrType(EnumTypeButtonType.SUBMIT)
                        text("Add Owner")
                    }
                }
            }
        }
    }
}
