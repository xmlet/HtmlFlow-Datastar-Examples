package org.springframework.samples.petclinic.views.visits

import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.pet.Pet
import org.xmlet.htmlapifaster.Table
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.Signal

// Used in OwnersDetails
internal fun Table<*>.visitsTableHead() {
    thead {
        tr {
            th { text("Visit Date") }
            th { text("Description") }
        }
    }
}

internal fun Table<*>.visitsTableBody(
    owner: Owner,
    pet: Pet,
    editing: Signal<Boolean?>,
) {
    tbody.of { innerbody ->
        pet.getVisits().forEach { vs ->
            innerbody.tr {
                td { text(vs.date) }
                td { text(vs.description) }
            }
        }
        tr {
            td {
                button {
                    attrId("pet-edit")
                    attrClass("btn btn-primary")
                    dataOn(Click) {
                        editing.setValue(true)
                        get(Routes.petEdit(owner.id, pet.id))
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { fetching or editing }
                    i { attrClass("pixelarticons:close") }
                    text("Edit Pet")
                }
            }
            td {
                a {
                    attrClass("btn btn-primary")
                    attrHref(Routes.visitNew(owner.id, pet.id))
                    text("Add Visit")
                }
            }
        }
    }
}
