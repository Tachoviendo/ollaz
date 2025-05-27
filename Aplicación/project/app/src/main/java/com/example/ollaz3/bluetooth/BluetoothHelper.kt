package com.example.ollaz3.bluetooth // Asegúrate que el paquete sea este

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object BluetoothHelper {

    // Códigos de solicitud que tu Activity/Fragment usará para identificar la respuesta
    const val REQUEST_CODE_ENABLE_BT = 1001
    const val REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1002

    /**
     * Obtiene el BluetoothAdapter del sistema.
     *
     * @param context Contexto de la aplicación.
     * @return BluetoothAdapter o null si no está disponible.
     */
    fun getBluetoothAdapter(context: Context): BluetoothAdapter? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return bluetoothManager?.adapter
    }

    /**
     * Verifica si el dispositivo soporta Bluetooth.
     *
     * @param context Contexto de la aplicación.
     * @return true si Bluetooth es soportado, false en caso contrario.
     */
    fun isBluetoothSupported(context: Context): Boolean {
        return getBluetoothAdapter(context) != null
    }

    /**
     * Verifica si Bluetooth está actualmente habilitado en el dispositivo.
     *
     * @param context Contexto de la aplicación.
     * @return true si Bluetooth está habilitado, false en caso contrario.
     */
    fun isBluetoothEnabled(context: Context): Boolean {
        val adapter = getBluetoothAdapter(context)
        return adapter?.isEnabled ?: false
    }

    /**
     * Lanza un Intent para solicitar al usuario que habilite Bluetooth.
     * La Activity que llama debe manejar el resultado en onActivityResult o
     * mediante un ActivityResultLauncher.
     *
     * @param activity La Activity que inicia la solicitud.
     * @param requestCode El código que se devolverá en onActivityResult.
     *                    Ignorado si se usa ActivityResultLauncher.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestEnableBluetoothActivity(activity: Activity, requestCode: Int = REQUEST_CODE_ENABLE_BT) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        // Para API 31+ (Android 12+), se necesita BLUETOOTH_CONNECT para esta acción.
        // La UI que llame a esto debe asegurarse de tener el permiso o solicitarlo.
        activity.startActivityForResult(enableBtIntent, requestCode)
    }

    /**
     * Lanza un Intent para solicitar al usuario que habilite Bluetooth usando ActivityResultLauncher.
     *
     * @param launcher El ActivityResultLauncher configurado para manejar el Intent.
     */
    fun requestEnableBluetoothWithLauncher(launcher: ActivityResultLauncher<Intent>) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        // Para API 31+ (Android 12+), se necesita BLUETOOTH_CONNECT para esta acción.
        // La UI que llame a esto debe asegurarse de tener el permiso o solicitarlo.
        launcher.launch(enableBtIntent)
    }

    /**
     * Retorna la lista de permisos de Bluetooth requeridos según la versión de Android.
     *
     * @return Array de Strings con los nombres de los permisos.
     */
    fun getRequiredBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31) y superior
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
                // ACCESS_FINE_LOCATION no es estrictamente necesario para BLUETOOTH_SCAN
                // a menos que se use explícitamente `android:usesPermissionFlags="neverForLocation"`
                // en BLUETOOTH_SCAN y aún así se quieran obtener datos de ubicación.
                // No obstante, si tu app ya lo necesita por otras razones, está bien incluirlo en la solicitud general.
            )
        } else {
            // Versiones anteriores a Android 12 (API < 31)
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION // O ACCESS_COARSE_LOCATION. FINE es más común.
            )
        }
    }

    /**
     * Verifica si todos los permisos de Bluetooth requeridos han sido otorgados.
     *
     * @param context Contexto de la aplicación.
     * @return true si todos los permisos necesarios están otorgados, false en caso contrario.
     */
    fun hasAllBluetoothPermissions(context: Context): Boolean {
        return getRequiredBluetoothPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Solicita los permisos de Bluetooth necesarios a través de la Activity.
     * La Activity que llama debe manejar el resultado en onRequestPermissionsResult.
     *
     * @param activity La Activity que inicia la solicitud de permisos.
     * @param requestCode El código que se devolverá en onRequestPermissionsResult.
     */
    fun requestBluetoothPermissions(activity: Activity, requestCode: Int = REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
        ActivityCompat.requestPermissions(
            activity,
            getRequiredBluetoothPermissions(),
            requestCode
        )
    }
}