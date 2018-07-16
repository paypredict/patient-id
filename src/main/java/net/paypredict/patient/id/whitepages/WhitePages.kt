package net.paypredict.patient.id.whitepages

import java.io.File
import javax.json.Json
import javax.json.JsonObject

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/14/2018.
 */
object WhitePages {
    val dir = File("/PayPredict/whitepages")
    private val confDir = dir.resolve("conf")
    private val confFile = confDir.resolve("whitepages.json")

    val conf: JsonObject by lazy {
        if (confFile.isFile)
            Json.createReader(confFile.reader()).use { it.readObject() } else
            Json.createObjectBuilder().build()
    }


}