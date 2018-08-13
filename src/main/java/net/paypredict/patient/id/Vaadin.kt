package net.paypredict.patient.id

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.data.renderer.TemplateRenderer

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */

operator fun HasComponents.plusAssign(value: Component) {
    add(value)
}

inline fun <reified T> String.template(build: TemplateRenderer<T>.() -> Unit): Renderer<T> =
    TemplateRenderer.of<T>(this).apply { build() }

inline fun <reified T> Grid<T>.column(renderer: Renderer<T>, build: Grid.Column<T>.() -> Unit = {}) {
    addColumn(renderer).build()
}

inline fun <reified T, reified P> Grid<T>.column(
    name: String,
    noinline provider: (T) -> P
) {
    column(name, provider) {}
}

inline fun <reified T, reified P> Grid<T>.column(
    name: String,
    noinline provider: (T) -> P,
    build: Grid.Column<T>.() -> Unit
) {
    column(
        "<div>[[item.$name]]</div>"
            .template {
                withProperty(name, provider)
            }
    ) {
        setHeader(name.capitalize())
        build()
    }
}
