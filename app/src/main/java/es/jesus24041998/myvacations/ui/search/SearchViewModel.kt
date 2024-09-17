package es.jesus24041998.myvacations.ui.search

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {
    var loginExecuted = mutableStateOf(false)


    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

}