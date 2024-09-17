package es.jesus24041998.myvacations.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme

@Composable
@Preview(showBackground = true)
private fun SearchPreview() {
    MyVacationsTheme {
        SearchView(false)
    }
}

@Composable
fun SearchScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    SearchView(loading)
}

@Composable
private fun SearchView(loading: Boolean) {
    BaseScreen(content = {
        Text(text = "Soy search")
    }, isLoading = loading)
}