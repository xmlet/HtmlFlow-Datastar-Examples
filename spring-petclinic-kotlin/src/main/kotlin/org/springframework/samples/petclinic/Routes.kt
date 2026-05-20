package org.springframework.samples.petclinic

object Routes {
    const val OWNERS_NEW = "/owners/new"
    const val OWNERS_FIND_RESULT = "/owners/find/result"
    const val OWNERS = "/owners"
    const val OWNERS_ID = "/owners/{ownerId}"
    const val OWNERS_EDIT = "/owners/{ownerId}/edit"
    const val OWNERS_EDIT_CANCEL = "/owners/{ownerId}/edit/cancel"
    const val PET_NEW = "/owners/{ownerId}/pets/new"
    const val PET_NEW_CANCEL = "/owners/{ownerId}/pets/new/cancel"
    const val PET_EDIT = "/owners/{ownerId}/pets/{petId}/edit"
    const val PET_EDIT_CANCEL = "/owners/{ownerId}/pets/{petId}/edit/cancel"
    const val VISIT_NEW = "/owners/{ownerId}/pets/{petId}/visits/new"

    fun ownerId(ownerId: Any?) = OWNERS_ID.replace("{ownerId}", ownerId.toString())

    fun ownerEdit(ownerId: Any?) = OWNERS_EDIT.replace("{ownerId}", ownerId.toString())

    fun ownerEditCancel(ownerId: Any?) = OWNERS_EDIT_CANCEL.replace("{ownerId}", ownerId.toString())

    fun petNew(ownerId: Any?) = PET_NEW.replace("{ownerId}", ownerId.toString())

    fun petNewCancel(ownerId: Any?) = PET_NEW_CANCEL.replace("{ownerId}", ownerId.toString())

    fun petEditCancel(
        ownerId: Any?,
        petId: Any?,
    ) = PET_EDIT_CANCEL
        .replace("{ownerId}", ownerId.toString())
        .replace("{petId}", petId.toString())

    fun petEdit(
        ownerId: Any?,
        petId: Any?,
    ) = PET_EDIT
        .replace("{ownerId}", ownerId.toString())
        .replace("{petId}", petId.toString())

    fun visitNew(
        ownerId: Any?,
        petId: Any?,
    ) = VISIT_NEW
        .replace("{ownerId}", ownerId.toString())
        .replace("{petId}", petId.toString())
}
