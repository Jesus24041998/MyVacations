package es.jesus24041998.myvacations.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme

@Composable
@Preview(showBackground = true)
private fun SearchPreview() {
    MyVacationsTheme {
        SearchView()
    }
}

@Composable
@Preview(showBackground = true)
private fun NoConectionPreview() {
    MyVacationsTheme {
        NoConection()
    }
}

@Composable
fun SearchScreen(
    viewModel: HomeViewModel
) {
    val loading by viewModel.isLoading.observeAsState(false)
    val isInternetAvailable by viewModel.isConnection.observeAsState(true)
    if (isInternetAvailable) SearchView(viewModel,loading) else NoConection()
}

@Composable
private fun SearchView(viewModel: HomeViewModel? = null,loading: Boolean = false) {
    BaseScreen(content = {
        Text(text = "Soy search")
    }, isLoading = loading)
}

@Composable
private fun NoConection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_nointernet),
                contentDescription = "No Connection",
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No hay conexión a Internet",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Por favor,revisa tu conexión e inténtalo de nuevo.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
    }
}
