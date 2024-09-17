package es.jesus24041998.myvacations.ui.mytravels

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
private fun MyTravelPreview() {
    MyVacationsTheme {
        MyTravelView(false)
    }
}

@Composable
fun MyTravel(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    MyTravelView(loading)
}

@Composable
private fun MyTravelView(loading: Boolean) {
    BaseScreen(content = {
        Text(text = "Soy My travel")
    }, isLoading = loading)
}