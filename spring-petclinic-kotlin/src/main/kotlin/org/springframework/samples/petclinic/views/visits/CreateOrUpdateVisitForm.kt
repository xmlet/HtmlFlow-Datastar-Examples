package org.springframework.samples.petclinic.views.visits

import htmlflow.HtmlView
import htmlflow.dyn
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.samples.petclinic.views.fragments.partialInputField
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import java.time.LocalDate

@Component
class CreateOrUpdateVisitForm {
    val view: HtmlView<Any> = layout { createOrUpdateForm() }

    private fun Div<*>.createOrUpdateForm() {
        h2 { text("New Visit") }
        b { text("Pet") }
        table {
            attrClass("table table-striped")
            tableHead()
            tableBody()
        }
        form {
            attrClass("form-horizontal")
            val dateSignal = dataSignal("date")
            val descriptionSignal = dataSignal("description")
            attrMethod(EnumMethodType.POST)
            dyn { pet: Pet ->
                attrAction(Routes.visitNew(pet.owner!!.id, pet.id))
            }
            div {
                attrClass("form-group has-feedback")
                partialInputField("Date", "date", LocalDate.now(), dateSignal, EnumTypeInputType.DATE)
                partialInputField("Description", "description", "", descriptionSignal)
            }
            div {
                attrClass("form-group")
                div {
                    attrClass("col-sm-offset-2 col-sm-10")
                    input {
                        attrType(EnumTypeInputType.HIDDEN)
                        attrName("petId")
                        dyn { pet: Pet ->
                            attrValue(if (pet.id != null) pet.id.toString() else "")
                        }
                    }
                    button {
                        attrClass("btn btn-primary")
                        attrType(EnumTypeButtonType.SUBMIT)
                        text("Add Visit")
                    }
                }
            }
        }
        br {}
        b { text("Previous Visits") }
        table {
            attrClass("table table-striped")
            tbody {
                tr {
                    th { text("Date") }
                    th { text("Description") }
                    dyn { pet: Pet ->
                        pet.getVisits().forEach { v ->
                            td { text(v.date) }
                            td { text(v.description) }
                        }
                    }
                }
            }
        }
    }

    private fun Table<*>.tableHead() {
        thead {
            tr {
                th { text("Name") }
                th { text("Birth Date") }
                th { text("Type") }
                th { text("Owner") }
            }
        }
    }

    private fun Table<*>.tableBody() {
        tbody {
            tr {
                td {
                    dyn { pet: Pet ->
                        text(if (pet.name != null) pet.name else "")
                    }
                }
                td {
                    dyn { pet: Pet ->
                        text(if (pet.birthDate != null) pet.birthDate else "")
                    }
                }
                td {
                    dyn { pet: Pet ->
                        text(if (pet.type != null) pet.type else "")
                    }
                }
                td {
                    dyn { pet: Pet ->
                        text(if (pet.owner != null) pet.owner!!.firstName + " " + pet.owner!!.lastName else "")
                    }
                }
            }
        }
    }
}