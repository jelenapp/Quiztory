package com.example.quiztory.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.quiztory.R
import com.example.quiztory.Screen
import com.example.quiztory.ui.theme.ThemeViewModel


class NavigationBar(
    private val navigateToHomePage: () -> Unit,
    private val navigateToFilterPage: () -> Unit,
    private val navigateToEventRemindersPage: () -> Unit,
    private val navigateToFriendsPage: () -> Unit,
    private val navigateToProfilePage: () -> Unit
) {
    @Composable
    fun Draw(currentScreen: Screen?, tvm: ThemeViewModel = ThemeViewModel.getInstance()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 45.dp)
                .drawBehind {
                    val borderSize = 2.dp.toPx()
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, borderSize / 2),
                        end = Offset(size.width, borderSize / 2),
                        strokeWidth = borderSize
                    )

                }

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TransparentIconButton(
                    onClick = navigateToHomePage,
                    icon = Icons.Default.Home,
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToFilterPage,
                    icon = Icons.Default.Search,
                    modifier = Modifier.weight(2f)
                )
                TransparentIconButton(
                    onClick = navigateToFriendsPage,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(2f)
                )
                    TransparentIconButton(
                        onClick = navigateToEventRemindersPage,
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(2f)
                    )

                TransparentIconButton(
                    onClick = navigateToProfilePage,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}


@Composable
fun TransparentIconButton(onClick: () -> Unit, icon: ImageVector, modifier: Modifier = Modifier, tvm : ThemeViewModel = ThemeViewModel.getInstance()) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp), // Remove elevation
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(40.dp) // Default size is 24.dp, increased by 50%
        )
    }
}