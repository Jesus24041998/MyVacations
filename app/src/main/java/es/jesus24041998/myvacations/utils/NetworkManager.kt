package es.jesus24041998.myvacations.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log

@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var lastNetwork: Network? = null
    private var lastConnectionState: Boolean? = null

    private lateinit var connectivityManager: ConnectivityManager
    private var listener: NetworkStatusListener? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (network == lastNetwork && lastConnectionState == true) {
                return
            }
            if (isNetworkAvailable()) {
                listener?.onConnected()
                lastConnectionState = true
            }
            lastNetwork = network
        }

        override fun onLost(network: Network) {
            if (network == lastNetwork && lastConnectionState == false) {
                return
            }
            if (!isNetworkAvailable()) {
                listener?.onDisconnected()
                lastConnectionState = false
            }
            lastNetwork = network
        }
    }

    fun registerNetworkCallback(networkStatusListener: NetworkStatusListener) {
        listener = networkStatusListener
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}

interface NetworkStatusListener {
    fun onConnected()
    fun onDisconnected()
}
