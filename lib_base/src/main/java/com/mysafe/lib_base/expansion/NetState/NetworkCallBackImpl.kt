package com.mysafe.lib_base.expansion.NetState

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log

class NetworkCallBackImpl : ConnectivityManager.NetworkCallback() {
    private var listener: OnNetworkListener? = null
    fun setListener(listener: OnNetworkListener?) {
        this.listener = listener
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.d("TAG_NET_onAvailable: ", "网络已连接")
        listener?.onNetworkState(NetworkState.CONNECT)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Log.d("TAG_NET_onLost: ", "网络断开")
        listener?.onNetworkState(NetworkState.NONE)
    }

    override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        Log.d("TAG_NET_WIFI: ", "WIFI已连接")
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.d("TAG_NET_WIFI: ", "WIFI已连接")
            listener?.onNetworkState(NetworkState.WIFI)
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.d("TAG_NET_CELLULAR: ", "以太网已连接")
            listener?.onNetworkState(NetworkState.ETHERNET)
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.d("TAG_NET_CELLULAR: ", "移动网络已连接")
            listener?.onNetworkState(NetworkState.CELLULAR)
        }
    }
}
