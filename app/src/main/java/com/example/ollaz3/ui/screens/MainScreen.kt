package com.example.ollaz3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ollaz3.ui.theme.DarkColorScheme as Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ollaz3.bluetooth.MonitorViewModel

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
                        Text(
                            text = "ZLink",
                            color = Color.tertiary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                YellowButton("Recetario", onClick = onRecipeBookClicked)
                Spacer(modifier = Modifier.height(24.dp))
                YellowButton("Ollas Brujas", onClick = onTemperatureMonitorClicked)
                Spacer(modifier = Modifier.height(24.dp))
                YellowButton("Sobre Nosotros", onClick = { /* TODO */ })

            }
        }
    }
}



