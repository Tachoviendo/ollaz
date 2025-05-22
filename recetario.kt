@Composable
fun BaseScreen(
    onHomeClick: () -> Unit,
    content: @Composable () -> Unit
) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content()
        }
    }
}
