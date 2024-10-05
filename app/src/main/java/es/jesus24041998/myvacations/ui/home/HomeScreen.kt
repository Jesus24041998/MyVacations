package es.jesus24041998.myvacations.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.mytravels.MyTravel
import es.jesus24041998.myvacations.ui.profile.ProfileScreen
import es.jesus24041998.myvacations.ui.search.SearchScreen
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme

enum class HomeDestination(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val titleTextId: Int,
) {
    MYTRAVELS(
        selectedIcon = R.drawable.ic_location_on,
        unselectedIcon = R.drawable.ic_location_off,
        titleTextId = R.string.navtravel
    ),
    SEARCH(
        selectedIcon = R.drawable.ic_search_on,
        unselectedIcon = R.drawable.ic_search_off,
        titleTextId = R.string.navsearch
    ),
    PROFILE(
        selectedIcon = R.drawable.ic_account_on,
        unselectedIcon = R.drawable.ic_account_off,
        titleTextId = R.string.navaccount
    )
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    MyVacationsTheme {
        HomeScreenView({},{},{},false)
    }
}

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToPolitics: () -> Unit,
    onNavigateToAddTravel: (travel: Travel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    fromSplash: Boolean
) {
    val loading by viewModel.isLoading.observeAsState(false)
    val loginExecuted = viewModel.loginExecuted
    LaunchedEffect(Unit) {
        if ((viewModel.getCurrentUser() == null || viewModel.getCurrentUser()?.isAnonymous == true || viewModel.getCurrentUser()?.isEmailVerified == false) && fromSplash && !loginExecuted.value) {
            loginExecuted.value = true
            onNavigateToLogin()
        }
    }
    HomeScreenView(onNavigateToLogin, onNavigateToPolitics, onNavigateToAddTravel,loading)
}

@Composable
private fun HomeScreenView(
    onNavigateToLogin: () -> Unit,
    onNavigateToPolitics: () -> Unit,
    onNavigateToAddTravel: (travel: Travel) -> Unit,
    loading: Boolean
) {
    BaseScreen(content = {
        var currentDestination by rememberSaveable { mutableStateOf(HomeDestination.MYTRAVELS) }
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                HomeDestination.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                painter = painterResource(id = if (it == currentDestination) it.selectedIcon else it.unselectedIcon),
                                contentDescription = "IconsNav"
                            )
                        },
                        label = { Text(stringResource(it.titleTextId)) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            when(currentDestination.name)
            {
                "MYTRAVELS" -> MyTravel(onNavigateToAddTravel)
                "SEARCH" -> SearchScreen()
                "PROFILE" -> ProfileScreen(onNavigateToLogin, onNavigateToPolitics)
            }
        }
    }, isLoading = loading)
}