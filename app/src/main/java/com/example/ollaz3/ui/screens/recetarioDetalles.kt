package com.example.ollaz3.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import com.example.ollaz3.bluetooth.MonitorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollaz3.ui.theme.DarkColorScheme
import com.example.ollaz3.ui.theme.DarkColorScheme as Color




@SuppressLint("DefaultLocale")
@Composable
fun YellowButton2(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkColorScheme.tertiary
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = DarkColorScheme.primary
        )
    }
}

@Composable
fun receta(recipe: RecipeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.secondary, shape = RoundedCornerShape(8.dp)), // Alinea el Column a la izquierda
        horizontalAlignment = Alignment.Start, // Alinea el contenido a la izquierda

    ) {
        Spacer(modifier = Modifier.height(12.dp))
        for (ingredienteItem in recipe.ingredientes){
            Text(text = "  > "+ingredienteItem)
            Spacer(modifier = Modifier.height(12.dp))

        }
    }

}
@Composable
fun SelectedRecipeDetails(
    recipe: RecipeItem,
    timeRemainingSeconds: Int,
    isTimerRunning: Boolean,
    onTimerToggle: () -> Unit,
    onBackToList: () -> Unit,
    viewModel: MonitorViewModel // Recibe el ViewModel como parámetro
) {
    var cocinar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {



        if (cocinar) {
            SelectedRecipeCook(
                recipe = recipe,
                timeRemainingSeconds = timeRemainingSeconds,
                isTimerRunning = isTimerRunning,
                onTimerToggle = onTimerToggle,
                onBackToList = onBackToList,
                viewModel = viewModel,
                show = cocinar
            )
        }
        else{

            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Receta : " + recipe.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.tertiary
            )
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
//
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.tertiary
            )
            Spacer(modifier = Modifier.height(24.dp))
            receta(recipe)
            Spacer(modifier = Modifier.height(24.dp))

            YellowButton2(
                text = "Empezar a Cocinar",
                onClick = { cocinar = !cocinar }
            )

        }











    }

}