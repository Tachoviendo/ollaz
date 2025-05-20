package com.example.ollaz3.ui.screens // O tu paquete de UI

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.ollaz3.bluetooth.BluetoothHelper
import com.example.ollaz3.bluetooth.Scanner
import com.example.ollaz3.ui.theme.Ollaz3Theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDeviceScreen(navController: NavHostController, monitorViewModel: MonitorViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scanner : Scanner = remember { Scanner(context, coroutineScope) }
    val scannerStateValue by scanner.scannerState.collectAsStateWithLifecycle()

    var bluetoothSupported by remember { mutableStateOf(true) }
    // Inicializa basado en el estado actual al componer
    var permissionsGranted by remember { mutableStateOf(BluetoothHelper.hasAllBluetoothPermissions(context)) }
    var bluetoothEnabled by remember { mutableStateOf(BluetoothHelper.isBluetoothEnabled(context)) }

    DisposableEffect(key1 = scanner, key2 = permissionsGranted, key3 = bluetoothEnabled) {
        val canScan = permissionsGranted && bluetoothEnabled
        Log.d("ConnectDeviceScreen", "DisposableEffect: canScan = $canScan")

        if (canScan) {
            // Solo registra el receiver si podemos escanear
            Log.d("ConnectDeviceScreen", "Registrando el discoveryReceiver del Scanner.")
            scanner.registerReceiver() // <--- LLAMADA A TU FUNCIÓN
        }

        onDispose {
            // Siempre intenta desregistrar, incluso si no se registró (unregisterReceiver debería manejarlo)
            Log.d("ConnectDeviceScreen", "Desregistrando el discoveryReceiver del Scanner.")
            scanner.unregisterReceiver()
            // También cancela el descubrimiento si estaba activo
            if (scannerStateValue.isDiscovering) {
                scanner.cancelDiscovery()
            }
        }
    }
    // Flag para controlar si debemos pedir explícitamente la activación de BT
    // después de que los permisos sean otorgados o si el usuario lo pide.
    var userRequestedBluetoothEnable by remember { mutableStateOf(false) }
    var bondingDeviceAddress by remember { mutableStateOf<String?>(null) } // Guarda la MAC del dispositivo que se está emparejando
    var bondStateMessage by remember { mutableStateOf<String?>(null) }


    // ... (Dentro de ConnectDeviceScreen, después de los launchers y antes de los LaunchedEffects)

    val bondStateReceiver = remember(context) { // `remember` para que el receiver persista
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                    // Obtener el BluetoothDevice del Intent
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        // Para versiones anteriores a Android 13 (API 33), EXTRA_DEVICE no toma la clase
                        @Suppress("DEPRECATION") // Suprimir la advertencia de desuso para esta rama
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }

                    // Obtener el nuevo estado del vínculo y el estado anterior
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                    val previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

                    Log.d("BondStateReceiver", "ACTION_BOND_STATE_CHANGED: device=${device?.address}, bondState=$bondState, previousBondState=$previousBondState")

                    device?.let { currentDevice -> // Renombrar a currentDevice para claridad
                        // Solo actuar si es el dispositivo que estamos intentando emparejar activamente
                        if (currentDevice.address == bondingDeviceAddress) {
                            val deviceName: String = try {
                                // El acceso a 'name' requiere BLUETOOTH_CONNECT en API 31+
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.BLUETOOTH_CONNECT // Asegúrate que Manifest esté importado
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    currentDevice.name ?: "Dispositivo Desconocido"
                                } else {
                                    Log.w("BondStateReceiver", "BLUETOOTH_CONNECT permission not granted for device name.")
                                    "Dispositivo (sin permiso para nombre)"
                                }
                            } catch (e: SecurityException) {
                                Log.e("BondStateReceiver", "SecurityException al obtener nombre de dispositivo: ${e.message}")
                                "Dispositivo (error al obtener nombre)"
                            }

                            when (bondState) {
                                BluetoothDevice.BOND_BONDING -> {
                                    bondStateMessage = "Emparejando con $deviceName..."
                                    Log.i("BondStateReceiver", "Emparejando con $deviceName (${currentDevice.address})")
                                }
                                BluetoothDevice.BOND_BONDED -> {
                                    bondStateMessage = "$deviceName emparejado exitosamente."
                                    Log.i("BondStateReceiver", "$deviceName (${currentDevice.address}) emparejado exitosamente.")
                                    // Aquí podrías querer actualizar una lista de dispositivos emparejados
                                    // o navegar a otra pantalla.
                                    bondingDeviceAddress = null // Limpiar la dirección del dispositivo en proceso de emparejamiento
                                }
                                BluetoothDevice.BOND_NONE -> {
                                    bondStateMessage = "No se pudo emparejar con $deviceName."
                                    Log.w("BondStateReceiver", "Fallo al emparejar con $deviceName (${currentDevice.address}). Estado anterior: $previousBondState")
                                    bondingDeviceAddress = null // Limpiar la dirección
                                }
                                else -> {
                                    Log.d("BondStateReceiver", "Estado de emparejamiento desconocido: $bondState para $deviceName (${currentDevice.address})")
                                }
                            }
                        } else {
                            // Log para depuración si recibimos un evento de otro dispositivo
                            Log.d("BondStateReceiver", "Bond state changed for a different device: ${currentDevice.address}, current bonding target: $bondingDeviceAddress")
                        }
                    }
                }
            }
        }
    }

// ... (Luego vienen tus LaunchedEffect, DisposableEffect y la UI con Column, etc.)




    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionsGranted = permissionsMap.values.all { it }
        if (permissionsGranted) {
            bluetoothEnabled = BluetoothHelper.isBluetoothEnabled(context)
            if (!bluetoothEnabled) {
                userRequestedBluetoothEnable = true // Prepara para pedir activación de BT si los permisos se otorgan
            }
        }
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        bluetoothEnabled = result.resultCode == Activity.RESULT_OK
        userRequestedBluetoothEnable = false // Resetea el flag
        if (!bluetoothEnabled) {
            Log.d("ConnectDeviceScreen", "El usuario no habilitó Bluetooth.")
            // Puedes añadir un mensaje para el usuario aquí si lo deseas
        }
    }

    // Efecto para solicitar la activación de Bluetooth si los permisos están y el usuario indicó que quiere activarlo.
    LaunchedEffect(permissionsGranted, userRequestedBluetoothEnable) {
        if (permissionsGranted && userRequestedBluetoothEnable && !bluetoothEnabled) {
            BluetoothHelper.requestEnableBluetoothWithLauncher(enableBluetoothLauncher)
        }
    }

    // Efecto inicial para comprobar soporte de Bluetooth.
    // Los estados de permissionsGranted y bluetoothEnabled ya se inicializan arriba.
    LaunchedEffect(Unit) {
        bluetoothSupported = BluetoothHelper.isBluetoothSupported(context)
    }

    // ... (todos los LaunchedEffect y DisposableEffect definidos arriba)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        // Quita verticalArrangement = Arrangement.Center si vas a tener una lista que ocupe espacio
    ) {
        Text(
            text = "Conectar Dispositivo Bluetooth",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostrar mensaje de estado del emparejamiento si existe
        bondStateMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Considera un botón para descartar el mensaje o que desaparezca después de un tiempo
        }

        if (!bluetoothSupported) {
            Text("Este dispositivo no soporta Bluetooth.")
        } else {
            if (!permissionsGranted) {
                Text("Se necesitan permisos de Bluetooth para continuar.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    permissionsLauncher.launch(BluetoothHelper.getRequiredBluetoothPermissions())
                }) {
                    Text("Solicitar Permisos")
                }
            } else if (!bluetoothEnabled) {
                Text("Bluetooth está desactivado.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    // Solicitar activación de Bluetooth
                    BluetoothHelper.requestEnableBluetoothWithLauncher(enableBluetoothLauncher)
                    // userRequestedBluetoothEnable se podría activar aquí también si quieres que el LaunchedEffect lo maneje
                }) {
                    Text("Activar Bluetooth")
                }
            } else {
                // Bluetooth y permisos están listos
                Row( // Manteniendo tu estructura Row
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            // No necesitas verificar permisos aquí directamente si el botón
                            // ya está habilitado/deshabilitado basado en permissionsGranted y bluetoothEnabled
                            // y si el DisposableEffect maneja el receiver basado en esos estados.

                            if (scannerStateValue.isDiscovering) {
                                Log.d("ConnectDeviceScreen", "Botón: Deteniendo búsqueda vía scanner.")
                                scanner.cancelDiscovery()
                            } else {
                                // Antes de iniciar, asegúrate de que el receiver esté listo
                                // (El DisposableEffect debería haberlo manejado si las condiciones son correctas)
                                Log.d("ConnectDeviceScreen", "Botón: Iniciando búsqueda vía scanner.")
                                // Opcionalmente, puedes limpiar la lista de dispositivos anteriores aquí
                                // si tu scanner.startDiscovery() no lo hace internamente.
                                // Ejemplo: scanner.clearDeviceList() o modificar el estado directamente
                                // _scannerState.update { it.copy(devices = emptyList(), error = null) }
                                // Esto depende de cómo quieras que se comporte la UI al iniciar una nueva búsqueda.

                                scanner.startDiscovery()
                            }
                        },
                        // Habilita el botón solo si los permisos están concedidos Y Bluetooth está activado.
                        // Esto previene intentos de escaneo cuando no es posible.
                        enabled = permissionsGranted && bluetoothEnabled
                    ) {
                        Text(if (scannerStateValue.isDiscovering) "Detener Búsqueda" else "Buscar Dispositivos")
                    }

                    if (scannerStateValue.isDiscovering) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dispositivos Encontrados:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (scannerStateValue.devices.isEmpty() && !scannerStateValue.isDiscovering) {
                    Text("No se encontraron dispositivos. Intenta buscar de nuevo.")
                } else if (scannerStateValue.devices.isEmpty() && scannerStateValue.isDiscovering) {
                    Text("Buscando dispositivos...")
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Para que ocupe el espacio restante en la Column
                ) {
                    items(scannerStateValue.devices, key = { it.address }) { device ->
                        DeviceItem(
                            discoveredDevice = device,
                            onClick = {
                                scanner.cancelDiscovery() // Detener escaneo antes de intentar emparejar
                                bondStateMessage = "Intentando emparejar con ${device.name ?: device.address}..."
                                bondingDeviceAddress = device.address // Establecer para el BroadcastReceiver
                                try {
                                    if (ActivityCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.BLUETOOTH_CONNECT
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        val bluetoothDevice = BluetoothHelper.getBluetoothAdapter(context)?.getRemoteDevice(device.address)
                                        if (bluetoothDevice != null) {
                                            val bondSucceeded = bluetoothDevice.createBond()
                                            if (!bondSucceeded) {
                                                bondStateMessage = "No se pudo iniciar el emparejamiento con ${device.name ?: device.address}."
                                                bondingDeviceAddress = null // Limpiar si el inicio falla inmediatamente
                                            }
                                        } else {
                                            bondStateMessage = "No se pudo obtener el dispositivo Bluetooth: ${device.address}"
                                            bondingDeviceAddress = null
                                        }
                                    } else {
                                        // Esto no debería ocurrir si la lógica de permisos es correcta
                                        bondStateMessage = "Permiso BLUETOOTH_CONNECT no concedido."
                                        bondingDeviceAddress = null
                                    }
                                } catch (e: SecurityException) {
                                    Log.e("ConnectDeviceScreen", "SecurityException al intentar emparejar: ${e.message}")
                                    bondStateMessage = "Error de seguridad al emparejar."
                                    bondingDeviceAddress = null
                                } catch (e: IllegalArgumentException) {
                                    Log.e("ConnectDeviceScreen", "IllegalArgumentException (MAC inválida?): ${e.message}")
                                    bondStateMessage = "Dirección MAC inválida para emparejar."
                                    bondingDeviceAddress = null
                                }
                            }
                        )
                        Divider()
                    }
                }

                // Muestra errores del scanner si los hay
                scannerStateValue.error?.let { error ->
                    Text(
                        text = "Error del escáner: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DeviceItem(
    discoveredDevice: com.example.ollaz3.bluetooth.DiscoveredBluetoothDevice, // Usa tu clase de datos
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = discoveredDevice.name ?: "Dispositivo Desconocido",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = discoveredDevice.address,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        // Podrías añadir un indicador si el dispositivo ya está emparejado
        // Text(if (discoveredDevice.isBonded) "Emparejado" else "No Emparejado")
    }
}



// Asegúrate de que no haya ninguna definición de DeviceItem o referencias a él si no lo estás usando.