package net.paypredict.patient.id

import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import java.util.concurrent.locks.ReentrantLock
import javax.json.Json
import javax.json.JsonObject
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import kotlin.concurrent.withLock

/**
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 7/16/2018.
 */

object DBS {
    fun ptn(): MongoDatabase = mongoClient.getDatabase(databaseName)

    object Collections {
        fun reference(): MongoCollection<Document> =
            ptn().getCollection("reference")

        fun whitePagesPerson(): MongoCollection<Document> =
            ptn().getCollection("whitePagesPerson")
    }

    private val mongoConf: JsonObject by lazy {
        (PatientId.conf["mongo"] as? JsonObject) ?: Json.createObjectBuilder().build()
    }

    private val address: ServerAddress by lazy {
        ServerAddress(
            mongoConf.getString("host", ServerAddress.defaultHost()),
            mongoConf.getInt("port", ServerAddress.defaultPort())
        )
    }

    private val mongoClient: MongoClient by lazy {
        MongoClient(address)
    }

    private val databaseName: String  by lazy {
        mongoConf.getString("db", "ptn")
    }

    fun addShutdownListener(shutdownListener: () -> Unit) = lock.withLock {
        shutdownListeners += shutdownListener
    }
}

private val lock = ReentrantLock()
private val shutdownListeners = mutableListOf<() -> Unit>()

@WebListener
class PatientIdContextListener : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent) {
    }

    override fun contextDestroyed(sce: ServletContextEvent) {
        lock.withLock { shutdownListeners.toList() }.forEach { listener ->
            try {
                listener()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
