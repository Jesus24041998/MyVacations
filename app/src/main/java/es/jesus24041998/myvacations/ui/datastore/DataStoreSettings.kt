package es.jesus24041998.myvacations.ui.datastore

import android.icu.util.Currency
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class Coin(
    val currencyCode: String = Currency.getInstance(Locale.US).currencyCode,
)

@Serializable
data class Activity(
    val name: String = "",
    val description: String = "",
    val initDate: String = "",
    val priceOrNot: Double? = null,
)

@Serializable
data class Extra(
    val description: String = "",
    val priceOrNot: Double? = null,
)

@Serializable
data class Travel(
    val online: Boolean = false,
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val activityList: List<Activity> = emptyList(),
    val initDate: String = "",
    val endDate: String = "",
    val extraList: List<Extra> = emptyList(),
    val total: Double = 0.0,
    val coin: Coin = Coin()
)

@Serializable
data class SettingsApp(
    val travels: List<Travel> = emptyList(),
    val firstTime: Boolean = true
)

@Singleton
class SettingsSerializer @Inject constructor() : Serializer<SettingsApp> {

    override val defaultValue = SettingsApp()

    override suspend fun readFrom(input: InputStream): SettingsApp =
        try {
            Json.decodeFromString(
                SettingsApp.serializer(), input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Settings", serialization)
        }

    override suspend fun writeTo(t: SettingsApp, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(SettingsApp.serializer(), t)
                    .encodeToByteArray()
            )
        }
    }
}