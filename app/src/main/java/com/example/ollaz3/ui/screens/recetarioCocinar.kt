package com.example.ollaz3.ui.screens

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.ollaz3.bluetooth.MonitorViewModel
import com.example.ollaz3.ui.theme.DarkColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)

@Composable
fun SelectedRecipeCook(
    recipe: RecipeItem,
    timeRemainingSeconds: Int,
    isTimerRunning: Boolean,
    onTimerToggle: () -> Unit,
    onBackToList: () -> Unit,
    viewModel: MonitorViewModel,
    show : Boolean// Recibe el ViewModel como parámetro
) {


    if (show) {

        val uiState by viewModel.arduinoUiState.collectAsStateWithLifecycle()
        val currentOvenTemperature: Float? = uiState.lastReceivedData?.trim()?.toFloatOrNull()
        val coroutineScope = rememberCoroutineScope()
        var bluetoothJob by remember { mutableStateOf<Job?>(null) }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
//        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
//        // --- SECCIÓN DE DEBUG EN UI ---
//        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
//            Column(modifier = Modifier.padding(8.dp)) {
//                // En tu sección de DEBUG INFO en SelectedRecipeDetails
//                val data = uiState.lastReceivedData
//                Text("lastReceivedData: '${data ?: "null"}'", fontSize = 10.sp)
//                Text("lastReceivedData.length: ${data?.length ?: "N/A"}", fontSize = 10.sp)
//                Text("lastReceivedData.chars: ${data?.map { it.code }?.joinToString() ?: "N/A"}", fontSize = 10.sp) // Muestra códigos ASCII/Unicode
//                Text("--- DEBUG INFO ---", fontWeight = FontWeight.Bold, fontSize = 12.sp)
//                Text("isConnected: ${uiState.isConnected}", fontSize = 10.sp)
//                //Text("isLoading: ${uiState.isLoading}", fontSize = 10.sp)
//                //Text("lastReceivedData: '${uiState.lastReceivedData ?: "null"}'", fontSize = 10.sp) // Muestra 'null' si es null
//                Text("currentOvenTemp (Float?): ${currentOvenTemperature?.toString() ?: "null"}", fontSize = 10.sp)
//                Text("Error Message: ${uiState.errorMessage ?: "N/A"}", fontSize = 10.sp)
//                Text("--- END DEBUG ---", fontWeight = FontWeight.Bold, fontSize = 12.sp)
//            }
//        }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))

            // --- FIN SECCIÓN DE DEBUG EN UI ---


            // SECCIÓN DE TEMPERATURA (similar a MonitorScreen)
            Text("Estado de la Olla:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (uiState.isConnected) "Conectado" else "Desconectado",
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Mostrar último dato crudo (opcional, para depuración)
            // Text(
            //     text = "Dato crudo: ${uiState.lastReceivedData ?: "N/A"}",
            //     style = MaterialTheme.typography.bodySmall,
            //     modifier = Modifier.padding(bottom = 8.dp)
            // )


            if (uiState.isConnected) {

                Text("Temperatura Actual:", style = MaterialTheme.typography.titleMedium)
                if (uiState.isConnected) {
                    val data = uiState.lastReceivedData?.trim()
                    val proceso : String? = (if (data != null && data.length>=2){
                        //data.drotimeRemap(1)
                        data.dropLast(1)
                    } else {
                        data
                    }).toString()
                    Text(
                        text = "${data}", // Muestra el dato tal cual lo recibes
                        style = MaterialTheme.typography.bodyLarge, // Ajusta el estilo como necesites
                        color = MaterialTheme.colorScheme.secondary, // Un color diferente para distinguirlo
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                // Se muestra si no está conectado
                Text(
                    "Conecta la olla para ver la temperatura.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }




            if (!uiState.isConnected && uiState.errorMessage == null) {
                Button(
                    onClick = {
                        bluetoothJob?.cancel()
                        bluetoothJob = coroutineScope.launch(Dispatchers.IO) {
                            viewModel.connectToDevice()
                        }

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkColorScheme.tertiary),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(text = "Conectar a la Olla")
                }
            }

            uiState.errorMessage?.let { error ->
                Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = {
                        bluetoothJob?.cancel()
                        bluetoothJob = coroutineScope.launch(Dispatchers.IO) {
                            viewModel.connectToDevice() // Reintentar conexión
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkColorScheme.tertiary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text("Reintentar Conexión")
                }
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Temporizador:", style = MaterialTheme.typography.titleMedium)
            val minutes = timeRemainingSeconds / 60
            val seconds = timeRemainingSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 50.sp), // Tamaño más pequeño
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    // Solo llama a onTimerToggle, no intentes conectar aquí
                    // La conexión debe estar establecida para que el timer tenga sentido (o manejarlo en onTimerToggle)
                    if (uiState.isConnected) {
                        onTimerToggle()
                    } else {
                        // Opcional: Mostrar un mensaje/Toast indicando que se debe conectar primero
                        // Por ejemplo, usando un Snackbar:
                        // scope.launch { snackbarHostState.showSnackbar("Conecta la olla para usar el timer") }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp),
                enabled = uiState.isConnected // Habilita el botón del timer solo si está conectado
            ) {
                val timerButtonText = when {
                    isTimerRunning -> "Detener Timer"
                    timeRemainingSeconds > 0 && timeRemainingSeconds < recipe.defaultTimerMinutes * 60 -> "Continuar Timer"
                    else -> "Iniciar Timer"
                }
                Text(timerButtonText)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = {
                // Considera si quieres desconectar al volver a la lista
                // if (uiState.isConnected) {
                //     viewModel.disconnectDevice()
                // }
                onBackToList()
            }) {
                Text(text= "Volver al Recetario",
                    color= DarkColorScheme.tertiary)
            }
        }


    }

}