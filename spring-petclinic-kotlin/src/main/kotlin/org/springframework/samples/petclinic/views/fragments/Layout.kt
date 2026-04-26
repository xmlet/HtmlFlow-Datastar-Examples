package org.springframework.samples.petclinic.views.fragments

import htmlflow.HtmlView
import htmlflow.html
import htmlflow.view
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.br
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.img
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.meta
import org.xmlet.htmlapifaster.nav
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.title

fun layout(content: Div<*>.() -> Unit): HtmlView<Unit> =
    view<Unit> {
        html {
            head {
                meta {
                    addAttr("http-equiv", "Content-Type")
                    attrContent("text/html; charset=UTF-8")
                }
                meta {
                    attrCharset("utf-8")
                }
                meta {
                    addAttr("http-equiv", "X-UA-Compatible")
                    attrContent("IE=edge")
                }
                meta {
                    attrName("viewport")
                    attrContent("width=device-width, initial-scale=1")
                }
                link {
                    addAttr("rel", "shortcut icon")
                    addAttr("type", "image/x-icon")
                    attrHref("/resources/images/favicon.png")
                }
                title {
                    text("PetClinic :: a Spring Framework demonstration")
                }
                comment(
                    "[if lt IE 9]>\r\n" +
                        "    <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>\r\n" +
                        "    <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>\r\n" +
                        "    <![endif]",
                )
                link {
                    attrRel(EnumRelType.STYLESHEET)
                    attrHref("/webjars/font-awesome/css/font-awesome.min.css")
                }
                link {
                    attrRel(EnumRelType.STYLESHEET)
                    attrHref("/resources/css/petclinic.css")
                }
            }
            body {
                nav {
                    navbarFragment()
                }
                div {
                    attrClass("container-fluid")
                    div {
                        attrClass("container xd-container")
                        content()
                        br {}
                        br {}
                        div {
                            attrClass("container")
                            div {
                                attrClass("row")
                                div {
                                    attrClass("col-12 text-center")
                                    img {
                                        attrSrc("/resources/images/spring-pivotal-logo.png")
                                        attrAlt("Sponsored by Pivotal")
                                    }
                                }
                            }
                        }
                    }
                }
                script {
                    attrSrc("/webjars/bootstrap/dist/js/bootstrap.bundle.min.js")
                }
                script {
                    attrType(EnumTypeScriptType.MODULE)
                    attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                }
            }
        }
    }
