@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ZLink",
                        color = Color.Yellow,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: handle menu click */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.Yellow
                        )
                    }
                },
                backgroundColor = Color.Black,
                elevation = 0.dp
            )
        },
        backgroundColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            YellowButton("Sobre Nosotros") { /* TODO */ }
            Spacer(modifier = Modifier.height(32.dp))
            YellowButton("Recetario") { /* TODO */ }
            Spacer(modifier = Modifier.height(32.dp))
            YellowButton("Ollas Brujas") { /* TODO */ }
        }
    }
}

@Composable
fun YellowButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Yellow,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }
}
