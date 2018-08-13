package net.paypredict.patient.id.case

import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import net.paypredict.patient.id.AppHeader
import net.paypredict.patient.id.appTitle
import net.paypredict.patient.id.plusAssign

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */
@HtmlImport("styles/shared-styles.html")
@Route("")
@PageTitle(appTitle)
class PtnCaseView : Div() {
    init {
        className = "root-layout"
        setSizeFull()

//        this += AppHeader()
        this += PtnCaseGrid()
    }
}
