package net.paypredict.patient.id.whitepages

import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/14/2018.
 */

object WhitePagesPersonCollection {
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)

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
                    this["time"] = now
                    this["query"] = query
                    this["person"] = person
                }
        }
        if (documents.isNotEmpty()) {
            executor.submit {
                val person = WhitePagesDB.person()
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

    fun shutdown() {
        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }
}

object WhitePagesDB {
    fun person(): MongoCollection<Document> = BDS.whitePages().getCollection("person")

    fun shutdown() {
        WhitePagesPersonCollection.shutdown()
    }
}

object BDS {
    fun whitePages(): MongoDatabase = mongoClient.getDatabase(databaseName)

    private val mongoConf: JsonObject by lazy {
        (WhitePages.conf["mongo"] as? JsonObject) ?: Json.createObjectBuilder().build()
    }

    private val address: ServerAddress by lazy {
        ServerAddress(
            mongoConf.getString("host", ServerAddress.defaultHost()),
            mongoConf.getInt("port", ServerAddress.defaultPort())
        )
    }

    private val mongoClient: MongoClient by lazy { MongoClient(address) }

    private val databaseName: String  by lazy { mongoConf.getString("db", "whitePages") }
}

@WebListener
class WhitePagesContextListener: ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent) {
    }

    override fun contextDestroyed(sce: ServletContextEvent) {
        WhitePagesDB.shutdown()
    }
}
