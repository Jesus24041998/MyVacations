package es.jesus24041998.myvacations.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.Travel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStoreRepository: DataStore<SettingsApp>,
    private val networkUtilities: Boolean

) : BaseViewModel() {
    private val _networkState = MutableLiveData(networkUtilities)
    val networkState: LiveData<Boolean> get() = _networkState
    private val _travels =
        MutableStateFlow<List<Travel>>(emptyList()) // MutableStateFlow que emitirá la lista de viajes
    val travels: MutableStateFlow<List<Travel>> = _travels

    var loginExecuted = mutableStateOf(false)

    fun loadingState(boolean: Boolean) {
        setLoadingState(boolean)
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun saveTravel(travel: Travel) {
        try {
            viewModelScope.launch {
                dataStoreRepository.updateData {
                    val updateTravel = it.travels + travel
                    it.copy(travels = updateTravel)
                }
                getTravels()
            }
        } catch (ioException: IOException) {
            Log.e("TravelPreferences", "Failed to update travel preferences", ioException)
        }
    }

    fun getTravels() {
        viewModelScope.launch {
            dataStoreRepository.data.collect { settings ->
                _travels.value = settings.travels
            }
        }
    }

    suspend fun deleteTravel(travelID: String) {
        dataStoreRepository.updateData { currentSettings ->
            val updateTravel = currentSettings.travels.filterNot { it.id == travelID }
            currentSettings.copy(travels = updateTravel)
        }
    }
}
