package com.mysafe.lib_base.expansion.NetState

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkReceiver {
    private lateinit var mContext: Context

    private var networkCallback: NetworkCallBackImpl? = NetworkCallBackImpl()
    private var request: NetworkRequest = NetworkRequest.Builder().build()
    private var manager: ConnectivityManager? = null
    private var listener: OnNetworkListener? = null

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkReceiver()
        }
    }

    private fun getManager(): ConnectivityManager {
        if (manager == null) {
            manager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        return manager!!
    }

    fun initContext(mContext: Context) {
        this.mContext = mContext
    }

    fun init(listener: OnNetworkListener) {
        this.listener = null
        this.listener = listener
        if (listener != null) {
            networkCallback?.setListener(listener)
        }
    }

    /**
     * 建议注册网络监听放在onResume中
     * unregister放在onPause中取消注册
     */
    fun registerNet() {
        getManager().registerNetworkCallback(request, networkCallback)
    }

    /**
     * unregister放在onPause中取消注册
     */
    fun unregisterNet() {
        listener = null
        getManager().unregisterNetworkCallback(networkCallback)
        networkCallback?.setListener(listener)
    }
}
