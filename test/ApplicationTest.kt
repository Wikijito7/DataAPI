package es.wokis

import com.typesafe.config.ConfigFactory
import es.wokis.dataapi.module
import io.ktor.config.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import org.junit.BeforeClass

class ApplicationTest {
    companion object {
        val engine = TestApplicationEngine(createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        })

        @BeforeClass
        @JvmStatic fun setup(){
            engine.start(wait = false)
        }
    }

    @Test
    fun testRoot() {
        with(engine) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
