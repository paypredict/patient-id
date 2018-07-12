package net.paypredict.patient.id

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route

/**
 * The main view contains a button and a template element.
 */
@HtmlImport("styles/shared-styles.html")
@Route("example")
class ExampleView : VerticalLayout() {
    init {
        val template = ExampleTemplate()

        val button = Button("Click me") {
            template.value = "Clicked!"
        }

        add(button, template)
        className = "main-layout"
    }
}
