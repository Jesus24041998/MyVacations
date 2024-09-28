package es.jesus24041998.myvacations.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

open class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("BaseViewModel", "CoroutineExceptionHandler handled crash $exception")
    }
    private var _scope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
    protected val scope get() = _scope

    fun reinitScope() {
        if (!isJobActive()) {
            _scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)
        }
    }

    fun isJobActive() = _scope.isActive
    fun cancelJob() = _scope.cancel()
    override fun onCleared() {
        super.onCleared()
        _scope.cancel()
    }

    // Función para cambiar el estado de carga
    protected fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}