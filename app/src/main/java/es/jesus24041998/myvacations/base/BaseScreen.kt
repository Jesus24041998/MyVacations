package es.jesus24041998.myvacations.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jesus24041998.myvacations.login.LoginScreenView
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme

@Composable
@Preview(showBackground = true)
private fun BaseScreenPreview() {
    MyVacationsTheme {
        LoadingScreenView(content = {
            LoginScreenView(
                onNavigateToHome = {},
            )
        }, isLoading = true)
    }
}

@Composable
fun BaseScreen(
    content: @Composable () -> Unit,
    isLoading: Boolean
) {
    LoadingScreenView(content, isLoading)
}

@Composable
private fun LoadingScreenView(content: @Composable () -> Unit, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (isLoading) 0.5f else 1f)
    ) {
        if (!isLoading) content()
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        }
    }
}