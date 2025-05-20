package com.example.ollaz3

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController // Importado
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// ASUME QUE ESTOS SON TUS COMPOSABLES DE PANTALLA
// Si los nombres son diferentes, ajústalos.
import com.example.ollaz3.ui.screens.ConnectDeviceScreen
import com.example.ollaz3.ui.screens.MainScreen
import com.example.ollaz3.ui.screens.MonitorScreen // Cambiaremos cómo se llama
import com.example.ollaz3.ui.screens.MonitorViewModel
import com.example.ollaz3.ui.screens.MonitorViewModelFactory // Ya lo tienes
import com.example.ollaz3.ui.screens.Recetario // Renombrado para claridad, o usa tu nombre
// import com.example.ollaz3.ui.screens.MonitorViewModelFactory // No es necesario si se pasa el ViewModel
// import com.example.ollaz3.viewmodels.MonitorViewModel // Mueve la clase ViewModel a su propio archivo
// import com.example.ollaz3.ui.screens.MonitorViewModelFactory // Ya lo tienes

import com.example.ollaz3.ui.theme.Ollaz3Theme

// Define tus rutas de navegación (esto está bien)
object AppDestinations {
    const val MAIN_SCREEN = "main_screen"
    const val CONNECT_DEVICE_SCREEN = "connect_device_screen" // Asumimos que esta pantalla podría necesitar el ViewModel también
    const val RECIPE_BOOK_SCREEN = "recetario"
    const val TEMP_MONITOR_SCREEN = "temp_monitor_screen"
}

class MainActivity : ComponentActivity() {

    // Instancia única del ViewModel, obtenida a nivel de Activity
    private val monitorViewModel: MonitorViewModel by viewModels {
        MonitorViewModelFactory(application)
    }

    private val requestBluetoothPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                android.util.Log.i("PERMISSIONS", "${it.key} = ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothPermissions.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
        // ... (lógica de permisos para versiones anteriores)

        setContent {
            Ollaz3Theme {
                // Ya NO llamas a MainScreen aquí directamente si forma parte de la navegación
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pasa la instancia única del monitorViewModel a AppNavigation
                    AppNavigation(navController = rememberNavController(), viewModel = monitorViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MonitorViewModel) { // Recibe NavController y ViewModel
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MAIN_SCREEN
    ) {
        composable(AppDestinations.MAIN_SCREEN) {
            MainScreen(
                viewModel = viewModel, // <<-- Pasa el ViewModel a MainScreen
                onConnectClicked = {
                    // Si ConnectDeviceScreen también necesita el ViewModel, se lo pasas en su composable
                    navController.navigate(AppDestinations.CONNECT_DEVICE_SCREEN)
                },
                onRecipeBookClicked = {
                    navController.navigate(AppDestinations.RECIPE_BOOK_SCREEN)
                },
                onTemperatureMonitorClicked = {
                    navController.navigate(AppDestinations.TEMP_MONITOR_SCREEN)
                }
            )
        }
        composable(AppDestinations.CONNECT_DEVICE_SCREEN) {
            // Asume que ConnectDeviceScreen está definido para aceptar el ViewModel
            ConnectDeviceScreen(
                navController = navController, // Si necesita navegar de vuelta o a otro lado
                monitorViewModel = viewModel   // <<-- Pasa el ViewModel
            )
        }

        //nada al idle le pinto que este todo el rojo pero está bien jiji


        composable(AppDestinations.RECIPE_BOOK_SCREEN) @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT]) {
            Recetario( // Asegúrate de usar el nombre correcto de tu Composable (Recetario o RecetarioScreen)
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize() // <<-- AÑADE EL MODIFIER
            )
        }
        composable(AppDestinations.TEMP_MONITOR_SCREEN) @androidx.annotation.RequiresPermission(
            allOf = [android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT]
        ) {
            MonitorScreen(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize() // <<-- AÑADE EL MODIFIER
            )
            // Ya NO se crea un ViewModel aquí dentro. Se usa la instancia compartida.
        }
    }
}