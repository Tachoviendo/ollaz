@Composable
fun ConexionScreen() {
    val backgroundColor = Color(0xFF121212) // fondo oscuro
    val buttonColor = Color(0xFFFFEB3B) // amarillo brillante
    val textColor = Color.Black

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = backgroundColor,
                elevation = 0.dp,
                title = {
                    Text(
                        "ZLink",
                        color = buttonColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = buttonColor)
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp)) // margen derecho
                }
            )
        },
        bottomBar = {
            BottomNavigation(backgroundColor = backgroundColor) {
                BottomNavigationItem(
                    selected = true,
                    onClick = { /* TODO */ },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = buttonColor)
                    }
                )
            }
        },
        backgroundColor = backgroundColor,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* TODO: Reintentar conexión */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                    modifier = Modifier.padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevation(8.dp)
                ) {
                    Text("Reintentar\nConexión", color = textColor, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { /* TODO: Iniciar timer */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevation(8.dp)
                ) {
                    Text("Iniciar Timer", color = textColor)
                }
            }
        }
    )
}
