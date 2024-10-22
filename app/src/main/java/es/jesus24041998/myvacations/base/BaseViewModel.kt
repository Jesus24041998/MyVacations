package es.jesus24041998.myvacations.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    protected fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}