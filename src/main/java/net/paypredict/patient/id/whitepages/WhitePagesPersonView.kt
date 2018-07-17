package net.paypredict.patient.id.whitepages

import com.mongodb.DBRef
import com.mongodb.ErrorCategory
import com.mongodb.MongoWriteException
import com.vaadin.flow.component.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Hr
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import net.paypredict.patient.id.DBS
import net.paypredict.patient.id.PatientId
import org.bson.Document
import java.net.URL
import java.net.URLEncoder
import javax.json.Json
import javax.json.JsonObject

/**
 * The main view contains a button and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle(appTitle)
class WhitePagesPersonView : VerticalLayout() {

    private val whitePagesDebug: JsonObject?
        get() =
            PatientId.dir
                .resolve("whitepages-debug.json")
                .run {
                    when {
                        isFile -> Json.createReader(reader()).use { it.readObject() }
                        else -> null
                    }
                }
    private val whitePagesPersonCollectionName: String by lazy {
        DBS.Collections.whitePagesPerson().namespace.collectionName
    }

    private var personGrid: WhitePagePersonGrid? = null

    init {
        className = "root-layout"
        setSizeFull()

        val name = TextField("NAME")
        val postalCode = TextField("ZIP CODE")
        val streetLine1 = TextField("STREET LINE 1")
        val streetLine2 = TextField("STREET LINE 2")
        val city = TextField("CITY")
        val state = TextField("STATE")
        val country = TextField("COUNTRY").apply {
            value = "US"
            isVisible = false
        }

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
            this += Div().apply {
                style["font-size"] = "large"
                style["padding"] = "20pt"
                this += H2(appTitle)
                this += Text(appDescription).apply {
                    style["max-width"] = "42em"
                }
            }
        }

        whitePagesDebug?.let {
            results.removeAll()
            results += WhitePagePersonGrid(it).also { personGrid = it }
            WhitePagesPersonCollection.storeResponse("debug", it)
        }

        val parameters = Div().apply {
            style["overflow"] = "auto"
            parameterMap.values.forEach { textField ->
                when (textField) {
                    name -> this += section("Main Fields")
                    streetLine1 -> this += section("Additional")
                }
                this += paramsDiv {
                    this += textField.apply {
                        width = "100%"
                        when (this) {
                            name -> {
                                isRequired = true
                                isAutofocus = true
                            }
                            postalCode -> isRequired = true
                        }
                    }
                }
            }
            this += section()

            this += VerticalLayout().apply {
                width = "100%"
                val runQuery = Button("LOOKUP PATIENT") {
                    val person_api = WhitePages.conf.getString("person_api")
                    val api_key = WhitePages.conf.getString("api_key")
                    val query = "search.metro=true&" + parameterMap
                        .entries
                        .joinToString(separator = "&") {
                            it.key + "=" + URLEncoder.encode(it.value.value, "UTF-8")
                        }

                    val url = URL("$person_api?api_key=$api_key&$query")
                    val response = url.openConnection().getInputStream().let {
                        Json.createReader(it).readObject()
                    }

                    results.removeAll()
                    results += WhitePagePersonGrid(response).also { personGrid = it }
                    WhitePagesPersonCollection.storeResponse(query, response)
                }
                this += runQuery
                this.setHorizontalComponentAlignment(Alignment.END, runQuery)

                parameterMap.values.forEach {
                    it.addKeyPressListener(Key.ENTER, ComponentEventListener {
                        runQuery.click()
                    })
                }
            }

            this += section()
            val referenceTextField = TextField("REFERENCE").apply {
                width = "100%"
                isRequired = true
            }
            this += paramsDiv { this += referenceTextField }

            this += VerticalLayout().apply {
                width = "100%"
                val addToRef = Button("ADD TO REFERENCE") {
                    referenceTextField.isInvalid = false
                    referenceTextField.errorMessage = null
                    fun showReferenceError(errorMessage: String) {
                        referenceTextField.isInvalid = true
                        referenceTextField.errorMessage = errorMessage
                    }
                    val grid = personGrid
                    val selectedPersonId = grid?.selectedPersonId
                    if (grid == null || selectedPersonId == null) {
                        showReferenceError("Patient isn't selected")
                        return@Button
                    }
                    val personIdList = grid.personIdList

                    val case = DBS.Collections.case()
                    fun String.toDBRef(): DBRef = DBRef(whitePagesPersonCollectionName, this)
                    val document = Document("_id", referenceTextField.value).apply {
                        this["personFound"] = personIdList.map { it.toDBRef() }
                        this["personSelected"] = selectedPersonId.toDBRef()
                    }
                    try {
                        case.insertOne(document)
                        Notification.show("Reference saved", 1000, Notification.Position.BOTTOM_START)
                        grid.select(null)
                    } catch (e: Exception) {
                        if (e is MongoWriteException && e.error.category == ErrorCategory.DUPLICATE_KEY) {
                            showReferenceError("Reference ${referenceTextField.value} already exists")
                        } else {
                            showError(e)
                            e.printStackTrace()
                        }
                    }
                }
                this += addToRef
                this.setHorizontalComponentAlignment(Alignment.END, addToRef)
                referenceTextField.addKeyPressListener(Key.ENTER, ComponentEventListener {
                    addToRef.click()
                })
            }

        }
        val splitLayout = SplitLayout(parameters, results).apply {
            setSizeFull()
            setSplitterPosition(30.0)
        }
        this += splitLayout
    }

    private fun showError(e: Throwable) {
        Dialog().apply {
            this += H2(Text(e.message))
            open()
        }
    }

    private fun section(caption: String = "") =
        Div().apply {
            this += H3(caption).apply {
                style["padding-left"] = "9pt"
            }
            this += Hr()
        }

    private fun paramsDiv(init: Div.() -> Unit): Div =
        Div().apply {
            style["padding-left"] = "16pt"
            style["padding-right"] = "8pt"
            init()
        }
}

operator fun HasComponents.plusAssign(value: Component) {
    add(value)
}

private const val appTitle = "Fast and Accurate Patient Data Entry"

private const val appDescription = """
Our real-time identity validation technology allows entering patient information quickly
and accurately. Save data entry time, avoid claim denials and mailing problems.
Start entry with typing name and zip code to get results.
If too many or too few results returned, enter additional details
until the exact patient match found.
"""
