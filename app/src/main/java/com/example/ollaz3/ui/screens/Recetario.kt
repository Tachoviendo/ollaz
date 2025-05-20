package com.example.ollaz3.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.OptIn
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RecipeItem(
    val id: String,
    val name: String,
    val description: String = "Deliciosa receta tradicional.",
    val targetTemperature: Float = 180f, // Temperatura objetivo para la receta
    val defaultTimerMinutes: Int = 30
)
@JvmOverloads // Si planeas usar previews con parámetros por defecto para Modifier
@RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN]) // Si connectToDevice se llama desde aquí
@Composable
fun Recetario
    (
    navController: NavController, // Si lo necesitas
    viewModel: MonitorViewModel, // <<-- RECIBE LA INSTANCIA COMPARTIDA
    modifier: Modifier = Modifier // Mantén tu parámetro modifier
)
{





    val sampleRecipes = remember {
        listOf(
            RecipeItem(id = "1", name = "Guiso de Arroz", targetTemperature = 175f, defaultTimerMinutes = 45),
            RecipeItem(id = "2", name = "Olla Criolla", targetTemperature = 190f, defaultTimerMinutes = 60),
            RecipeItem(id = "3", name = "Estofado de Lentejas", targetTemperature = 160f, defaultTimerMinutes = 50)
        )
    }
    var selectedRecipe by remember { mutableStateOf<RecipeItem?>(null) }

    var timerRunning by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember { mutableIntStateOf(0) }

    // Estado para la temperatura del horno, podría venir de un ViewModel en una app real








                 LaunchedEffect(timerRunning, selectedRecipe) {
        if (timerRunning && selectedRecipe != null) {
            if (timeRemainingSeconds == 0 && selectedRecipe != null) { // Iniciar solo si no está ya corriendo o es una nueva selección
                timeRemainingSeconds = selectedRecipe!!.defaultTimerMinutes * 60
            }
            while (timeRemainingSeconds > 0 && timerRunning) {
                delay(1000)
                timeRemainingSeconds--
            }
            if (timeRemainingSeconds == 0) {
                timerRunning = false
            }
        } else if (!timerRunning && selectedRecipe != null && timeRemainingSeconds == 0) {
            // Si el timer se detuvo y finalizó, resetear al valor por defecto de la receta para la próxima vez
            timeRemainingSeconds = selectedRecipe!!.defaultTimerMinutes * 60
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recetario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (selectedRecipe == null) {
            Text(
                text = "Selecciona una receta:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start)
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleRecipes) { recipe ->
                    RecipeListItemCard(
                        recipe = recipe,
                        onClick = {
                            selectedRecipe = recipe
                            timeRemainingSeconds = recipe.defaultTimerMinutes * 60
                            timerRunning = false
                        }
                    )
                }
            }
        } else {




                SelectedRecipeDetails(
                    recipe = selectedRecipe!!,
                    timeRemainingSeconds = timeRemainingSeconds,
                    isTimerRunning = timerRunning,
                    onTimerToggle = {

                        if (timerRunning) { // Si está corriendo y se presiona, se detiene
                            timerRunning = false
                        } else {

                            // Si está detenido y se presiona, se inicia/continua
                            if (timeRemainingSeconds == 0) { // Si el tiempo llegó a cero, reinicia con el default
                                timeRemainingSeconds = selectedRecipe!!.defaultTimerMinutes * 60
                            }
                            timerRunning = true
                        }
                    },
                    onBackToList = {
                        selectedRecipe = null
                        timerRunning = false
                    },
                    viewModel = viewModel // Pasar el ViewModel existente
                ) // No es necesario pasar viewModel aquí si SelectedRecipeDetails lo crea
            }
        }
    }


@Composable
fun RecipeListItemCard(recipe: RecipeItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.name, style = MaterialTheme.typography.titleMedium)
            Text(text = recipe.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(UnstableApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun SelectedRecipeDetails(
    recipe: RecipeItem,
    timeRemainingSeconds: Int,
    isTimerRunning: Boolean,
    onTimerToggle: () -> Unit,
    onBackToList: () -> Unit,
    viewModel: MonitorViewModel // Recibe el ViewModel como parámetro
) {
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
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
                    data.drop(1)
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
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Conectar a la Olla")
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
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            ) {
                Text("Reintentar Conexión")
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
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
            Text("Volver al Recetario")
        }
    }
}


