package org.springframework.samples.petclinic.views.owners

import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.tbody
import htmlflow.view
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumMethodType
import org.xmlet.htmlapifaster.EnumTypeButtonType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.Form
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.form
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Input
import kotlin.collections.forEach
import kotlin.time.Duration.Companion.milliseconds

@Component
class OwnersFind {
    val view: HtmlView<Any> = layout { findOwners() }

    private fun Div<*>.findOwners() {
        h2 { text("Find Owners") }

        form {
            attrClass("form-horizontal")
            attrId("search-owner-form")
            attrAction(Routes.OWNERS)
            attrMethod(EnumMethodType.GET)

            div {
                attrClass("form-group")
                div {
                    attrClass("control-group")
                    attrId("lastNameGroup")
                    label {
                        attrClass("col-sm-2 control-label")
                        text("Last name ")
                    }
                    div {
                        attrClass("col-sm-10")
                        activeSearchOwner()
                        resultsTable()
                    }
                }
            }

            findOwnerButton()
            newOwnerButton()
        }
    }

    private fun Tbody<*>.ownerRows() {
        dyn { owners: List<Owner> ->
            owners.forEach { owner ->
                tr {
                    attrOnclick("window.location='${Routes.ownerId(owner.id)}'")
                    attrStyle("cursor: pointer;")
                    attrOnmouseover("this.style.backgroundColor='#f5f5f5'")
                    attrOnmouseout("this.style.backgroundColor='' ")
                    td {
                        text(owner.firstName)
                    }
                    td { text(owner.lastName) }
                    td { text(owner.pets) }
                }
            }
        }
    }

    val activeSearchOwnerRowsFragment: HtmlView<Collection<Owner>> =
        view {
            tbody {
                attrId("owners-table")
                ownerRows()
            }
        }

    private fun Form<*>.findOwnerButton() {
        div {
            attrClass("col-sm-offset-2 col-sm-10")
            button {
                attrClass("btn btn-primary")
                attrType(EnumTypeButtonType.SUBMIT)
                text("Find Owner")
            }
        }
    }

    private fun Div<*>.resultsTable() {
        table {
            thead {
                tr {
                    th {
                        attrStyle("padding-right: 16px;")
                        text("First Name")
                    }
                    th {
                        attrStyle("padding-right: 16px;")
                        text("Last Name")
                    }
                    th { text("Pets") }
                }
            }
            tbody {
                attrId("owners-table")
                ownerRows()
            }
        }
    }

    private fun Form<*>.newOwnerButton() {
        div {
            a {
                attrClass("btn btn-primary")
                attrHref(Routes.OWNERS_NEW)
                text("Add Owner")
            }
        }
    }

    private fun Div<*>.activeSearchOwner() {
        input {
            attrClass("fom")
            attrType(EnumTypeInputType.TEXT)
            attrName("lastName")
            attrPlaceholder("Find Owners")
            dataBind("last-name")
            dataOn(Input) {
                get(Routes.OWNERS_FIND_RESULT)
                modifiers { debounce(200.milliseconds) }
            }
        }
    }
}
