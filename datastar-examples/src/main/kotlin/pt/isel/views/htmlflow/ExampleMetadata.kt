package pt.isel.views.htmlflow

import org.xmlet.htmlapifaster.EnumRelType
import org.xmlet.htmlapifaster.Head
import org.xmlet.htmlapifaster.link
import org.xmlet.htmlapifaster.meta
import org.xmlet.htmlapifaster.title

fun Head<*>.exampleMetadata(name: String) {
    meta { attrCharset("UTF-8") }
    title { text("$name — HtmlFlow") }
    link {
        attrRel(EnumRelType.ICON)
        attrHref("/images/htmlflow-logo.png")
    }
}
