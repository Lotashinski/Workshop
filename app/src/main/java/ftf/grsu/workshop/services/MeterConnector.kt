package ftf.grsu.workshop.services

import android.bluetooth.BluetoothDevice
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.device.IMeterBuilder
import java.util.concurrent.locks.ReentrantLock

class MeterConnector(private val bluetoothDevice: BluetoothDevice) : IMeterBuilder {
    companion object Constants {
        private val LOCK = ReentrantLock()
    }

    override val title: String = bluetoothDevice.name ?: ""
    override val address: String = bluetoothDevice.address
    override val meter: Meter
        get() {
            LOCK.lock()
            val meter = configure()
            LOCK.unlock()

            return meter
        }

    private fun configure(): Meter {
        val uids = bluetoothDevice.uuids ?: throw UnsupportedDeviceException()
        val uid = uids.first()

        val socket = bluetoothDevice.createRfcommSocketToServiceRecord(uid.uuid)
        val transmitter = Transmitter(socket)

        return Meter(address, transmitter)
    }
}