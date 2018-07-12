package net.paypredict.patient.id

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.templatemodel.TemplateModel
import net.paypredict.patient.id.ExampleTemplate.ExampleModel

/**
 * Simple template example.
 */
@Tag("example-template")
@HtmlImport("src/example-template.html")
class ExampleTemplate : PolymerTemplate<ExampleModel>() {

    /**
     * Template model which defines the single "value" property.
     */
    interface ExampleModel : TemplateModel {
        var value: String
    }

    init {
        // Set the initial value to the "value" property.
        model.value = "Not clicked"
    }

    /*
     * Allow setting the value property from outside of the class.
     */
    var value: String
        get() = model.value
        set(value) {
            model.value = value
        }

}
