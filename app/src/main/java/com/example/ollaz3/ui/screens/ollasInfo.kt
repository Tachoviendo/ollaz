package com.example.ollaz3.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ollaz3.R
import com.example.ollaz3.ui.theme.DarkColorScheme

@Composable
fun ollasInfo(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkColorScheme.primary
    ){
        Column(modifier = Modifier.padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text= "Ollas Brujas",
                color = DarkColorScheme.tertiary)
              Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Una olla bruja o térmica es un recipiente aislado que se utiliza para completar la cocción de los alimentos después de que han sido calentados y llevados a ebullición en una cocina convencional. \n" +
                    "\n" +
                    "Este método aprovecha el calor retenido en los alimentos para terminar su cocción, economizando energía. \n" +
                    "\n" +
                    "El proceso consiste en calentar inicialmente la comida en una olla normal y luego transferirla a la olla bruja, donde el aislamiento térmico mantiene el calor y finaliza la cocción sin necesidad de más energía externa. ")
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = DarkColorScheme.tertiary)
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ollabruja), // Reemplaza con tu ID de drawable
                contentDescription = stringResource(id = R.string.descripcion_de_mi_imagen), // Descripción para accesibilidad
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(10.dp)),
                alignment = Alignment.Center
            )



        }


    }
}
