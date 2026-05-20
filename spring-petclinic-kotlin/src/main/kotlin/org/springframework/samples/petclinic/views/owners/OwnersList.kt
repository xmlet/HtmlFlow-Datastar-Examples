package org.springframework.samples.petclinic.views.owners

import htmlflow.HtmlView
import htmlflow.dyn
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.Table
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr

@Component
class OwnersList {
    val view: HtmlView<Any> = layout { listOwners() }

    private fun Div<*>.listOwners() {
        h2 { text("Owners") }

        table {
            attrId("owners")
            attrClass("table table-striped")
            tableHead()
            tbody {
                tableBody()
            }
        }
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
