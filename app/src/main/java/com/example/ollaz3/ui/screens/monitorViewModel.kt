package com.example.ollaz3.ui.screens // O donde prefieras

import android.app.Application
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import com.example.ollaz3.bluetooth.SimpleArduinoServer // Asegúrate de importar tu clase
import com.example.ollaz3.bluetooth.SimpleArduinoState // Y el estado
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MonitorViewModel(application: Application) : ViewModel() {
    private val arduinoServer = SimpleArduinoServer(application.applicationContext, viewModelScope)
    val arduinoUiState: StateFlow<SimpleArduinoState> = arduinoServer.state

    // Dirección MAC de tu HC-05
    private val deviceAddress = "00:24:04:00:0B:8B" // <<--- CAMBIA ESTO POR TU MAC REAL

    fun connectToDevice() {
        viewModelScope.launch {
            arduinoServer.connect(deviceAddress)
        }
    }

    fun disconnectDevice() {
        arduinoServer.disconnect()
    }

    // Es buena práctica limpiar los recursos cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        arduinoServer.disconnect()
    }
}

// Factoría para pasar Application al ViewModel
class MonitorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MonitorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MonitorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}