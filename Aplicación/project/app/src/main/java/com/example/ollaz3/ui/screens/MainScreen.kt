package com.example.ollaz3.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollaz3.R
import com.example.ollaz3.bluetooth.MonitorViewModel
import kotlinx.coroutines.launch
import com.example.ollaz3.ui.theme.DarkColorScheme as Color

@Composable
fun YellowButton(text: String, onClick: () -> Unit) {
    Button(
        onClick =  onClick ,
        modifier = Modifier
            .width(220.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.tertiary
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MonitorViewModel,
    onConnectClicked: () -> Unit,
    onTemperatureMonitorClicked: () -> Unit,
    onRecipeBookClicked: () -> Unit,
    onOllasClicked: () -> Unit,
    onZlinkClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.primary,
                drawerContentColor = Color.tertiary
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Menú",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                NavigationDrawerItem(
                    label = { Text("Conectar olla") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onConnectClicked()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Monitorear temperatura") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onTemperatureMonitorClicked()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {

                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.tertiary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.primary
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.tertiary)
                )
            },
            containerColor = Color.primary
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // Reemplaza con tu ID de drawable
                    contentDescription = stringResource(id = R.string.descripcion_de_mi_imagen), // Descripción para accesibilidad
                    modifier = Modifier.size(300.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                YellowButton("Recetario", onClick = onRecipeBookClicked)
                Spacer(modifier = Modifier.height(24.dp))
                YellowButton("Ollas Brujas", onClick = onOllasClicked)
                Spacer(modifier = Modifier.height(24.dp))
                YellowButton("Sobre Nosotros", onClick =  onZlinkClicked)

            }
        }
    }
}



