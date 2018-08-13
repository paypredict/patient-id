package net.paypredict.patient.id.case

import com.mongodb.DBRef
import java.util.*

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/17/2018.
 */
class PtnCase(val id: String) {
    var time: Date? = null
    var patientSelected: DBRef? = null
    var patientFound: List<DBRef> = emptyList()
}