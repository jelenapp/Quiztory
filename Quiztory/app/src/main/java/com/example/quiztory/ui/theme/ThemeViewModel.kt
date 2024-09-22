package com.example.quiztory.ui.theme


import androidx.compose.runtime.mutableStateOf
import com.example.quiztory.ui.QuiztoryAppViewModel

class ThemeViewModel private constructor() : QuiztoryAppViewModel() {

    var isDarkTheme = mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }

    companion object {
        private var INSTANCE: ThemeViewModel? = null

        fun getInstance(): ThemeViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeViewModel().also {
                    INSTANCE = it
                }
            }
        }
    }

}