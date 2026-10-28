package pt.isel.views.htmlflow

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import io.ktor.server.routing.RoutingContext
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import pt.isel.ktor.switchUser
import pt.isel.ktor.updateUserAge

data class Person(
    val name: String,
    val age: Int,
)

val signalComplexDomain =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("/js/datastar.js")
                        }
                        link {
                            attrRel(EnumRelType.STYLESHEET)
                            attrHref("/css/styles.css")
                        }
                    }
                    body {
                        div {
                            attrId("person-info")
                            val (person) = dataSignals("person" to Person("John Doe", 18))
                            p { dataText { +person.on(Person::name) } }
                            p { dataText { +person.on(Person::age) } }
                            div {
                                input {
                                    attrType(EnumTypeInputType.TEXT)
                                    dataBind("updatedUserAge")
                                }
                                button {
                                    dataOn(Click) { put(RoutingContext::updateUserAge) }
                                    text("Update user age")
                                }
                            }
                            button {
                                dataOn(Click) { put(RoutingContext::switchUser) }
                            }
                        }
                    }
                }
            }
        }.toString()
