package net.paypredict.patient.id.whitepages

import net.paypredict.patient.id.PatientId
import javax.json.Json
import javax.json.JsonObject

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/14/2018.
 */
object WhitePages {
    val conf: JsonObject by lazy {
        PatientId.conf["whitepages"] as? JsonObject ?: Json.createObjectBuilder().build()
    }
}