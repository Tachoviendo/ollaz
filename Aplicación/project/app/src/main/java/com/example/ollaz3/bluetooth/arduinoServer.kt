package com.example.ollaz3.bluetooth // Asegúrate que el package sea correcto

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.UUID

// Estado simplificado
data class SimpleArduinoState(
    val lastReceivedData: String? = null,
    var isConnected: Boolean = false,
    val errorMessage: String? = null
)

class SimpleArduinoServer(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var dataListeningJob: Job? = null

    private val _state = MutableStateFlow(SimpleArduinoState())
    val state: StateFlow<SimpleArduinoState> = _state.asStateFlow()

    companion object {
        private const val TAG = "SimpleArduinoServer"
    }

    @SuppressLint("MissingPermission") // Asumimos que los permisos se manejan externamente
    suspend fun connect(deviceAddress: String) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _state.update { it.copy(errorMessage = "Bluetooth no disponible o desactivado.") }
            return
        }

        disconnect() // Asegura limpieza previa

        val device: BluetoothDevice? = try {
            bluetoothAdapter.getRemoteDevice(deviceAddress)
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Dirección MAC inválida o dispositivo no encontrado.") }
            return
        }

        Log.d(TAG, "Intentando conectar a: ${device?.name ?: deviceAddress}")

        withContext(Dispatchers.IO) {
            try {
                bluetoothSocket = device?.createRfcommSocketToServiceRecord(SPP_UUID)
                bluetoothSocket?.connect()
                inputStream = bluetoothSocket?.inputStream

                _state.update { it.copy(isConnected = true, errorMessage = null) }
                Log.i(TAG, "Conectado a ${device?.name ?: deviceAddress}")
                startListening()

            } catch (e: IOException) {
                Log.e(TAG, "Error de conexión: ${e.message}")
                _state.update { it.copy(isConnected = false, errorMessage = "Error de conexión: ${e.message?.take(50)}") }
                disconnect()
            }
        }
    }

    private fun startListening() {


        dataListeningJob?.cancel()
        dataListeningJob = coroutineScope.launch(Dispatchers.IO) {
            Log.i(TAG, "Iniciando escucha de datos...")
            val readBuffer = ByteArray(1024)
            val accumulatedData = StringBuilder()
            val delimiter = '\n'

            while (isActive && bluetoothSocket?.isConnected == true) {
                try {
                    val numBytesRead = inputStream?.read(readBuffer) ?: -1

                    if (numBytesRead > 0) {
                        val receivedChunk = String(readBuffer, 0, numBytesRead, Charsets.UTF_8)
                        accumulatedData.append(receivedChunk)

                        var delimiterIndex: Int
                        while (accumulatedData.indexOf(delimiter).also { delimiterIndex = it } != -1) {
                            val completeMessage = accumulatedData.substring(0, delimiterIndex).trim()
                            accumulatedData.delete(0, delimiterIndex + 1)

                            if (completeMessage.isNotEmpty()) {
                                Log.d(TAG, "Dato recibido: '$completeMessage'")
                                _state.update { it.copy(lastReceivedData = completeMessage) }
                            }
                        }
                    } else if (numBytesRead == -1) {
                        Log.i(TAG, "Stream cerrado por el dispositivo remoto.")
                        if (isActive) _state.update { it.copy(isConnected = false, errorMessage = "Desconectado por dispositivo.") }
                        break
                    }
                } catch (e: IOException) {
                    if (isActive) {
                        Log.e(TAG, "Error de lectura: ${e.message}")
                        _state.update { it.copy(isConnected = false, errorMessage = "Error de lectura.") }
                    }
                    break
                }
            }
            Log.i(TAG, "Escucha de datos terminada.")
            if (isActive && _state.value.isConnected) { // Si el bucle terminó y aún se creía conectado
                _state.update { it.copy(isConnected = false, errorMessage = _state.value.errorMessage ?: "Conexión cerrada.") }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        Log.d(TAG, "Desconectando...")
        try {
            dataListeningJob?.cancel()
            inputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.w(TAG, "Excepción al cerrar recursos: ${e.message}")
        } finally {
            dataListeningJob = null
            inputStream = null
            bluetoothSocket = null
            // Solo actualiza si no hay un error ya presente o si estaba conectado
            if (_state.value.isConnected || _state.value.errorMessage == null) {
                _state.update { it.copy(isConnected = false, lastReceivedData = null, errorMessage = if(it.isConnected) "Desconectado" else it.errorMessage) }
            }
        }
    }
}