package es.jesus24041998.myvacations.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.utils.NetworkManager
import es.jesus24041998.myvacations.utils.NetworkStatusListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val dataStoreRepository: DataStore<SettingsApp>,
    private val networkManager: NetworkManager
) : BaseViewModel(), NetworkStatusListener {

    private val _travels = MutableStateFlow<List<Travel>>(emptyList())
    val travels: MutableStateFlow<List<Travel>> = _travels

    private val _firstTime = MutableStateFlow<Boolean>(true)
    val firstTime: MutableStateFlow<Boolean> = _firstTime

    private val _isConnection = MutableLiveData(networkManager.isNetworkAvailable())
    val isConnection: LiveData<Boolean> = _isConnection

    var loginExecuted = mutableStateOf(false)

    init {
        networkManager.registerNetworkCallback(this)
        viewModelScope.launch {
            dataStoreRepository.data.collect { settings ->
                _firstTime.value = settings.firstTime
            }
        }
    }

    override fun onCleared() {
        networkManager.unregisterNetworkCallback()
        super.onCleared()
    }

    override fun onConnected() {
        _isConnection.postValue(true)
    }

    override fun onDisconnected() {
        _isConnection.postValue(false)
    }

    private fun checkInternetConection() = networkManager.isNetworkAvailable()

    fun loadingState(boolean: Boolean) {
        setLoadingState(boolean)
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    private fun saveTravelLocal(travel: Travel) {
        viewModelScope.launch {
            setLoadingState(true)
            dataStoreRepository.updateData {
                val updateTravel = it.travels + travel
                it.copy(travels = updateTravel)
            }
            setLoadingState(false)
        }
    }

    fun saveFirstTime(boolean: Boolean = false) {
        viewModelScope.launch {
            dataStoreRepository.updateData {
                it.copy(firstTime = boolean)
            }
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

    fun getTravels() {
        loadingState(true)
        getLocalTravels()
        checkCloudTravels()
        loadingState(false)
    }

    private fun checkCloudTravels() {
        viewModelScope.launch {
            loadingState(true)
            if (checkInternetConection()) {
                if (getCurrentUser()?.isAnonymous == false) {
                    val user = firebaseAuth.currentUser?.isAnonymous?.not()
                    if (user == true) {
                        firebaseAuth.currentUser?.uid?.let {
                            firebaseFirestore.collection("users")
                                .document(it)
                                .collection("travels")
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        val travel = document.toObject<Travel>()
                                        travel.let { travelOnline ->
                                            if (_travels.value.find { travel -> travel.id == travelOnline.id } == null) {
                                                saveTravelLocal(travelOnline)
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    throw exception
                                }
                        }
                    }
                }
            } else {
                //TODO NO HAY INTERNET
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
            getLocalTravels()
            setLoadingState(true)
            val user = firebaseAuth.currentUser?.isAnonymous?.not()
            if (user == true) {
                travels.value.forEachIndexed { index, travel ->
                    if (!travel.online) {
                        firebaseAuth.currentUser?.uid?.let {
                            val travelname = travel.id.split("_")[1]
                            val travelOnline = Travel(
                                true,
                                it + "_" + travelname,
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
                                .document(travelOnline.id)
                                .set(travelOnline)
                                .addOnSuccessListener {
                                    viewModelScope.launch {
                                        dataStoreRepository.updateData { currentSettings ->
                                            val updateTravel =
                                                currentSettings.travels.filterNot { travels -> travels.id == travel.id } + travelOnline
                                            currentSettings.copy(travels = updateTravel)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    it.message?.let { it1 -> Log.e("ERROR", it1) }
                                }
                                .addOnCompleteListener { setLoadingState(false) }
                        }
                    }
                }
            }
            setLoadingState(false)
        }
    }
}
