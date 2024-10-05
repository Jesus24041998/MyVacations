package es.jesus24041998.myvacations.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.Travel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val dataStoreRepository: DataStore<SettingsApp>

) : BaseViewModel() {
    private val _travels = MutableStateFlow<List<Travel>>(emptyList())
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

    fun saveTravelMain(travel: Travel) {
        try {
            viewModelScope.launch {
                dataStoreRepository.updateData {
                    val updateTravel = it.travels + travel
                    it.copy(travels = updateTravel)
                }
                saveTravelCloud(travel)
            }
        } catch (ioException: IOException) {
            Log.e("TravelPreferences", "Failed to update travel preferences", ioException)
        }
    }

    private fun saveTravelCloud(travel: Travel) {
        try {
            viewModelScope.launch {
                setLoadingState(true)
                val user = firebaseAuth.currentUser?.isAnonymous?.not()
                if (user == true) {
                    firebaseAuth.currentUser?.uid?.let {
                        firebaseFirestore.collection("users").document(it)
                            .collection("travels")
                            .document(travel.id)
                            .get()
                    }
                }
                setLoadingState(false)
            }
        } catch (ioException: IOException) {
            Log.e("TravelPreferences", "Failed to update travel preferences", ioException)
        }
    }

    fun getTravelsLocal() {
        loadingState(true)
        viewModelScope.launch {
            dataStoreRepository.data.collect { settings ->
                _travels.value = settings.travels
            }
        }
        loadingState(false)
    }

    fun deleteTravel(travelID: String) {
        viewModelScope.launch {
            loadingState(true)
            dataStoreRepository.updateData { currentSettings ->
                val updateTravel = currentSettings.travels.filterNot { it.id == travelID }
                currentSettings.copy(travels = updateTravel)
            }
            loadingState(false)
        }
    }

    fun syncWithDataBase() {
        viewModelScope.launch {
            setLoadingState(true)
            getTravelsLocal()
            val userId = firebaseAuth.currentUser?.uid
            val user = firebaseAuth.currentUser?.isAnonymous?.not()
            if (user == true) {
                travels.value.forEach { travel ->
                    userId?.let {
                        firebaseFirestore.collection("users").document(it)
                            .collection("travels")
                            .document(travel.id)
                            .get()
                    }
                }
            }
            setLoadingState(false)
        }
    }
}
