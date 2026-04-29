package org.springframework.samples.petclinic.views

import htmlflow.HtmlView
import htmlflow.dyn
import org.springframework.samples.petclinic.owner.Pet
import org.springframework.samples.petclinic.owner.PetRepository
import org.springframework.samples.petclinic.owner.PetType
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.samples.petclinic.views.fragments.partialInputField
import org.springframework.samples.petclinic.views.fragments.partialSelectField
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.form
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click

@Component
class CreateOrUpdatePetForm(
    private val pets: PetRepository,
) {
    private val petTypes: List<PetType> = pets.findPetTypes()
    val view: HtmlView<Any> = layout { petForm() }

    fun Div<*>.petForm() {
        h2 {
            dyn { pet: Pet -> if (pet.id == null) text("New ") }
            text("Pet")
        }
        div {
            attrClass("form-horizontal")
            input {
                attrType(EnumTypeInputType.HIDDEN)
                attrName("id")
                attrValue("")
            }
            div {
                attrClass("form-group has-feedback")
                div {
                    attrClass("form-group")
                    label {
                        attrClass("col-sm-2 control-label")
                        text("Owner")
                    }
                    div {
                        attrClass("col-sm-10")
                        span {
                            dyn { pet: Pet ->
                                text(pet.owner?.let { "${it.firstName} ${it.lastName}" } ?: "")
                            }
                        }
                    }
                    dyn { pet: Pet ->
                        form {
                            val name = dataSignal("_name")
                            val birthDate = dataSignal("_birthdate")
                            val type = dataSignal("_type")

                            attrClass("form-group")
                            partialInputField("Name", "name", pet.name ?: "", name)
                            partialInputField(
                                "Birth Date",
                                "birthDate",
                                pet.birthDate ?: "",
                                birthDate,
                                EnumTypeInputType.DATE,
                            )
                            partialSelectField("type", petTypes, pet.type?.name ?: "", type)
                            div {
                                attrClass("col-sm-offset-2 col-sm-10")
                                button {
                                    attrClass("btn btn-primary")
                                    val ownerId = pet.owner?.id
                                    val petId = pet.id
                                    dataAttr("disabled") { !name or !birthDate or !type }
                                    if (pet.name == null || petId == null) {
                                        dataOn(Click) { post("/owners/$ownerId/pets/new", "{contentType: 'form'}") }
                                        text("Add Pet")
                                    } else {
                                        dataOn(Click) { post("/owners/$ownerId/pets/$petId/edit", "{contentType: 'form'}") }
                                        text("Update Pet")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
