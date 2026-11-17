package pt.isel.views.fragments

import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.a
import org.xmlet.htmlapifaster.p

private const val SOURCE_ROOT =
    "https://github.com/xmlet/HtmlFlow-Datastar-Examples/blob/main/datastar-examples/src/main"

fun Div<*>.sourceLinks(
    example: String,
    name: String,
    view: String = name,
) {
    p {
        attrClass("demo-source-links")
        text("Source code: ")
        val sources =
            listOf(
                "HTML view" to "resources/public/html/$example.html",
                "HtmlFlow view" to "kotlin/pt/isel/views/htmlflow/$view.kt",
                "Ktor server" to "kotlin/pt/isel/ktor/Demo$name.kt",
            )
        sources.forEachIndexed { index, (label, path) ->
            if (index > 0) text(" · ")
            a {
                attrHref("$SOURCE_ROOT/$path")
                addAttr("target", "_blank")
                addAttr("rel", "noopener noreferrer")
                text(label)
            }
        }
    }
}
