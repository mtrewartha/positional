import org.gradle.api.Project
import java.io.File
import java.util.*

class KeyStore(project: Project) {

    val file: File by lazy { project.file(properties.getProperty(PROPERTIES_KEY_STORE_FILE)) }
    val keyAlias: String by lazy { properties.getProperty(PROPERTIES_KEY_ALIAS) }
    val keyPassword: String by lazy { properties.getProperty(PROPERTIES_KEY_PASSWORD) }
    val password: String by lazy { properties.getProperty(PROPERTIES_KEY_STORE_PASSWORD) }

    private val properties: Properties by lazy {
        Properties().apply { load(project.file(PROPERTIES_FILE_PATH).inputStream()) }
    }

    companion object {
        private const val PROPERTIES_FILE_PATH = "upload_keystore.properties"
        private const val PROPERTIES_KEY_ALIAS = "keyAlias"
        private const val PROPERTIES_KEY_PASSWORD = "keyPassword"
        private const val PROPERTIES_KEY_STORE_FILE = "storeFile"
        private const val PROPERTIES_KEY_STORE_PASSWORD = "storePassword"
    }
}