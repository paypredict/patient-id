package net.paypredict.patient.id

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.net.URLEncoder
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonWriterFactory
import javax.json.stream.JsonGenerator

/**
 * The main view contains a button and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle(appTitle)
class RootView : VerticalLayout() {

    init {
        className = "root-layout"
        setSizeFull()

        val name = TextField("NAME")
        val postalCode = TextField("ZIP CODE")
        val streetLine1 = TextField("STREET LINE 1")
        val streetLine2 = TextField("STREET LINE 2")
        val city = TextField("CITY")
        val state = TextField("STATE")
        val country = TextField("COUNTRY")

        val parameterMap = mapOf(
            "name" to name,
            "address.postal_code" to postalCode,
            "address.street_line_1" to streetLine1,
            "address.street_line_2" to streetLine2,
            "address.city" to city,
            "address.state_code" to state,
            "address.country_code" to country
        )

        val results = Div().apply {
            style["overflow"] = "auto"
            this+= Div().apply {
                style["padding"] = "20pt"
                this += H2(appTitle)
                this += Text(appDescription).apply {
                    width = "42em"
                }
            }
        }

        val parameters = Div().apply {
            style["overflow"] = "auto"
            parameterMap.values.forEach { textField ->
                when (textField) {
                    name -> this += section("Main")
                    streetLine1 -> this += section("Additional")
                }
                this += Div().apply {
                    style["padding-left"] = "16pt"
                    style["padding-right"] = "8pt"
                    this += textField.apply { width = "100%" }
                }
            }
            this += section()

            this += VerticalLayout().apply {
                width = "100%"
                val runQuery = Button("RUN QUERY") {

                    val person_api = whitePagesConf.getString("person_api")
                    val api_key = whitePagesConf.getString("api_key")
                    val query = parameterMap
                        .entries
                        .joinToString(separator = "&") {
                            it.key + "=" + URLEncoder.encode(it.value.value, "UTF-8")
                        }

                    val url = URL("$person_api?api_key=$api_key&$query")
                    val response = url.openConnection().getInputStream().let {
                        Json.createReader(it).readObject()
                    }

                    results.removeAll()
                    results += TextArea().apply {
                        setSizeFull()
                        value = response.toStringPP()
                        isReadOnly = true
                    }
                }
                this += runQuery
                this.setHorizontalComponentAlignment(Alignment.END, runQuery)
            }
        }
        val splitLayout = SplitLayout(parameters, results).apply {
            setSizeFull()
            setSplitterPosition(20.0)
        }
        this += splitLayout
        splitLayout.secondaryComponent
    }

    private val confDir = File("/PayPredict/conf")
    private val whitePagesConf: JsonObject by lazy {
        confDir
            .resolve("whitepages.json")
            .let { Json.createReader(it.reader()).use { it.readObject() } }
    }
    private val jsonPP: JsonWriterFactory by lazy {
        Json.createWriterFactory(mapOf<String, Any>(JsonGenerator.PRETTY_PRINTING to true))
    }

    private fun JsonObject.toStringPP(): String =
        StringWriter().use { jsonPP.createWriter(it).write(this); it }.toString().trimStart()

    private fun section(caption: String = "") =
        Div().apply {
            this += H3(caption).apply {
                style["padding-left"] = "9pt"
            }
            this += Hr()
        }
}

private operator fun HasComponents.plusAssign(value: Component) {
    add(value)
}

private const val appTitle = "Fast and Accurate Patient Entry"

private const val appDescription = """
Our real-time identity validation technology allows entering patient information quickly
and accurately. Save data entry time, avoid claim denials and mailing problems.
Start entry with typing name and zip code to get results.
If too many or too few results returned, enter additional details
until the exact patient match found.
"""