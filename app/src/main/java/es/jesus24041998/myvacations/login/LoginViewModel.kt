package es.jesus24041998.myvacations.login

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.getCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jesus24041998.myvacations.base.BaseViewModel
import es.jesus24041998.myvacations.utils.SnackBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : BaseViewModel() {

    private val _authState = MutableLiveData<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)
    val authState: LiveData<FirebaseUser?> get() = _authState

    private val _toastState = MutableLiveData<SnackBarState>()
    val toastState: LiveData<SnackBarState?> get() = _toastState

    private val _throwHome = MutableLiveData(false)
    val throwHome: LiveData<Boolean> get() = _throwHome
    fun init() {
        if (firebaseAuth.currentUser == null) {
            onOfflineLogin()
        }
    }

    private fun login(exception: Exception, email: String, password: String) {
        viewModelScope.launch {
            if (exception.message == "The email address is already in use by another account.") {
                firebaseAuth.currentUser?.delete()
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    if (firebaseAuth.currentUser?.isEmailVerified == true) {
                        setLoadingState(false)
                        _toastState.value = SnackBarState.LOGIN_OK
                        _authState.value = firebaseAuth.currentUser
                        _throwHome.value = true
                    } else {
                        setLoadingState(false)
                        _toastState.value = SnackBarState.EMAIL_NOT_VERIFIED
                        _throwHome.value = false
                    }
                }
                .addOnFailureListener {
                    setLoadingState(false)
                    _toastState.value =
                        when (it.localizedMessage) {
                            "We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]" -> SnackBarState.ACCOUNT_BLOCK
                            else -> SnackBarState.WRONG_CREDENTIALS
                        }
                }
        }
    }

    fun onRegisterClick(email: String, password: String) {
        viewModelScope.launch {
            setLoadingState(true)
            delay(2000)
            val credential = EmailAuthProvider.getCredential(email, password)
            firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnSuccessListener {
                val user = firebaseAuth.currentUser
                user?.sendEmailVerification()?.addOnSuccessListener {
                    _toastState.value = SnackBarState.EMAIL_SEND_VERIFICATION
                    setLoadingState(false)
                }
            }?.addOnFailureListener {
                login(it, email, password)
            }
        }
    }

    //Phone EndRequest

    fun resendVerificationEmail() {
        viewModelScope.launch {
            setLoadingState(true)
            delay(2000)
            firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                _toastState.value = SnackBarState.EMAIL_RESENT_VERIFICATION
                setLoadingState(false)
            }?.addOnFailureListener {
                _toastState.value =
                    if (it.message == "We have blocked all requests from this device due to unusual activity. Try again later.") SnackBarState.EMAIL_RESENT_VERIFICATION_FAIL else SnackBarState.LOGIN_FAILED
                setLoadingState(false)
            }
        }
    }

    fun emailPasswordMalformed() {
        _toastState.value = SnackBarState.EMAIL_PASSWORD_MALFORMED
    }

    private fun deleteUser() {
        viewModelScope.launch {
            Firebase.auth.currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                    }
                }
        }
    }

    private fun onOfflineLogin() {
        viewModelScope.launch {
            setLoadingState(true)
            firebaseAuth.signInAnonymously().addOnCompleteListener {
                setLoadingState(false)
            }
        }
    }
}