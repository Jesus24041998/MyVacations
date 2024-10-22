package es.jesus24041998.myvacations.ui.mytravels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.utils.MyAlertDialog
import es.jesus24041998.myvacations.utils.MyListTravel
import es.jesus24041998.myvacations.utils.SnackBarViewError
import kotlinx.coroutines.launch

@Composable
@Preview(showBackground = true)
private fun MyTravelPreview() {
    MyVacationsTheme {
        MyTravelView()
    }
}

@Composable
fun MyTravel(
    viewModel: HomeViewModel,
    onNavigateToAddTravel: (travel: Travel) -> Unit,
) {
    val loading by viewModel.isLoading.observeAsState(false)
    val isInternetAvailable by viewModel.isConnection.observeAsState(true)
    val travels by viewModel.travels.collectAsState(initial = emptyList())
    MyTravelView(travels, onNavigateToAddTravel, loading, viewModel, isInternetAvailable)
}

@Composable
private fun MyTravelView(
    travels: List<Travel> = emptyList(),
    onNavigateToAddTravel: (travel: Travel) -> Unit = {},
    loading: Boolean = false,
    viewModel: HomeViewModel? = null,
    isInternetAvailable: Boolean = true
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val noInternetMessage = stringResource(id = R.string.no_internet)
    var dialogNoNeedBackup by remember { mutableStateOf(false) }
    //TODO AGREGAR PEQUEÑA GUIA CUANDO ESTE DISPONIBLE LA NUBE SELECTORA
    BaseScreen(content = {
        Scaffold(
            snackbarHost = {
                SnackBarViewError(snackbarHostState)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LaunchedEffect(isInternetAvailable) {
                    if (!isInternetAvailable) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = noInternetMessage,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
                if (dialogNoNeedBackup) {
                    MyAlertDialog(
                        false,
                        onConfirm = {
                            dialogNoNeedBackup = false
                        },
                        title = R.string.empty,
                        subtitle = R.string.titleNoDataBackup
                    ) {
                        dialogNoNeedBackup = false
                    }
                }

                MyListTravel(user = viewModel?.getCurrentUser(),
                    items = travels,
                    position = 0,
                    callback = { index ->
                        travels[index].let {
                            onNavigateToAddTravel(
                                Travel(
                                    it.online,
                                    it.id,
                                    it.name,
                                    it.description,
                                    it.activityList,
                                    it.initDate,
                                    it.endDate,
                                    it.extraList,
                                    it.total,
                                    it.coin
                                )
                            )
                        }
                    },
                    callbackBackUp = {
                        if (travels.any { !it.online }) {
                            viewModel?.syncWithDataBase()
                        } else {
                            dialogNoNeedBackup = true
                        }
                    },
                    callbackNew = {
                        onNavigateToAddTravel(
                            Travel(
                                false,
                                "",
                                "",
                                "",
                                listOf(),
                                "",
                                "",
                                listOf(),
                                0.0,
                                Coin("")
                            )
                        )

                    })
            }
        }
    }, isLoading = loading)
}