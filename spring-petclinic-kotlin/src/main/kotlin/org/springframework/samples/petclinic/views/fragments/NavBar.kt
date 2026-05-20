package org.springframework.samples.petclinic.views.fragments

import org.xmlet.htmlapifaster.EnumTypeButtonType
import org.xmlet.htmlapifaster.Nav
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlapifaster.ul

fun Nav<*>.navbarFragment() {
    attrClass("navbar navbar-expand-lg navbar-dark")
    addAttr("role", "navigation")
    div {
        attrClass("container-fluid")
        a {
            attrClass("navbar-brand")
            attrHref("/")
            span {}
        }
        button {
            attrType(EnumTypeButtonType.BUTTON)
            attrClass("navbar-toggler")
            addAttr("data-bs-toggle", "collapse")
            addAttr("data-bs-target", "#main-navbar")
            span {
                attrClass("navbar-toggler-icon")
            }
        }
        div {
            attrClass("collapse navbar-collapse")
            attrId("main-navbar")
            ul {
                attrClass("nav navbar-nav me-auto")
                li {
                    attrClass("nav-item")
                    a {
                        attrClass("nav-link active")
                        attrHref("/")
                        attrTitle("home page")
                        span {
                            attrClass("fa fa-home")
                        }
                        span { text("Home") }
                    }
                }
                li {
                    attrClass("nav-item")
                    a {
                        attrClass("nav-link")
                        attrHref("/owners/find")
                        attrTitle("find owners")
                        span {
                            attrClass("fa fa-search")
                        }
                        span { text("Find owners") }
                    }
                }
                li {
                    attrClass("nav-item")
                    a {
                        attrClass("nav-link")
                        attrHref("/vets.html")
                        attrTitle("veterinarians")
                        span {
                            attrClass("fa fa-th-list")
                        }
                        span { text("Veterinarians") }
                    }
                }
                li {
                    attrClass("nav-item")
                    a {
                        attrClass("nav-link")
                        attrHref("/oups")
                        attrTitle("trigger a RuntimeException to see how it is handled")
                        span {
                            attrClass("fa fa-exclamation-triangle")
                        }
                        span { text("Error") }
                    }
                }
            }
        }
    }
}
