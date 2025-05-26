package com.example.ollaz3.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavController
import com.example.ollaz3.bluetooth.MonitorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ollaz3.ui.theme.DarkColorScheme as Color
import com.example.ollaz3.ui.screens.SelectedRecipeDetails
import com.example.ollaz3.ui.screens.SelectedRecipeCook

data class RecipeItem(
    val id: String,
    val name: String,
    val description: String = "Deliciosa receta tradicional.",
    val ingredientes: List<String> = emptyList(), // Lista de ingredientes, por defecto vacía
    val preparacion: String,
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
            RecipeItem(
                id = "1",
                name = "Sopa de Pollo",
                description = "4 personas",
                ingredientes = listOf("1 Papa", "1 batata", "1 zanahoria", "apio", "1 trozo de repollo", "1 choclo", "1 ajo", "cebolla de verdeo", "puerro", "2,5 Litros de agua", "Condimentos al gusto: Sal, laurel, orégano"),
                preparacion = "Se cortan todas las verduras, se colocan" +
                        "dentro de la olla junto con los condimentos" +
                        "y el agua. Se lleva a hervor y luego se" +
                        "coloca dentro de la olla bruja por alrededor" +
                        "de 3 horas.",
                defaultTimerMinutes = 180),

        )
    }
    var selectedRecipe by remember { mutableStateOf<RecipeItem?>(null) }

    var timerRunning by remember { mutableStateOf(false) }
    var timeRemainingSeconds by remember { mutableIntStateOf(0) }

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
    Surface(modifier = Modifier.fillMaxSize(),
        color = Color.primary){
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (selectedRecipe == null) {

                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Recetario",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.tertiary
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.tertiary)
                Spacer(modifier = Modifier.height(24.dp))
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
                )
// pasar viewModel aquí si SelectedRecipeDetails lo crea
            }
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
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.secondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium

            )
            Text(text = recipe.description, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }
    }
}



