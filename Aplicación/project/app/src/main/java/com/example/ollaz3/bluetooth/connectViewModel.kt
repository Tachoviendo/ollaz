//package com.example.ollaz3.bluetooth
//
//import android.Manifest
//import android.bluetooth.BluetoothDevice
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.os.Build
//import android.util.Log
//import androidx.activity.result.ActivityResultLauncher
//import androidx.compose.runtime.getValue
//import androidx.core.app.ActivityCompat
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//data class ConnectUiState(
//    val bluetoothSupported: Boolean = true,
//    val permissionsGranted: Boolean = false,
//    val bluetoothEnabled: Boolean = false,
//    val isDiscovering: Boolean = false,
//    val devices: List<DiscoveredBluetoothDevice> = emptyList(),
//    val bondStateMessage: String? = null,
//    val error: String? = null,
//    val userRequestedBluetoothEnable: Boolean = false,
//    val bondingDeviceAddress: String? = null,
//    val requiredPermissions: Array<String> = emptyArray() // Añade esta línea
//)
//
//sealed class ConnectUiEvent {
//    object CheckBluetoothSupport : ConnectUiEvent()
//    object CheckPermissions : ConnectUiEvent()
//    data class PermissionsResult(val granted: Boolean) : ConnectUiEvent()
//    data class RequestEnableBluetooth(val launcher: ActivityResultLauncher<Intent>) : ConnectUiEvent()
//    data class BluetoothEnableResult(val enabled: Boolean) : ConnectUiEvent()
//    object ToggleScan : ConnectUiEvent()
//    data class ConnectDevice(val address: String) : ConnectUiEvent()
//    object DisconnectDevice : ConnectUiEvent()
//
//}
//
//class ConnectViewModel(private val context: Context) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(ConnectUiState())
//    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()
//
//    private val scanner = Scanner(context, viewModelScope)
//
//    private val bluetoothAdapter = BluetoothHelper.getBluetoothAdapter(context)
//
//
//    private var bondStateReceiver: BroadcastReceiver? = null
//
//    init {
//        checkBluetoothSupport()
//        checkPermissions()
//        registerBondStateReceiver()
//        observeScannerState()
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        unregisterBondStateReceiver() // Desregistrar el receiver aquí
//        scanner.unregisterReceiver() // Desregistrar el receiver del Scanner
//        cancelDiscovery()
//    }
//
//    fun onEvent(event: ConnectUiEvent) {
//        when (event) {
//            ConnectUiEvent.CheckBluetoothSupport -> checkBluetoothSupport()
//            ConnectUiEvent.CheckPermissions -> checkPermissions()
//            is ConnectUiEvent.PermissionsResult -> handlePermissionsResult(event.granted)
//            is ConnectUiEvent.RequestEnableBluetooth -> requestEnableBluetooth(event.launcher)
//            is ConnectUiEvent.BluetoothEnableResult -> handleBluetoothEnableResult(event.enabled)
//            ConnectUiEvent.ToggleScan -> toggleScan()
//            is ConnectUiEvent.ConnectDevice -> connectDevice(event.address)
//            ConnectUiEvent.DisconnectDevice -> TODO()
//        }
//    }
//
//    private fun checkBluetoothSupport() {
//        _uiState.update { it.copy(bluetoothSupported = BluetoothHelper.isBluetoothSupported(context)) }
//    }
//
//    private fun checkPermissions() {
//        _uiState.update {
//            it.copy(
//                permissionsGranted = BluetoothHelper.hasAllBluetoothPermissions(context),
//                requiredPermissions = BluetoothHelper.getRequiredBluetoothPermissions()
//            )
//        }
//    }
//
//    private fun handlePermissionsResult(granted: Boolean) {
//        _uiState.update { it.copy(permissionsGranted = granted) }
//        if (granted) {
//            checkBluetoothEnabled()
//        }
//    }
//
//    private fun checkBluetoothEnabled() {
//        _uiState.update { it.copy(bluetoothEnabled = BluetoothHelper.isBluetoothEnabled(context)) }
//        if (!_uiState.value.bluetoothEnabled) {
//            _uiState.update { it.copy(userRequestedBluetoothEnable = true) }
//        }
//    }
//
//    private fun requestEnableBluetooth(launcher: ActivityResultLauncher<Intent>) {
//        BluetoothHelper.requestEnableBluetoothWithLauncher(launcher)
//    }
//
//    private fun handleBluetoothEnableResult(enabled: Boolean) {
//        _uiState.update { it.copy(bluetoothEnabled = enabled, userRequestedBluetoothEnable = false) }
//    }
//
//    private fun toggleScan() {
//        Log.d("ConnectViewModel", "ToggleScan called. Permissions: ${_uiState.value.permissionsGranted}, Bluetooth: ${_uiState.value.bluetoothEnabled}, Discovering: ${_uiState.value.isDiscovering}")
//
//        if (!_uiState.value.permissionsGranted || !_uiState.value.bluetoothEnabled) {
//            _uiState.update { it.copy(error = "Se requieren permisos y Bluetooth activado para escanear.") }
//            return
//        }
//
//        if (_uiState.value.isDiscovering) {
//            cancelDiscovery()
//        } else {
//            startDiscovery()
//        }
//        _uiState.update{it.copy(isDiscovering = !it.isDiscovering)} //Actualizar el estado
//    }
//
//    private fun connectDevice(address: String) {
//        cancelDiscovery()
//        _uiState.update { it.copy(bondStateMessage = "Intentando emparejar...", bondingDeviceAddress = address) }
//        viewModelScope.launch {
//            try {
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    val bluetoothDevice = BluetoothHelper.getBluetoothAdapter(context)?.getRemoteDevice(address)
//                    if (bluetoothDevice != null) {
//                        val bondSucceeded = bluetoothDevice.createBond()
//                        if (!bondSucceeded) {
//                            _uiState.update {
//                                it.copy(
//                                    bondStateMessage = "No se pudo iniciar el emparejamiento.",
//                                    bondingDeviceAddress = null
//                                )
//                            }
//                        }
//                    } else {
//                        _uiState.update {
//                            it.copy(
//                                bondStateMessage = "No se pudo obtener el dispositivo Bluetooth.",
//                                bondingDeviceAddress = null
//                            )
//                        }
//                    }
//                } else {
//                    _uiState.update {
//                        it.copy(
//                            bondStateMessage = "Permiso BLUETOOTH_CONNECT no concedido.",
//                            bondingDeviceAddress = null
//                        )
//                    }
//                }
//            } catch (e: SecurityException) {
//                Log.e("ConnectViewModel", "SecurityException al intentar emparejar: ${e.message}")
//                _uiState.update {
//                    it.copy(
//                        bondStateMessage = "Error de seguridad al emparejar.",
//                        bondingDeviceAddress = null
//                    )
//                }
//            } catch (e: IllegalArgumentException) {
//                Log.e("ConnectViewModel", "IllegalArgumentException (MAC inválida?): ${e.message}")
//                _uiState.update {
//                    it.copy(
//                        bondStateMessage = "Dirección MAC inválida para emparejar.",
//                        bondingDeviceAddress = null
//                    )
//                }
//            }
//        }
//    }
//
//    private fun startDiscovery() {
//        scanner.startDiscovery()
//    }
//
//    private fun cancelDiscovery() {
//        scanner.cancelDiscovery()
//    }
//
//
//    private fun observeScannerState() {
//        viewModelScope.launch {
//            scanner.scannerState.collect { scannerState ->
//                _uiState.update {
//                    it.copy(
//                        devices = scannerState.devices,
//                        isDiscovering = scannerState.isDiscovering,
//                        error = scannerState.error
//                    )
//                }
//            }
//        }
//    }
//
//    private fun registerBondStateReceiver() {
//        bondStateReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                if (intent.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
//                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
//                    } else {
//                        @Suppress("DEPRECATION")
//                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    }
//
//                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
//                    val previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)
//
//                    device?.let { currentDevice ->
//                        if (currentDevice.address == _uiState.value.bondingDeviceAddress) {
//                            val deviceName = try {
//                                if (ActivityCompat.checkSelfPermission(
//                                        context,
//                                        Manifest.permission.BLUETOOTH_CONNECT
//                                    ) == PackageManager.PERMISSION_GRANTED
//                                ) {
//                                    currentDevice.name ?: "Dispositivo Desconocido"
//                                } else {
//                                    "Dispositivo (sin permiso para nombre)"
//                                }
//                            } catch (e: SecurityException) {
//                                "Dispositivo (error al obtener nombre)"
//                            }
//
//                            when (bondState) {
//                                BluetoothDevice.BOND_BONDING -> {
//                                    _uiState.update { it.copy(bondStateMessage = "Emparejando con $deviceName...") }
//                                }
//                                BluetoothDevice.BOND_BONDED -> {
//                                    _uiState.update {
//                                        it.copy(
//                                            bondStateMessage = "$deviceName emparejado exitosamente.",
//                                            bondingDeviceAddress = null
//                                        )
//                                    }
//                                }
//                                BluetoothDevice.BOND_NONE -> {
//                                    _uiState.update {
//                                        it.copy(
//                                            bondStateMessage = "No se pudo emparejar con $deviceName.",
//                                            bondingDeviceAddress = null
//                                        )
//                                    }
//                                }
//                                else -> {
//                                    Log.d("BondStateReceiver", "Estado de emparejamiento desconocido.")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        context.registerReceiver(bondStateReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
//    }
//
//    private fun unregisterBondStateReceiver() {
//        bondStateReceiver?.let { context.unregisterReceiver(it) }
//    }
//}