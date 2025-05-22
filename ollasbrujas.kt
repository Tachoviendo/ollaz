@Composable
fun OllaBrujaScreen() {
    val backgroundColor = Color(0xFF121212) // fondo oscuro
    val textColor = Color(0xFFFFEB3B) // amarillo brillante

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = backgroundColor,
                elevation = 0.dp,
                title = {
                    Text("ZLink", color = textColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = textColor)
                    }
                },
                actions = {
                    // Espacio para íconos del sistema si quieres agregarlos aquí
                    Spacer(modifier = Modifier.width(48.dp)) // simula margen derecho
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = backgroundColor,
                elevation = 8.dp
            ) {
                BottomNavigationItem(
                    selected = true,
                    onClick = { /* TODO */ },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = textColor)
                    }
                )
            }
        },
        backgroundColor = backgroundColor,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "OLLAS BRUJAS",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Las ollas térmicas, también conocidas como ollas brujas, son utensilios de cocina que conservan la temperatura de los alimentos por largo tiempo sin necesidad de calor adicional. Gracias a su diseño aislante, mantienen tanto el calor como el frío, lo que las hace prácticas y eficientes, ideales para platos como guisos o sopas. Además, ayudan a ahorrar energía al reducir la necesidad de recurrir a fuentes de calor adicionales.",
                    color = textColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    )
}
