package at.museumrailwayevents.eventcollectors.collectors.util

import base.boudicca.SemanticKeys
import org.jsoup.nodes.Document

fun String.asTagsList(): MutableList<String> {
    return this.split(",").toMutableList()
}

fun List<String>.toTagsValue(): String {
    return this.joinToString(",").lowercase()
}

fun MutableMap<String, String>.addTags(tagsToAdd: List<String>) {
    val tags = if (this.containsKey(SemanticKeys.TAGS)) {
        this[SemanticKeys.TAGS]?.asTagsList()!!
    } else {
        mutableListOf()
    }

    tags.addAll(tagsToAdd)
    this[SemanticKeys.TAGS] = tags.toTagsValue()
}

fun Document.keepLineBreaks(before: List<String> = listOf("p", "br"), after: List<String> = emptyList()): Document {
    val outputSettings: Document.OutputSettings = Document.OutputSettings()
    outputSettings.prettyPrint(false)
    this.outputSettings(outputSettings)
    val document = this
    before.forEach{ htmlTag ->
        document.select(htmlTag).before("\n")
    }
    after.forEach{ htmlTag ->
        document.select(htmlTag).after("\n")
    }
    return document
}