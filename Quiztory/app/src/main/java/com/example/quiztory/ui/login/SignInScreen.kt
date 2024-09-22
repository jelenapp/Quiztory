package com.example.quiztory.ui.login

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiztory.R
import com.example.quiztory.Screen

import com.example.quiztory.ui.login.SignInViewModel
import com.example.quiztory.ui.theme.ThemeViewModel


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignInScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SignInViewModel = SignInViewModel.getInstance(context)
) {
    val username = viewModel.username.collectAsState()
    val password = viewModel.password.collectAsState()
    val email = viewModel.email.collectAsState()


    Scaffold(
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = if (ThemeViewModel.getInstance().isDarkTheme.value) R.mipmap.quiztorylogo else R.mipmap.quiztorylogo),
                contentDescription = "Auth image",
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            )


            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            OutlinedTextField(
                singleLine = true,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
                    .border(
                        BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(50)
                    ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                value = email.value,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = { Text("E-mail") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "E-mail"
                    )
                }
            )


            OutlinedTextField(
                singleLine = true,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
                    .border(
                        BorderStroke(width = 2.dp, color = Color.Black),
                        shape = RoundedCornerShape(50)
                    ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                value = password.value,
                onValueChange = { viewModel.updatePassword(it) },
                placeholder = { Text("Sifra") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Sifra"
                    )
                },
                visualTransformation = PasswordVisualTransformation()
            )


            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            Button(
                onClick = {
                    viewModel.onSignInClick {
                        navController.popBackStack(Screen.Start.name, inclusive = true)
                        navController.navigate(Screen.Map.name)
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
            ) {
                Text(
                    text = "Ulogujte se",
                    fontSize = 16.sp,
                    modifier = modifier.padding(0.dp, 6.dp)
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

            TextButton(onClick = {
                viewModel.onSignUpClick {
                    navController.popBackStack(Screen.SignIn.name, inclusive = false)
                    navController.navigate(Screen.SignUp.name)
                }
            }) {
                Text(text = "Napravite nalog", fontSize = 16.sp)
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )

//            TextButton(onClick = {
//                viewModel.onSignUpClick {
//               //     navController.popBackStack(Screen.SignIn.name, inclusive = false)
//                 //   navController.navigate(Screen.Filter.name)
//                }
//            }) {
//         //       Text(text = stringResource(R.string.continue_as_guest), fontSize = 16.sp)
//            }

        }
    }
}


