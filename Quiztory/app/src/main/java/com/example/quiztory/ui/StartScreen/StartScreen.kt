package com.example.quiztory.ui.StartScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiztory.R
import com.example.quiztory.Screen
import com.example.quiztory.ui.theme.ThemeViewModel
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: StartScreenViewModel = StartScreenViewModel.getInstance(),
    navController: NavController,
    tvm : ThemeViewModel = ThemeViewModel.getInstance()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiztory",
            fontSize = 80.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Image(
            painter = painterResource(id = if(tvm.isDarkTheme.value) R.mipmap.quiztorylogo else R.mipmap.quiztorylogo),
            contentDescription = "Auth image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(30.dp))
        )
    }
    LaunchedEffect(true) {
        delay(300L)
        viewModel.onAppStart(navController)
    }
}