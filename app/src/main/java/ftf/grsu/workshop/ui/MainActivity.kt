package ftf.grsu.workshop.ui

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import ftf.grsu.workshop.services.BtRequestService
import androidx.lifecycle.Observer
import ftf.grsu.workshop.R

class MainActivity : AppCompatActivity() {
    private val viewModel: DeviceViewModel by viewModels()
    private val serviceConnector = BtRequestConnector()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothAdapter  = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null){
            Toast
                .makeText(this, R.string.bt_adapter_not_found, Toast.LENGTH_SHORT)
                .show()

            finish()
        }

        if (!bluetoothAdapter.isEnabled) {
            val intentEnableBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intentEnableBt, 1)
        }

        val intent = Intent(this, BtRequestService::class.java)
        bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_chart,
                R.id.nav_device,
                R.id.nav_support
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    inner class BtRequestConnector : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("activity.root", "unbinding service btRequest")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is BtRequestService.Binder) {
                Log.d("activity.root", "binding service btRequest")
                val btRequestService = service.service

                btRequestService.isConnect.observe(this@MainActivity,
                    Observer {
                        viewModel.setIsConnect(it)
                    })
                btRequestService.meterBuilders.observe(this@MainActivity,
                    Observer { viewModel.setBuilders(it) })
                btRequestService.currentMeter.observe(this@MainActivity,
                    Observer { viewModel.setCurrent(it) })

                viewModel.connect = { meterBuilder -> btRequestService.connect(meterBuilder) }
                viewModel.disconnect = { btRequestService.disconnect() }
            }
        }
    }
}
