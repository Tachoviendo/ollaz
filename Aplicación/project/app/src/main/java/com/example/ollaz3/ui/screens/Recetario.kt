package com.example.ollaz3.ui.screens

import android.Manifest
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ollaz3.bluetooth.MonitorViewModel
import kotlinx.coroutines.delay
import com.example.ollaz3.ui.theme.DarkColorScheme as Color

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
                name = "Sopa",
                description = "4 personas",
                ingredientes = listOf("1 Papa", "1 batata", "1 zanahoria", "apio", "1 trozo de repollo", "1 choclo", "1 ajo", "cebolla de verdeo", "puerro", "2,5 Litros de agua", "Condimentos al gusto: Sal, laurel, orégano"),
                preparacion = "Se cortan todas las verduras, se colocan" +
                        "dentro de la olla junto con los condimentos" +
                        "y el agua. Se lleva a hervor y luego se" +
                        "coloca dentro de la olla bruja por alrededor" +
                        "de 3 horas.",
                defaultTimerMinutes = 1),

            RecipeItem(
                id = "2",
                name = "Arroz con Verduras",
                description = "4 personas",
                ingredientes = listOf("1 cebolla", "½ ají", "1 ajo", "1 papa", "1 trozo de calabaza", "1 batata", "1 choclo", "1 taza de arroz", "1½ litro de agua", "Condimentos a gusto: Sal, ají molido, pimentón"),
                preparacion = "Se rehoga la cebolla con el ají y el ajo durante algunos minutos. Se agrega el arroz para nacarar durante 2 minutos. Se agrega 1,5 litros de agua junto con el resto de las verduras y condimentos. Se lleva a hervor y luego se coloca dentro de la olla bruja por alrededor de 45 minutos.",
                defaultTimerMinutes = 45
            ),
            RecipeItem(
                id = "3",
                name = "Guisado de Carne",
                description = "4 personas",
                ingredientes = listOf("1 cebolla", "1 zanahoria", "1 trozo de calabaza", "1 papa", "2 zapallitos", "400 g de carne (Roast beef)", "1 taza de agua", "Condimentos a gusto: sal, pimienta, orégano, ají molido"),
                preparacion = "Se cortan las verduras y la carne en trozos pequeños. Se coloca la cacerola en el fuego, se le agrega un poco de aceite. Se rehogan la cebolla y la zanahoria junto con los condimentos. Se agrega la carne y el resto de las verduras. Se deja unos minutos para que tome sabor y luego se incorporan 200 ml de agua. Se lleva a hervor y luego se coloca dentro de la olla bruja por 1 hora.",
                defaultTimerMinutes = 60
            ),
            RecipeItem(
                id = "4",
                name = "Guiso de Lentejas",
                description = "4 personas",
                ingredientes = listOf("1 zanahoria", "1 cebolla", "1 ajo", "½ ají", "1 trozo de calabaza", "1 taza de lentejas (remojadas)", "5 cucharadas de aceite", "1½ litros de agua", "Opcional: panceta y/o chorizo colorado", "Condimentos a gusto: sal, laurel, ají molido, pimienta"),
                preparacion = "Se ponen en remojo las lentejas desde el día anterior. Se fríen en aceite la cebolla, el ají, la zanahoria y el ajo durante algunos minutos. Se agregan las lentejas escurridas, la calabaza, los condimentos y el agua. Se lleva a hervor y luego se coloca dentro de la olla bruja por 3 horas.",
                defaultTimerMinutes = 180
            ),
            RecipeItem(
                id = "5",
                name = "Puchero",
                description = "4 personas",
                ingredientes = listOf("1 papa", "1 trozo de calabaza", "1 batata", "1 zanahoria", "1 cebolla", "1 trozo de brócoli", "1 puñado de arvejas", "½ kilo de falda o caracú", "2 litros de agua", "Opcional: puerro, cebolla de verdeo, choclo", "Condimentos a gusto: Sal, laurel"),
                preparacion = "Se pone la carne, las verduras y el agua en la olla. Se lleva a hervor y luego se coloca dentro de la olla bruja por 3 horas.",
                defaultTimerMinutes = 180
            ),
            RecipeItem(
                id = "6",
                name = "Cocido de Arroz y Porotos Mungo",
                description = "4 personas",
                ingredientes = listOf("1 cebolla", "1 ajo", "1 ají morrón", "1 zanahoria", "1 trozo de calabaza", "1 batata", "1 trozo de repollo colorado", "2 tomates", "½ taza de porotos mungo (remojados)", "1 taza de arroz blanco", "1½ litro de agua", "Condimentos a gusto: sal, pimienta, orégano, ají molido, laurel", "Opcional: salchichas cocidas en trozos"),
                preparacion = "Se coloca la cacerola en el fuego con un poco de aceite. Se rehogan la cebolla, el ají, el ajo y la zanahoria. Se incorpora el tomate. Se agregan los porotos y un litro de agua, cocinando por 15 minutos. Luego se agregan la calabaza, la batata, el arroz, el repollo y ½ litro más de agua. Se lleva a hervor y se coloca dentro de la olla bruja por 2 horas.",
                defaultTimerMinutes = 120
            ),
            RecipeItem(
                id = "7",
                name = "Arroz con Leche",
                description = "4 personas",
                ingredientes = listOf("1 taza de arroz (200 g)", "120 g de azúcar", "1 litro de leche", "1 pizca de sal", "Opcional: cascarita de limón o naranja, ramita de canela"),
                preparacion = "Se pone la leche y el arroz en la cacerola. A los 10 minutos se le agrega el azúcar. Se lleva a hervor y luego se coloca dentro de la olla bruja por 2 horas.",
                defaultTimerMinutes = 120
            ),
            RecipeItem(
                id = "8",
                name = "Mermelada de Naranjas",
                description = "Rinde 2 kg de mermelada",
                ingredientes = listOf("4 a 6 naranjas agrias", "1 manzana roja", "1 kg de azúcar", "150 ml de agua"),
                preparacion = "Se lavan y cortan las naranjas (quitando las semillas) y la manzana. Se procesa todo con Minipimer o similar. Se coloca en una olla con el azúcar y el agua. Se lleva a hervor por 5 minutos y luego se coloca dentro de la olla bruja por aproximadamente 12 horas.",
                defaultTimerMinutes = 720
            )










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



