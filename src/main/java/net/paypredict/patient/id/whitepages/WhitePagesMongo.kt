package net.paypredict.patient.id.whitepages

import com.mongodb.client.model.UpdateOptions
import net.paypredict.patient.id.DBS
import org.bson.Document
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.json.JsonArray
import javax.json.JsonObject

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/14/2018.
 */

object WhitePagesPersonCollection {
    private val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(1).also {
            DBS.addShutdownListener {
                executor.shutdown()
                executor.awaitTermination(10, TimeUnit.SECONDS)
            }
        }
    }

    fun storeResponse(query: String, response: JsonObject) {
        val now = Date()
        val array = response["person"] as? JsonArray ?: return
        val documents = mutableListOf<Document>()
        for (jsonValue in array) {
            if (jsonValue !is JsonObject) continue
            val person: Document = Document.parse(jsonValue.toString())
            documents += Document()
                .apply {
                    this["_id"] = person["id"]
                    this["updated"] = now
                    this["query"] = query
                    this["person"] = person
                }
        }
        if (documents.isNotEmpty()) {
            executor.submit {
                val person = DBS.Collections.whitePagesPerson()
                val upsert = UpdateOptions().upsert(true)
                for (doc in documents) {
                    try {
                        person.replaceOne(Document("_id", doc["_id"]), doc, upsert)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        break
                    }
                }
            }
        }
    }

}
