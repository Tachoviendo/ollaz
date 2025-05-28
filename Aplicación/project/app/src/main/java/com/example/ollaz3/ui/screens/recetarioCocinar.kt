package com.example.ollaz3.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ollaz3.bluetooth.MonitorViewModel
import com.example.ollaz3.ui.theme.DarkColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val CHANNEL_ID = "timer_channel"
const val NOTIFICATION_ID = 1
const val TEMP_NOTIFICATION_ID = 2 // ID diferente para la notificación de temperatura

var primeraVez= true
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Timer Notifications"
        val descriptionText = "Notifications for timer completion"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

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


    val context = LocalContext.current
    var notificationSent by remember { mutableStateOf(false) }
    var tempNotificationSent by remember { mutableStateOf(false) } // Para la notificación de temperatura

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }


    LaunchedEffect(timeRemainingSeconds, isTimerRunning) {
        if (timeRemainingSeconds == 0 && !isTimerRunning && !notificationSent) {
            sendTimerFinishedNotification(context, recipe.name)
            notificationSent = true
        } else if (timeRemainingSeconds > 0 || isTimerRunning) {
            notificationSent = false
        }
    }

    val uiState by viewModel.arduinoUiState.collectAsStateWithLifecycle() // Moved here to be accessible
    val data = uiState.lastReceivedData?.trim()




    val tempFloat = data?.dropLast(2)?.toFloatOrNull()


    Text(text="Temperatura: $tempFloat")


    val temperatureLimit: Float = 23.0f

    LaunchedEffect(data, uiState.isConnected, recipe.name) {
        if (uiState.isConnected && data!= null) {

            val tempFloat = if (data.length >= 2) {
                data.dropLast(2).toFloatOrNull()
            } else {
                null
            }


            if (tempFloat != null) {
                if (tempFloat > temperatureLimit && !tempNotificationSent) {
                    sendTemperatureHighNotification(context, recipe.name, tempFloat)
                    tempNotificationSent = true
                } else if (tempFloat <= temperatureLimit && tempNotificationSent) {
                    // Si la temperatura sube por encima del umbral y ya se envió una notificación, resetear.
                    tempNotificationSent = false
                }
            }
        } else if (!uiState.isConnected && tempNotificationSent) {
            tempNotificationSent = false
        }

    }

    if (show) {

        // Solicitar permiso de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        // Permiso concedido
                    } else {
                        // Permiso denegado, podrías mostrar un mensaje al usuario
                    }
                }
            )
            LaunchedEffect(Unit) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        //val currentOvenTemperature: Float? = uiState.lastReceivedData?.trim()?.toFloatOrNull() // Movido arriba
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
//
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))



            // SECCIÓN DE TEMPERATURA (similar a MonitorScreen)
            Text("Estado de la Olla:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (uiState.isConnected) "Conectado" else "Desconectado",
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )



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
            var timerButtontext = ""

            if (primeraVez){
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 50.sp), // Tamaño más pequeño
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            }else{
                timerButtontext = "Reiniciar"
                Text(
                    text = "Cocción termianda",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp), // Tamaño más pequeño
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            }





            @Composable
            fun boton(text: String){
                Button(
                    onClick = {
                        if (uiState.isConnected) {

                                onTimerToggle()
                                if (!primeraVez){
                                    primeraVez = true
                                }

                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isConnected) DarkColorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.12f) // Color deshabilitado
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp),
                    enabled = uiState.isConnected // Habilita el botón del timer solo si está conectado
                ) {
                    Text(
                        text = text,
                        color = DarkColorScheme.tertiary
                    )
                }
            }




            if (isTimerRunning){


                timerButtontext = "Detener Timer"
                boton(timerButtontext)
            }
            else if (timeRemainingSeconds> 0 && timeRemainingSeconds < recipe.defaultTimerMinutes * 60){

                timerButtontext = "Continuar Timer"
                boton(timerButtontext)
            }
            else if (timeRemainingSeconds == 0){
                if (primeraVez){
                    primeraVez = false
                }
                timerButtontext = "Reiniciar"
                boton(timerButtontext)
            }
            else{
                if (!primeraVez){
                    timerButtontext = "Reiniciar"
                }
                else{
                    timerButtontext = "Iniciar Timer"
                }

                boton(timerButtontext)

            }






            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = {
                onBackToList()
            }) {
                Text(text= "Volver al Recetario",
                    color= DarkColorScheme.tertiary)
            }
        }


    }

}

@SuppressLint("MissingPermission") // Ya se solicita el permiso en el Composable
fun sendTimerFinishedNotification(context: Context, recipeName: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Considera usar un ícono de tu app
        .setContentTitle("¡Tiempo Terminado!")
        .setContentText("La cocción de $recipeName ha finalizado.")
        .setPriority(NotificationCompat.PRIORITY_HIGH) // O DEFAULT, según la importancia
    notificationManager.notify(NOTIFICATION_ID, builder.build())
}

// Definición de la función para enviar la notificación de temperatura alta
@SuppressLint("MissingPermission") // Asegúrate de que el permiso POST_NOTIFICATIONS se maneja
fun sendTemperatureHighNotification(context: Context, recipeName: String, temperature: Float) { // Nombre de función cambiado
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val temperatureNotificationLimit = 31.0f; // Límite específico para la notificación
    val builder = NotificationCompat.Builder(context, CHANNEL_ID) // Reutiliza el mismo CHANNEL_ID o crea uno específico
        .setSmallIcon(android.R.drawable.ic_dialog_alert) // Ícono diferente para distinguir
        .setContentTitle("¡Temperatura Alta!") // Título cambiado
        .setContentText("La temperatura para '$recipeName' ha superado los ${temperatureNotificationLimit}°C, es $temperature°C.") // Texto cambiado, usa temperatureNotificationLimit
        .setPriority(NotificationCompat.PRIORITY_HIGH) // Puede ser menos urgente que el fin del timer
    notificationManager.notify(TEMP_NOTIFICATION_ID, builder.build()) // Usa un ID diferente
}