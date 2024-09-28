package es.jesus24041998.myvacations.ui.mytravels

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.datastore.Activity
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Extra
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.utils.MyListTravel
import es.jesus24041998.myvacations.utils.dateTimeToMillis
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
@Preview(showBackground = true)
private fun MyTravelPreview() {
    MyVacationsTheme {
        MyTravelView(false, hiltViewModel())
    }
}

@Composable
fun MyTravel(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    MyTravelView(loading, viewModel)
}

@Composable
private fun MyTravelView(loading: Boolean, viewModel: HomeViewModel) {
    val travels by viewModel.travels.collectAsState(initial = emptyList())
    val activity = listOf(
        Activity(
            name = "Visita cazorla",
            description = "Visitamos Cazorla",
            initDate = dateTimeToMillis(21, 9, 2024),
            1.0
        ),
        Activity(
            name = "Ruta cerrada de Utrero",
            description = "La ruta se encuentra en Cazorla",
            initDate = dateTimeToMillis(21, 9, 2024)
        ),
        Activity(
            name = "Ruta rio Barosa",
            description = "La ruta se encuentra al lado del rio Barosa",
            initDate = dateTimeToMillis(22, 9, 2024)
        )
    )
    val extra = listOf(
        Extra(description = "Cerveza", priceOrNot = 3.0),
        Extra(description = "Cafe", priceOrNot = 1.0)
    )
    // Crear un nuevo viaje
    val newTravel = Travel(
        id = "1",
        name = "Cazorla",
        description = "Un viaje a Cazorla , veremos sus calles y monumentos , tambien haremos un poco de ruta por su montaña",
        activityList = activity,
        initDate = 1000L,
        endDate = 2000L,
        extraList = extra,
        total = 200.0,
        coin = Coin("USD")
    )

    LaunchedEffect(Unit) {
        viewModel.getTravels()
    }

    BaseScreen(content = {
        Column {
            MyListTravel(items = travels, position = 0, callback = { index ->
                travels[index].let {
                    Log.d("pruebas2", "Id -> " + it.id)
                    Log.d("pruebas2", "Name -> " + it.name)
                    Log.d("pruebas2", "Description -> " + it.description)
                    Log.d("pruebas2", "InitDate -> " + it.initDate.toString())
                    Log.d("pruebas2", "EndDate -> " + it.endDate.toString())
                    Log.d("pruebas2", "ActivityList -> " + it.activityList.size.toString())
                    Log.d("pruebas2", "ExtraList -> " + it.extraList.size.toString())
                }
            },
                callbackNew = {
                    Log.d("pruebas2", "Agrego nuevo viaje")
                    viewModel.saveTravel(newTravel)
                })
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