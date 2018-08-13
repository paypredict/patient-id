package net.paypredict.patient.id

import java.io.File
import javax.json.Json
import javax.json.JsonObject

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/16/2018.
 */
object PatientId {
    val dir = File("/PayPredict/ptn")
    private val confDir = dir.resolve("conf")
    private val confFile = confDir.resolve("ptn.json")

    val conf: JsonObject by lazy {
        if (confFile.isFile)
            Json.createReader(confFile.reader()).use { it.readObject() } else
            Json.createObjectBuilder().build()
    }
}

const val appTitle = "Fast and Accurate Patient Data Entry"

const val appDescription = """
Our real-time identity validation technology allows entering patient information quickly
and accurately. Save data entry time, avoid claim denials and mailing problems.
Start entry with typing name and zip code to get results.
If too many or too few results returned, enter additional details
until the exact patient match found.
"""
