package com.example.quiztory.ui.StartScreen


import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import com.example.quiztory.services.implementations.AccountService
import com.example.quiztory.ui.QuiztoryAppViewModel

class StartScreenViewModel (
    private val accountService: AccountService,
) : QuiztoryAppViewModel(){

    companion object{

        private var INSTANCE: StartScreenViewModel? = null

        fun getInstance(): StartScreenViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StartScreenViewModel(accountService = AccountService()).also { INSTANCE = it }
            }
        }
    }

    fun onAppStart(navController: NavController) {
        if (accountService.hasUser() && accountService.isVerified()){
            navController.popBackStack(com.example.quiztory.Screen.Home.name, inclusive = true)
            navController.navigate(com.example.quiztory.Screen.Home.name)
        }
        else {
            navController.popBackStack(com.example.quiztory.Screen.Start.name, inclusive = true)
            navController.navigate(com.example.quiztory.Screen.SignIn.name)
        }
    }
}