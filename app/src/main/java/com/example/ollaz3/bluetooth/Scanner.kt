package com.example.ollaz3.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DiscoveredBluetoothDevice(
    val name: String?,
    val address: String,
    val bluetoothDevice: BluetoothDevice
)

data class ScannerState( // ASEGÚRATE DE QUE ESTA DATA CLASS ESTÉ DEFINIDA
    val devices: List<DiscoveredBluetoothDevice> = emptyList(),
    val isDiscovering: Boolean = false,
    val error: String? = null
)

class Scanner(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothHelper.getBluetoothAdapter(context)
    private val _scannerState = MutableStateFlow(ScannerState())
    val scannerState: StateFlow<ScannerState> = _scannerState.asStateFlow()
    private val discoveredDevicesSet = mutableSetOf<DiscoveredBluetoothDevice>()

    val isDiscovering: Boolean
        @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
        get() = bluetoothAdapter?.isDiscovering ?: false

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(receivedContext: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    device?.let { handleDeviceFound(it) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("Scanner", "Discovery started")
                    discoveredDevicesSet.clear()
                    _scannerState.update { it.copy(isDiscovering = true, devices = emptyList(), error = null) }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("Scanner", "Discovery finished")
                    _scannerState.update { it.copy(isDiscovering = false) }
                }
            }
        }
    }

    private fun handleDeviceFound(device: BluetoothDevice) {
        val hasConnectPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        val deviceName = if (hasConnectPermission) device.name else "Desconocido"
        val newDevice = DiscoveredBluetoothDevice(deviceName, device.address, device)

        if (discoveredDevicesSet.none { it.address == newDevice.address }) {
            discoveredDevicesSet.add(newDevice)
            _scannerState.update { it.copy(devices = discoveredDevicesSet.toList()) }
            Log.d("Scanner", "Dispositivo encontrado: ${newDevice.name ?: "N/A"} - ${newDevice.address}")
        }
    }

    private var discoveryStartTime: Long? = null

    fun startDiscovery() {
        if (bluetoothAdapter == null) {
            _scannerState.update { it.copy(error = "Adaptador Bluetooth no disponible.") }
            Log.e("Scanner", "BluetoothAdapter es null.")
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            _scannerState.update { it.copy(error = "Bluetooth no está habilitado.") }
            Log.e("Scanner", "Bluetooth no está habilitado.")
            return
        }

        if (!BluetoothHelper.hasAllBluetoothPermissions(context)) {
            _scannerState.update { it.copy(error = "Faltan permisos de Bluetooth para escanear.") }
            Log.e("Scanner", "Faltan permisos de Bluetooth para escanear.")
            return
        }

        // Comprobación específica de BLUETOOTH_SCAN para Android S y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            _scannerState.update { it.copy(error = "Permiso BLUETOOTH_SCAN denegado.") }
            Log.e("Scanner", "BLUETOOTH_SCAN permission denied for startDiscovery")
            return
        }
        // Comprobación para ACCESS_FINE_LOCATION (necesario antes de Android S para descubrir)
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _scannerState.update { it.copy(error = "Permiso ACCESS_FINE_LOCATION denegado.") }
            Log.e("Scanner", "ACCESS_FINE_LOCATION permission denied for startDiscovery")
            return
        }


        // Si ya está descubriendo, cancela primero.
        if (bluetoothAdapter.isDiscovering) {
            Log.d("Scanner", "El descubrimiento ya está en progreso. Se reiniciará.")
            bluetoothAdapter.cancelDiscovery() // Es importante cancelar antes de reiniciar
        }

        if (bluetoothAdapter.isDiscovering) {
            Log.d("MyApp", "Cancelando descubrimiento existente...")
            if (!bluetoothAdapter.cancelDiscovery()) {
                Log.e("Scanner", "Fallo al cancelar el descubrimiento existente antes de reiniciar.")
                // Considerar no continuar si la cancelación falla, o manejar el error
            }
        }

        Log.d("MyApp", "Intentando iniciar nuevo descubrimiento...")
        val discoveryStarted = bluetoothAdapter.startDiscovery()
    }

    fun cancelDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Scanner", "Permiso BLUETOOTH_SCAN denegado para cancelDiscovery")
                _scannerState.update { it.copy(error = "Permiso BLUETOOTH_SCAN denegado para cancelar.") }
                return
            }
            if (bluetoothAdapter.cancelDiscovery()) {
                Log.d("Scanner", "Descubrimiento cancelado.")
            } else {
                Log.e("Scanner", "Fallo al cancelar el descubrimiento.")
            }
            _scannerState.update { it.copy(isDiscovering = false) }
        }
    }

    fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(discoveryReceiver, intentFilter)
        Log.d("Scanner", "Discovery receiver registrado.")
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(discoveryReceiver)
            Log.d("Scanner", "Discovery receiver desregistrado.")
        } catch (e: IllegalArgumentException) {
            Log.w("Scanner", "Receiver no estaba registrado o ya fue desregistrado: ${e.message}")
        }
    }


}