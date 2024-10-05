package es.jesus24041998.myvacations.ui.mytravels

import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.datastore.Activity
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Extra
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.utils.MyListTravel
import es.jesus24041998.myvacations.utils.NetworkUtilities
import es.jesus24041998.myvacations.utils.SnackBarViewError
import es.jesus24041998.myvacations.utils.dateTimeToMillis
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
@Preview(showBackground = true)
private fun MyTravelPreview() {
    MyVacationsTheme {
        MyTravelView({}, false, hiltViewModel())
    }
}

@Composable
fun MyTravel(
    onNavigateToAddTravel: (travel: Travel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    MyTravelView(onNavigateToAddTravel,loading, viewModel)
}

@Composable
private fun MyTravelView(
    onNavigateToAddTravel: (travel: Travel) -> Unit,
    loading: Boolean,
    viewModel: HomeViewModel
) {
    val travels by viewModel.travels.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isInternetNotAvailable = snapshotFlow { !NetworkUtilities.isInternetAvailable(context) }.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.getTravelsLocal()
    }
    val activity = listOf(
        Activity(
            name = "Visita cazorla",
            description = "Visitamos Cazorla",
            initDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            1.0
        ),
        Activity(
            name = "Ruta cerrada de Utrero",
            description = "La ruta se encuentra en Cazorla",
            initDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ),
        Activity(
            name = "Ruta rio Barosa",
            description = "La ruta se encuentra al lado del rio Barosa",
            initDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
    )
    val extra = listOf(
        Extra(description = "Cerveza", priceOrNot = 3.0),
        Extra(description = "Cafe", priceOrNot = 1.0)
    )
    // Crear un nuevo viaje
    val newTravel = Travel(
        id = "1", //(viewModel.getCurrentUser()?.uid + "_" + name + travels.size + 1),
        name = "Cazorla",
        description = "Un viaje a Cazorla , veremos sus calles y monumentos , tambien haremos un poco de ruta por su montaña",
        activityList = activity,
        initDate = "26/09/2024",
        endDate = "26/09/2024",
        extraList = extra,
        total = 200.0,
        coin = Coin("USD")
    )

    BaseScreen(content = {
        Scaffold(
            snackbarHost = { SnackBarViewError(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if(isInternetNotAvailable.value) {
                    val nointernet = stringResource(id = R.string.no_internet)
                    LaunchedEffect(key1 = snackbarHostState) {
                        snackbarHostState.showSnackbar(
                            message = nointernet,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }

                MyListTravel(user = viewModel.getCurrentUser(),
                    items = travels,
                    position = 0,
                    callback = { index ->
                        travels[index].let {
                            onNavigateToAddTravel(Travel(it.id, it.name, it.description, it.activityList, it.initDate, it.endDate, it.extraList, it.total, it.coin))
                        }
                    },
                    callbackBackUp = { if (viewModel.getCurrentUser()?.isAnonymous == false) viewModel.syncWithDataBase() },
                    callbackNew = {
                        onNavigateToAddTravel(Travel("", "", "", listOf(), "", "", listOf(), 0.0, Coin("")))

                    })
            }
        }
    }, isLoading = loading)


//TODO Aplicar el tipo de moneda para cada viaje
    /*
      var expanded by remember { mutableStateOf(false) }
      Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.accountcoin)+" ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                    )
                    coin?.let {
                        ExposedDropdownMenuBox(modifier = Modifier.weight(1f),
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                readOnly = true,
                                value = Currency.getInstance(coin.currencyCode).getSymbol(
                                    Locale.US),
                                onValueChange = { },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable,true)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                monedasMasFamosas.forEach { selectionOption ->
                                    Currency.getInstance(selectionOption).let {
                                        DropdownMenuItem(text = {
                                            Text(text = it.currencyCode +" | "+it.getSymbol(Locale.US))
                                        }, onClick = {
                                            viewModel.updateCoin(Coin(selectionOption))
                                            expanded = false
                                        })
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
     */

}