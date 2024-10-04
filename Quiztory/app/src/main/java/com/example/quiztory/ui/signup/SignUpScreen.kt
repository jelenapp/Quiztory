package com.example.quiztory.ui.signup

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.quiztory.R
import com.example.quiztory.Screen
import com.example.quiztory.ui.theme.ThemeViewModel
import com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreen(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: SignUpViewModel = SignUpViewModel.getInstance(context),
    navController: NavController,
    goToSignIn: () -> Unit = {
        navController.popBackStack(Screen.SignIn.name,inclusive = true)
        navController.navigate(Screen.SignIn.name)
    }
) {
    var uri by remember { mutableStateOf<Uri?>(viewModel
        .profilna) }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )
    val username = viewModel.username.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()

    val namesurname = viewModel.namesurname.collectAsState()
    val phonenumber = viewModel.phonenumber.collectAsState()
    val email = viewModel.email.collectAsState()


    Scaffold() { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {

            Image(
                painter = painterResource(id = if(ThemeViewModel.getInstance().isDarkTheme.value) R.mipmap.quiztorylogo else R.mipmap.quiztorylogo),
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
                value = username.value,
                onValueChange = { viewModel.updateUsername(it) },
                placeholder = { Text("Korisnicko ime") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Korisnicko ime"
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

            /**
             * Potvrdi sifru
             */
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
                value = confirmPassword.value,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                placeholder = { Text("Potvrdite sifru") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Potvrdite sifru"
                    )
                },
                visualTransformation = PasswordVisualTransformation()
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
                value = namesurname.value,
                onValueChange = { viewModel.updateNameSurname(it) },
                placeholder = { Text("Ime i prezime") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Ime i prezime"
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
                value = phonenumber.value,
                onValueChange = { viewModel.updatePhonenumber(it) },
                placeholder = { Text("Broj telefona") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Broj telefona"
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
//
            Column {

                AsyncImage (
                    model = if (uri == null) { R.mipmap.quiztorylogo }  else { uri },
                    contentDescription = null,
                    modifier = Modifier
                        .size(248.dp)
                        .border(width = 3.dp, color = Color.Gray),
                    )

                Button(
                    onClick = {
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                {
                    Text("Odaberite fotografiju")
                }

            }
            Button(onClick = {
                uri?.let {
                    viewModel.uploadProfileImage(uri!!, viewModel.uid!!) { imageUrl ->
                        navController.navigate(Screen.SignIn.name)
                    }
                }
            }) {
                Text("Save")
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            Button(
                onClick = {viewModel.onSignUpClick(goToSignIn) },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
            ) {
                Text(
                    text = "Registrujte se",
                    fontSize = 16.sp,
                    modifier = modifier.padding(0.dp, 6.dp)
                )
            }
        }
    }
}