package net.paypredict.patient.id

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */
class AppHeader: HorizontalLayout() {
    init {
        isMargin = true
        this += H4(Text(appTitle))
    }
}