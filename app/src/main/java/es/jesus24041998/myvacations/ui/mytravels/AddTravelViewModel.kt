package es.jesus24041998.myvacations.ui.mytravels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.utils.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddTravelViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val dataStoreRepository: DataStore<SettingsApp>,
    private val networkManager: NetworkManager
) : BaseViewModel() {
    private val _travels = MutableStateFlow<List<Travel>>(emptyList())

    private val _isOnline = MutableStateFlow<Boolean>(checkInternetConection())
    private val isOnline: MutableStateFlow<Boolean> = _isOnline

    init {
        isOnline.value = checkInternetConection() && getCurrentUser()?.isAnonymous == false
        getLocalTravels()
    }

    private fun loadingState(boolean: Boolean) {
        setLoadingState(boolean)
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private fun checkInternetConection() = networkManager.isNetworkAvailable()

    fun saveTravelMain(travel: Travel) {
        try {
            viewModelScope.launch {
                setLoadingState(true)
                if (isOnline.value) {
                    saveTravelCloud(travel)
                }

                dataStoreRepository.updateData {
                    val travelreformat = Travel(
                        online = isOnline.value,
                        id = travel.id,
                        name = travel.name,
                        description = travel.description,
                        activityList = travel.activityList,
                        initDate = travel.initDate,
                        endDate = travel.endDate,
                        extraList = travel.extraList,
                        total = travel.total,
                        coin = travel.coin
                    )
                    val updateTravel = it.travels + travelreformat
                    it.copy(travels = updateTravel)
                }
                setLoadingState(false)
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
                        val travelOnline = Travel(
                            online = isOnline.value,
                            travel.id,
                            travel.name,
                            travel.description,
                            travel.activityList,
                            travel.initDate,
                            travel.endDate,
                            travel.extraList,
                            travel.total,
                            travel.coin
                        )

                        firebaseFirestore.collection("users").document(it)
                            .collection("travels")
                            .document(travel.id)
                            .set(travelOnline)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {
                                isOnline.value = false
                            }
                            .addOnCompleteListener { setLoadingState(false) }
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("TravelPreferences", "Failed to update travel preferences", ioException)
        }
    }

    private fun getLocalTravels() {
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
}
