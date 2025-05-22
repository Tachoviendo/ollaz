@Composable
fun AboutUsScreen(onHomeClick: () -> Unit) {
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
                    IconButton(onClick = { /* TODO: handle menu */ }) {
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
        backgroundColor = Color.Black,
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color.Black,
                elevation = 0.dp
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onHomeClick) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.Yellow
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NOSOTROS",
                color = Color.Yellow,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = """
                Somos un equipo que pertenece a la asignatura “TIT” de la carrera de analista en informática dictada en el campus Salto de UCU.
                El equipo deberá realizar dos proyectos a lo largo de todo el semestre.
                El objetivo es poder culminar ambos y colmar ampliamente las expectativas de nuestros docentes.
                """.trimIndent(),
                color = Color.Yellow,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
