package com.grsu.workshop.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.grsu.workshop.R
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.service.ConnectService

class MainActivity : AppCompatActivity() {

    private inner class ServiceConnector : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("main_activity", "connection to service")

            if (service !is ConnectService.ConnectServiceBinder)
                return

            val s = service.service

            _viewModel.bindService(s)

            Log.d("main_activity", "connected to service")
        }
    }

    private val _connector = ServiceConnector()
    private val _viewModel by viewModels<MainViewModel>()
    private lateinit var _deviceTitle: TextView

    private fun u(it: IDevice) {
        val uiAdapter = it.uiAdapter()

        val content = uiAdapter.contentFragment()
        val title = uiAdapter.titleFragment()

        _deviceTitle.setText(uiAdapter.title)

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.device_content,
                content
            )
            .replace(
                R.id.title_content,
                title
            )
            .commitNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        _deviceTitle = findViewById(R.id.device_title)

        _viewModel.device.observe(this, Observer {
            Log.d("main_fragment", "observer cal()")
            u(it)
        })

        _viewModel.message.observe(this, Observer {
            Snackbar.make(findViewById(R.id.device_content), it, Snackbar.LENGTH_LONG)
                .setAction(it, null)
                .show()
        })

        val intent = Intent(this, ConnectService::class.java)
        bindService(intent, _connector, Context.BIND_AUTO_CREATE)

        Log.d("main_activity", "created")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }
}
