package net.paypredict.patient.id

import java.io.StringWriter
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonString
import javax.json.JsonWriterFactory
import javax.json.stream.JsonGenerator

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */
fun JsonObject.str(name: String): String? =
    (get(name) as? JsonString)?.string

fun JsonObject.toStringPP(): String =
    StringWriter().use { jsonPP.createWriter(it).write(this); it }.toString().trimStart()

private val jsonPP: JsonWriterFactory by lazy {
    Json.createWriterFactory(mapOf<String, Any>(JsonGenerator.PRETTY_PRINTING to true))
}
