package com.example.ollaz3.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ollaz3.R
import com.example.ollaz3.ui.theme.DarkColorScheme

// Función de utilidad para abrir URLs
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Manejar excepción, por ejemplo, si no hay navegador disponible
        // Podrías mostrar un Toast o un mensaje en la UI
        e.printStackTrace()
    }
}

@Composable
fun SocialMediaButton(
    text: String,
    socialMediaUrl: String,
    icon: ImageVector? = null, // Para íconos de Material Icons
    drawableResId: Int? = null, // Para íconos de tus drawables
    contentDescription: String
) {
    val context = LocalContext.current

    Button(
        onClick = { openUrl(context, socialMediaUrl) },
        // Puedes personalizar los colores y la forma del botón aquí
         colors = ButtonDefaults.buttonColors(containerColor = DarkColorScheme.tertiary)
    ) {
        // Mostrar ícono si se proporciona
        when {
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // La descripción del botón ya es suficiente
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
            drawableResId != null -> {
                Image(
                    painter = painterResource(id = drawableResId),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
        }
        Text(text)
    }
}

@Composable
fun zlinkInfo(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkColorScheme.primary,


    ){
        Column(modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally){
            Spacer(modifier = Modifier.height(48.dp))
            Image(
                painter = painterResource(id = R.drawable.integrantes), // Reemplaza con tu ID de drawable
                contentDescription = stringResource(id = R.string.descripcion_de_mi_imagen), // Descripción para accesibilidad
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ZLINK",
                style = MaterialTheme.typography.headlineMedium,
                color = DarkColorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Lucca Di Raimundo, Valentin Echeverría, Ignacio Silva y Renzo Berreta componen el equipo zLink.",
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            SocialMediaButton(
                text="Seguinos en Instagram",
                socialMediaUrl = "https://www.instagram.com/zlink.ucu/",
                drawableResId = R.drawable.instagramicon,
                contentDescription = "Instagram Icon"
                )







        }

    }
}
