package com.example.ollaz3.ui.screens// Asegúrate de que el paquete sea el correcto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ollaz3.ui.theme.Ollaz3Theme

@Composable
fun MainScreen(
    viewModel: MonitorViewModel,
    onConnectClicked: () -> Unit,
    onRecipeBookClicked: () -> Unit,
    onTemperatureMonitorClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center // Centra el contenido del Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Ollaz",
                style = MaterialTheme.typography.displayLarge, // Un estilo grande para el título
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp)) // Espacio entre el título y el menú

            // Menú de opciones
            MenuButton(text = "Vincular Olla", onClick = onConnectClicked)
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(text = "Conectar Olla", onClick = onTemperatureMonitorClicked)
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(text = "Recetario", onClick = onRecipeBookClicked)

        }
    }
}

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.width(280.dp) // Ancho fijo para los botones del menú
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}



