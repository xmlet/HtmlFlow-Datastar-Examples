package org.springframework.samples.petclinic.views.vets

import htmlflow.HtmlView
import htmlflow.dyn
import org.springframework.samples.petclinic.vet.Specialty
import org.springframework.samples.petclinic.vet.Vets
import org.springframework.samples.petclinic.views.fragments.layout
import org.springframework.stereotype.Component
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.Table
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import java.util.stream.Collectors

@Component
class VetList {
    val view: HtmlView<Any> = layout { vetList() }

    private fun Div<*>.vetList() {
        h2 {
            text("Veterinarians")
            table {
                attrId("vets")
                attrClass("table table-striped")
                tableHead()
                tableBody()
            }
        }
    }

    private fun Table<*>.tableHead() {
        thead {
            tr {
                th { text("Name") }
                th { text("Specialties") }
            }
        }
    }

    private fun Table<*>.tableBody() {
        dyn { vets: Vets ->
            vets.vetList?.forEach { vet ->
                tr {
                    td {
                        text(vet.firstName + " " + vet.lastName)
                    }
                    td {
                        span {
                            text(format(vet.specialties))
                        }
                    }
                }
            }
        }
    }

    private fun format(specialties: MutableSet<Specialty>): String? {
        if (specialties.isEmpty()) return "none"
        return specialties.stream().map { obj: Specialty? -> obj.toString() }.collect(Collectors.joining(" "))
    }
}
