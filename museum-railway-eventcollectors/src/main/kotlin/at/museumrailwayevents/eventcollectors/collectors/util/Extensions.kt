package at.museumrailwayevents.eventcollectors.collectors.util

import base.boudicca.SemanticKeys

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