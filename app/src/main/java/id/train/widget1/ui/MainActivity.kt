package id.train.widget1.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import id.train.widget1.MyApplication.Companion.prefManager
import id.train.widget1.R
import id.train.widget1.utils.Constants.CONNECT_DISCONNECT
import id.train.widget1.utils.sendIntentService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    override fun onResume() {
        setupView()
        super.onResume()
    }

    override fun onDestroy() {
        isConnected = prefManager.spCheckConnection
        super.onDestroy()
    }

    private fun setupView() {
        isConnected = prefManager.spCheckConnection
        if (isConnected) connectedView() else notConnectedView()

        btn_start.setOnClickListener {
            if (!isConnected) {
                connectVpn()
            } else {
                disconnectVpn()
            }

            sendIntentService(CONNECT_DISCONNECT)
            prefManager.spCheckConnection = isConnected
        }
    }

    private fun connectedView() {
        progress_home.visibility = View.INVISIBLE
        btn_start.setBackgroundColor(Color.GREEN)
        isConnected = true
    }

    private fun notConnectedView() {
        progress_home.visibility = View.INVISIBLE
        btn_start.setBackgroundColor(Color.RED)
        isConnected = false
    }

    private fun connectingView() {
        progress_home.visibility = View.VISIBLE
    }

    fun connectVpn() {
        connectingView()
        Handler().postDelayed({
            connectedView()
        }, 1500)
    }

    fun disconnectVpn() {
        notConnectedView()
    }

    companion object {
        private var isConnected = false

        const val CONNECTED_KEY = "CONNECTED"
        const val NOT_CONNECTED_KEY = "NOT CONNECTED"
        const val CONNECTING_KEY = "CONNECTING . . ."
        const val WIDGET_KEY = "MY WIDGET"
    }
}