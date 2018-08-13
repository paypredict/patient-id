package net.paypredict.patient.id.case

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import net.paypredict.patient.id.column
import net.paypredict.patient.id.plusAssign
import java.util.*

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */
class PtnCaseGrid : Div() {
    private val grid: Grid<PtnCase>

    init {
        setSizeFull()
        grid = Grid<PtnCase>().apply {
            setSizeFull()
            column("id", PtnCase::id) {
                setHeader("Case ID")
            }
            column("time") { it.time?.toString() }
            column("patient") {
                    when(it.patientSelected) {
                        null -> "Patient not selected"
                        else -> ""
                    }
            }
        }
        this += grid

        grid.setItems(PtnCase("123").apply { time = Date() })
    }
}