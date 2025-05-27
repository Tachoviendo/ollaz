package com.example.ollaz3.ui.screens

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ollaz3.bluetooth.MonitorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.ollaz3.ui.theme.DarkColorScheme as Color

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

    Surface(modifier= Modifier.fillMaxSize(),
        color = Color.primary){
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Temperatura de la OLLA",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.tertiary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.tertiary)
            Spacer(modifier = Modifier.height(24.dp))

            Text(text="Conectado: ${uiState.isConnected}",
                color = Color.tertiary)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text="Último Dato Recibido:",
                color = Color.tertiary)
            Text(
                text = uiState.lastReceivedData ?: "---",
                style = MaterialTheme.typography.titleLarge,
                color = Color.tertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar temperatura si se pudo convertir
            ovenTemperature?.let { temp ->
                Text(text  ="Temperatura (si es un número):",
                    color = Color.tertiary)
                Text(
                    text = "${String.format("%.1f", temp)} °C",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.tertiary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))


            uiState.errorMessage?.let { error ->
                Text(
                    text = "Error: $error",
                    color = Color.tertiary
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
                    colors = ButtonDefaults.buttonColors(containerColor= Color.tertiary),
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
                    enabled = uiState.isConnected,
                    colors = ButtonDefaults.buttonColors(containerColor= Color.tertiary)
                ) {
                    Text(text="Desconectar",
                        color = Color.secondary)
                }
            }
        }
    }
    }



