package org.springframework.samples.petclinic.views.pets

import htmlflow.HtmlPage
import htmlflow.dyn
import htmlflow.tr
import org.springframework.samples.petclinic.Routes
import org.springframework.samples.petclinic.owner.Owner
import org.springframework.samples.petclinic.pet.Pet
import org.springframework.samples.petclinic.pet.PetRepository
import org.springframework.samples.petclinic.views.fragments.rowElementInputInline
import org.springframework.samples.petclinic.views.visits.visitsTableBody
import org.springframework.samples.petclinic.views.visits.visitsTableHead
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.Td
import org.xmlet.htmlapifaster.Tr
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.dd
import org.xmlet.htmlapifaster.dl
import org.xmlet.htmlapifaster.dt
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.option
import org.xmlet.htmlapifaster.select
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.Signal
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click

// Used in OwnersDetails
internal fun HtmlPage.editPet(
    pets: PetRepository,
    editing: Signal<Boolean?>,
) {
    tr {
        dyn { pet: Pet ->
            attrId("row-pet-${pet.id}")
            td {
                addAttr("valign", "top")
                petsInputs(pets, "${pet.id}")
            }
        }
        td {
            addAttr("valign", "top")
            table {
                attrClass("visits-table")
                tbody {
                    petEditButtons(editing)
                }
            }
        }
    }
}

internal fun Div<*>.addNewPetButton(editing: Signal<Boolean?>) {
    button {
        dataAttr("disabled") { +editing }
        dyn { owner: Owner ->
            dataOn(Click) {
                editing.setValue(true)
                get(Routes.petNew(owner.id))
            }
            attrClass("btn btn-primary")
        }
        text("Add New Pet")
    }
}

internal fun Tbody<*>.tableBodyPetsAndVisits(editing: Signal<Boolean?>) {
    dyn { owner: Owner ->
        owner.getPets().forEach { pet ->
            tr {
                attrId("row-pet-${pet.id}")
                petRow(pet)
                td {
                    addAttr("valign", "top")
                    table {
                        attrClass("visits-table")
                        visitsTableHead()
                        visitsTableBody(owner, pet, editing)
                    }
                }
            }
        }
    }
}

internal fun Tr<*>.petRow(pet: Pet) {
    td {
        addAttr("valign", "top")
        dl {
            attrClass("dl-horizontal")
            dt { text("Name") }
            dd { text(pet.name) }
            dt { text("Birth Date") }
            dd { text(pet.birthDate) }
            dt { text("Type") }
            dd { text(pet.type) }
        }
    }
}

internal fun Td<*>.petsInputs(
    pets: PetRepository,
    signalSuffix: String,
) {
    dl {
        attrClass("dl-horizontal")
        dt { text("Name") }
        dd { rowElementInputInline("name$signalSuffix") }
        dt { text("Birth Date") }
        dd { rowElementInputInline("birth-date$signalSuffix", EnumTypeInputType.DATE) }
        dt { text("Type") }
        dd {
            select {
                attrName("type")
                dataBind("type$signalSuffix")
                pets.findPetTypes().forEach { item ->
                    option {
                        attrValue(item.name ?: "")
                        text(item.name ?: "")
                    }
                }
            }
        }
    }
}

internal fun Tbody<*>.petAddButtons(editing: Signal<Boolean?>) {
    tr {
        td {
            dyn { owner: Owner ->
                button {
                    attrId("save-pet")
                    attrClass("btn btn-primary")
                    dataOn(Click) {
                        editing.setValue(false)
                        post(Routes.petNew(owner.id))
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    i { attrClass("pixelarticons:check") }
                    text("save")
                }
            }
            dyn { owner: Owner ->
                button {
                    attrId("cancel-pet")
                    attrClass("btn btn-primary")
                    dataOn(Click) {
                        editing.setValue(false)
                        get(Routes.petNewCancel(owner.id))
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    i { attrClass("pixelarticons:check") }
                    text("cancel")
                }
            }
        }
    }
}

internal fun Tbody<*>.petEditButtons(editing: Signal<Boolean?>) {
    tr {
        td {
            dyn { pet: Pet ->
                button {
                    attrId("save-edit-pet")
                    attrClass("btn btn-primary")
                    dataOn(Click) {
                        editing.setValue(false)
                        patch(Routes.petEdit(pet.owner!!.id, pet.id))
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    i { attrClass("pixelarticons:check") }
                    text("save")
                }
            }
            dyn { pet: Pet ->
                button {
                    attrId("cancel-pet")
                    attrClass("btn btn-primary")
                    dataOn(Click) {
                        editing.setValue(false)
                        get(Routes.petEditCancel(pet.owner!!.id))
                    }
                    val fetching = dataIndicator("_fetching")
                    dataAttr("disabled") { +fetching }
                    i { attrClass("pixelarticons:check") }
                    text("cancel")
                }
            }
        }
    }
}
