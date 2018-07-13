package net.paypredict.patient.id.whitepage

import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.data.renderer.TemplateRenderer
import net.paypredict.patient.id.plusAssign
import java.io.StringWriter
import javax.json.*
import javax.json.stream.JsonGenerator
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/13/2018.
 */

class WhitePagePersonGrid(json: JsonObject) : Div() {
    init {
        width = "100%"
        height = "100%"
        val grid =
            Grid<WhitePagePerson>().apply {
                width = "100%"
                height = "100%"
                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<div>[[item.firstname]]</div>")
                        .withProperty("firstname", WhitePagePerson::firstname)
                ).setHeader("First Name")

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<div>[[item.middlename]]</div>")
                        .withProperty("middlename", WhitePagePerson::middlename)
                ).setHeader("MI")

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<div>[[item.lastname]]</div>")
                        .withProperty("lastname", WhitePagePerson::lastname)
                ).setHeader("Last Name")

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<div>[[item.age_range]]</div>")
                        .withProperty("age_range", WhitePagePerson::age_range)
                ).setHeader("Age")

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<div>[[item.gender]]</div>")
                        .withProperty("gender", WhitePagePerson::gender)
                ).setHeader("Gender")

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<pre>[[item.address]]</pre>")
                        .withProperty("address") {
                            it.address?.run {
                                "\n".str(
                                    street_line_1,
                                    street_line_2,
                                    ", ".str(city, state_code, "-".str(postal_code, zip4))
                                )
                            }

                        }
                ).setHeader("Address").flexGrow = 3

                addColumn(
                    TemplateRenderer
                        .of<WhitePagePerson>("<pre>[[item.phones]]</pre>")
                        .withProperty("phones") {
                            it.phones
                                .map { it.line_type + " " + it.phone_number }
                                .joinToString(separator = "\n")
                        }
                ).setHeader("Phone").flexGrow = 2

            }
        grid.setItems(json.toWhitePagePersonList())
        add(grid)


        json.showWarnings("warnings")
        json.showError("error")
    }

    private fun JsonObject.showWarnings(type: String) {
        val messages = getStringsList(type)
        if (messages.isNotEmpty())
            Notification.show(
                messages.joinToString(),
                3000,
                Notification.Position.TOP_CENTER
            )
    }

    private fun JsonObject.showError(type: String) {
        fun showMessageDialog(messages: List<String>) {
            Dialog().apply {
                messages.forEach { this += H4(it) }
                open()
            }
        }

        val messages = getStringsList(type)
        if (messages.isNotEmpty()) showMessageDialog(messages)
    }

    private fun JsonObject.getStringsList(name: String) =
        (this[name] as? JsonArray)?.filterIsInstance<JsonString>()?.map { it.string } ?: emptyList()

    private fun String.str(vararg strings: String?, nullOnEmpty: Boolean = true): String? {
        val list = listOfNotNull(*strings)
        if (nullOnEmpty && list.isEmpty()) return null
        return list.joinToString(separator = this)
    }
}

private fun JsonObject.toWhitePagePersonList(): List<WhitePagePerson> {
    val array = get("person") as? JsonArray ?: return emptyList()
    val result = mutableListOf<WhitePagePerson>()
    for (person in array) {
        if (person !is JsonObject) continue
        val id = person.str("id") ?: continue
        result += WhitePagePerson(
            id,
            stringProperties(person),
            addressProperties(person["found_at_address"] as? JsonObject),
            phonesProperties(person["phones"] as? JsonArray)
        )
    }
    return result
}

private inline fun <reified P> stringProperties(json: JsonObject): ReadOnlyProperty<P, String?> =
    object : ReadOnlyProperty<P, String?> {
        override fun getValue(thisRef: P, property: KProperty<*>): String? {
            return json.str(property.name)
        }
    }

private fun addressProperties(json: JsonObject?): ReadOnlyProperty<WhitePagePerson, Address?> =
    object : ReadOnlyProperty<WhitePagePerson, Address?> {
        override fun getValue(thisRef: WhitePagePerson, property: KProperty<*>): Address? {
            if (json == null) return null
            val id = json.getString("id", null) ?: return null
            return Address(id, stringProperties(json))
        }
    }

private fun phonesProperties(array: JsonArray?): ReadOnlyProperty<WhitePagePerson, List<Phone>> =
    object : ReadOnlyProperty<WhitePagePerson, List<Phone>> {
        override fun getValue(thisRef: WhitePagePerson, property: KProperty<*>): List<Phone> {
            if (array == null) return emptyList()
            return array
                .filterIsInstance<JsonObject>()
                .map { phone ->
                    val id = phone.getString("id")
                    Phone(id, stringProperties(phone))
                }
        }
    }


private fun JsonObject.str(name: String): String? =
    (get(name) as? JsonString)?.string

private val jsonPP: JsonWriterFactory by lazy {
    Json.createWriterFactory(mapOf<String, Any>(JsonGenerator.PRETTY_PRINTING to true))
}

private fun JsonObject.toStringPP(): String =
    StringWriter().use { jsonPP.createWriter(it).write(this); it }.toString().trimStart()

