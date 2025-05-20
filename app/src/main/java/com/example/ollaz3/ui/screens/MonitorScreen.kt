package com.example.ollaz3.ui.screens

import android.Manifest
import android.app.Application
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Importante para observar el StateFlow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
// Asumo que tienes tu SimpleArduinoServer y SimpleArduinoState en el package com.example.ollaz3.bluetooth

@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN]) // Requerido para conectar y, a veces, para obtener nombres de dispositivos
@Composable
fun MonitorScreen(
    navController: NavController, // Si lo necesitas
    viewModel: MonitorViewModel, // <<-- RECIBE LA INSTANCIA COMPARTIDA
    modifier: Modifier = Modifier // Mantén tu parámetro modifier
){


    val uiState by viewModel.arduinoUiState.collectAsStateWithLifecycle()

    // Scope para coroutines
    val coroutineScope = rememberCoroutineScope()
    // Job para manejar las operaciones de conexión/desconexión
    var bluetoothJob by remember { mutableStateOf<Job?>(null) }

    // Limpiar el Job cuando el Composable se destruye


    // Variable para la temperatura, intentando convertir el string recibido
    val ovenTemperature: Float? = uiState.lastReceivedData?.toFloatOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Temperatura de la OLLA",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Conectado: ${uiState.isConnected}")
        Spacer(modifier = Modifier.height(8.dp))

        Text("Último Dato Recibido:")
        Text(
            text = uiState.lastReceivedData ?: "---",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar temperatura si se pudo convertir
        ovenTemperature?.let { temp ->
            Text("Temperatura (si es un número):")
            Text(
                text = "${String.format("%.1f", temp)} °C",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))


        uiState.errorMessage?.let { error ->
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    bluetoothJob?.cancel() // Cancela cualquier operación previa
                    bluetoothJob = coroutineScope.launch(Dispatchers.IO) { // Ejecuta en un hilo de background
                        viewModel.connectToDevice()
                    }
                },
                enabled = !uiState.isConnected
            ) {
                Text("Conectar")
            }
            Button(
                onClick = {
                    bluetoothJob?.cancel() // Cancela cualquier operación previa
                    bluetoothJob = coroutineScope.launch(Dispatchers.IO) { // Ejecuta en un hilo de background
                        viewModel.disconnectDevice()
                    }
                },
                enabled = uiState.isConnected
            ) {
                Text("Desconectar")
            }
        }
    }
}