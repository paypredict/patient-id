package net.paypredict.patient.id

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.templatemodel.TemplateModel
import net.paypredict.patient.id.PreTemplate.PreModel

/**
 * Simple template example.
 */
@Tag("pre-template")
@HtmlImport("src/pre-template.html")
class PreTemplate : PolymerTemplate<PreModel>() {
    interface PreModel : TemplateModel {
        var value: String
    }

    init {
        model.value = ""
    }

    var value: String
        get() = model.value
        set(value) {
            model.value = value
        }
}
