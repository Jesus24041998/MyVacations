import androidx.datastore.core.DataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.jesus24041998.myvacations.ui.datastore.Activity
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Extra
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val dataStore: DataStore<SettingsApp> = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val firebaseStore: FirebaseFirestore = mockk(relaxed = true)
    private val networkUtilities: Boolean = true
    private val testDispatcher = StandardTestDispatcher()

    // Mock the DataStore flow to simulate stored data
    private val settingsTravelFlow = MutableStateFlow(SettingsApp())

    private var newTravel: Travel = Travel(
        id = "1",
        name = "Viaje a la montaña",
        description = "Excursión",
        activityList = emptyList(),
        initDate = 1000L,
        endDate = 2000L,
        extraList = emptyList(),
        total = 0.0,
        coin = Coin("EUR")
    )
    private val defaultTravel = arrayListOf(newTravel)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { dataStore.data } returns settingsTravelFlow
        viewModel = HomeViewModel(firebaseAuth, firebaseStore, dataStore, networkUtilities)
        val activity = listOf(
            Activity(
                name = "Visita cazorla",
                description = "Visitamos Cazorla",
                initDate = dateTime(21, 9, 2024).toEpochMilli(),
                1.0
            ),
            Activity(
                name = "Ruta cerrada de Utrero",
                description = "La ruta se encuentra en Cazorla",
                initDate = dateTime(21, 9, 2024).toEpochMilli()
            ),
            Activity(
                name = "Ruta rio Barosa",
                description = "La ruta se encuentra al lado del rio Barosa",
                initDate = dateTime(22, 9, 2024).toEpochMilli()
            )
        )
        val extra = listOf(
            Extra(description = "Cerveza", priceOrNot = 3.0),
            Extra(description = "Cafe", priceOrNot = 1.0)
        )
        // Crear un nuevo viaje
        newTravel = Travel(
            id = "1",
            name = "Cazorla",
            description = "Un viaje a Cazorla , veremos sus calles y monumentos , tambien haremos un poco de ruta por su montaña",
            activityList = activity,
            initDate = 1000L,
            endDate = 2000L,
            extraList = extra,
            total = 0.0,
            coin = Coin("EUR")
        )
        defaultTravel.add(newTravel)
    }

    private fun dateTime(day: Int, month: Int, year: Int) =
        LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant()

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Restablece el dispatcher main a su valor predeterminado
    }

    @Test
    fun `agregarViaje should add a new viaje to the DataStore`() = runTest {
        // Llamar a la función de agregar viaje en el ViewModel
        viewModel.saveTravelMain(newTravel)

        // Verificar que el viaje ha sido agregado
        val viajes = settingsTravelFlow.first().travels
        assertEquals(1, viajes.size)
        assertEquals(newTravel, viajes[0])

        // Verificar que el DataStore fue actualizado correctamente
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `eliminarViaje should remove the viaje from the DataStore`() = runTest {
        val testDelete = defaultTravel
        // Actualizar el flujo del DataStore con la lista simulada
        settingsTravelFlow.value = SettingsApp(travels = testDelete)

        // Llamar a la función de eliminar viaje
        viewModel.deleteTravel("1")

        // Verificar que el viaje ha sido eliminado
        val viajesRestantes = settingsTravelFlow.first().travels
        assertEquals(1, viajesRestantes.size)
        assertEquals("2", viajesRestantes[0].id)

        // Verificar que el DataStore fue actualizado correctamente
        coVerify { dataStore.updateData(any()) }
    }
}