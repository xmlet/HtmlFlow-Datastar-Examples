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
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.Table
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.input
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
    val view: HtmlView<Any> = layout { listOwners() }

    val activeSearchOwnerRowsFragment: HtmlView<Collection<Owner>> =
        view {
            tbody {
                tableBody()
            }
        }

    private fun Div<*>.newOwnerButton() {
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

    private fun Div<*>.listOwners() {
        h2 { text("Owners") }

        activeSearchOwner()

        table {
            attrId("owners")
            attrClass("table table-striped")
            tableHead()
            tbody {
                tableBody()
            }
        }
        newOwnerButton()
    }

    private fun Table<*>.tableHead() {
        thead {
            tr {
                th {
                    attrStyle("width: 150px;")
                    text("Name")
                }
                th {
                    attrStyle("width: 200px;")
                    text("Address")
                }
                th { text("City") }
                th {
                    attrStyle("width: 120px;")
                    text("Telephone")
                }
                th { text("Pets") }
            }
        }
    }

    private fun Tbody<*>.tableBody() {
        attrId("results-table")
        dyn { owners: List<Owner> ->
            owners.forEach { owner ->
                tr {
                    td {
                        a {
                            attrHref(Routes.ownerId(owner.id))
                            text(owner.firstName + " " + owner.lastName)
                        }
                    }
                    td { text(owner.address) }
                    td { text(owner.city) }
                    td { text(owner.telephone) }
                    td {
                        span().of { span ->
                            owner.getPets().forEach { pet ->
                                span.text(pet.name + " ")
                            }
                        }
                    }
                }
            }
        }
    }
}
