package ftf.grsu.workshop.services

import android.bluetooth.BluetoothDevice
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.device.IMeterBuilder
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock

class MeterBuilder(
    private val _bluetoothDevice: BluetoothDevice,
    private val _period: Long,
    private val _executorService: ExecutorService
) : IMeterBuilder {
    companion object Constants {
        private val LOCK = ReentrantLock()
    }

    override val title: String = _bluetoothDevice.name ?: ""
    override val address: String = _bluetoothDevice.address

    @ExperimentalUnsignedTypes
    override val meter: Meter
        get() {
            LOCK.lock()
            val meter = configure()
            LOCK.unlock()
            return meter
        }

    @ExperimentalUnsignedTypes
    private fun configure(): Meter {
        val uids = _bluetoothDevice.uuids ?: throw UnsupportedDeviceException()
        val uid = uids.first()

        val socket = _bluetoothDevice.createRfcommSocketToServiceRecord(uid.uuid)
        val transmitter = Transmitter(socket, _period, _executorService)

        return Meter(address, transmitter)
    }
}