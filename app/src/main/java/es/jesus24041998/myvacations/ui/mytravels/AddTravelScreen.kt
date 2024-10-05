package es.jesus24041998.myvacations.ui.mytravels

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.ui.datastore.Activity
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Extra
import es.jesus24041998.myvacations.ui.datastore.Travel
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.utils.MyDatePickerDialog
import es.jesus24041998.myvacations.utils.getSymbol
import es.jesus24041998.myvacations.utils.monedasMasFamosas
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
private fun AddTravelPreview() {
    MyVacationsTheme {
        AddTravelView()
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddTravelView(
    travel: Travel = Travel("", "", "", listOf(), "", "", listOf(), 0.0, Coin("")),
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController? = rememberNavController()
) {
    Scaffold(
        topBar = {
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    navController?.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Botonback",
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Text(
                    text = if(travel.id.isEmpty()) stringResource(id = R.string.addtraveltitle) else stringResource(id = R.string.edittraveltitle),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        content = { paddingValues ->
            FormularyModeView(
                travel,
                viewModel = viewModel,
                paddingValues = paddingValues,
                navController = navController
            )
        })
}


@ExperimentalMaterial3Api
@Composable
fun FormularyModeView(
    travel: Travel,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    navController: NavHostController?
) {
    val datePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val allowedPattern = Regex("^[a-zA-Z0-9 áéíóúÁÉÍÓÚñÑ]*$")

    val tripName = remember { mutableStateOf(travel.name) }
    val tripDescription = remember { mutableStateOf(travel.description) }
    val price = remember { mutableDoubleStateOf(travel.total) }

    val activityName = remember { mutableStateOf("") }
    val activityDescription = remember { mutableStateOf("") }
    val activityStartDate = remember { mutableStateOf(LocalDate.now()) }
    val activityPrice = remember { mutableStateOf("") }

    val extraDescription = remember { mutableStateOf("") }
    val extraPrice = remember { mutableStateOf("") }

    val startDate = remember {
        mutableStateOf(
            if (travel.initDate == "") LocalDate.now() else LocalDate.parse(
                travel.initDate,
                datePattern
            )
        )
    }
    val endDate = remember {
        mutableStateOf(
            if (travel.endDate == "") LocalDate.now() else LocalDate.parse(
                travel.endDate,
                datePattern
            )
        )
    }
    val currency = remember {
        mutableStateOf(
            if (travel.coin.currencyCode == "") Coin(
                Currency.getInstance(Locale.getDefault()).currencyCode
            ) else travel.coin
        )
    }
    val activities = remember { mutableStateOf(travel.activityList) }
    val extras = remember { mutableStateOf(travel.extraList) }
    val activitiesDialog = remember { mutableStateOf(false) }
    val extrasDialog = remember { mutableStateOf(false) }
    val modeActivity = remember { mutableStateOf(listOf<Activity>()) }
    val modeExtra = remember { mutableStateOf(listOf<Extra>()) }
    val error = remember { mutableStateOf(false) }

    val errorMessageNameActivity = remember { mutableStateOf("") }
    val errorMessageDescriptionActivity = remember { mutableStateOf("") }
    val errorMessageDatesActivity = remember { mutableStateOf("") }
    val errorMessagePricesActivity = remember { mutableStateOf("") }

    val errorMessageDescriptionExtra = remember { mutableStateOf("") }
    val errorMessagePricesExtra = remember { mutableStateOf("") }

    when {
        activitiesDialog.value -> ActivityFormulary(
            paddingValues,
            activitiesDialog,
            modeActivity,
            activities,
            activityName,
            activityDescription,
            activityStartDate,
            activityPrice,
            allowedPattern,
            error,
            errorMessageNameActivity,
            errorMessageDescriptionActivity,
            errorMessageDatesActivity,
            errorMessagePricesActivity,
            startDate,
            endDate,
            datePattern,
            currency
        )

        extrasDialog.value -> ExtrasFormulary(
            paddingValues,
            extrasDialog,
            modeExtra,
            extras,
            extraDescription,
            extraPrice,
            errorMessageDescriptionExtra,
            errorMessagePricesExtra,
            allowedPattern,
            error,
            currency
        )

        else -> TravelFormulary(
            travel,
            navController,
            viewModel,
            paddingValues,
            tripName,
            tripDescription,
            price,
            allowedPattern,
            currency,
            error,
            datePattern,
            startDate,
            endDate,
            activities,
            activitiesDialog,
            modeActivity,
            errorMessageNameActivity,
            errorMessageDescriptionActivity,
            errorMessageDatesActivity,
            errorMessagePricesActivity,
            activityName,
            activityDescription,
            activityStartDate,
            activityPrice,
            extras,
            extrasDialog,
            extraDescription,
            extraPrice,
            modeExtra,
            errorMessageDescriptionExtra,
            errorMessagePricesExtra
        )
    }


}

@ExperimentalMaterial3Api
@Composable
private fun TravelFormulary(
    travel: Travel,
    navController: NavHostController?,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    tripName: MutableState<String>,
    tripDescription: MutableState<String>,
    price: MutableDoubleState,
    allowedPattern: Regex,
    currency: MutableState<Coin>,
    error: MutableState<Boolean>,
    datePattern: DateTimeFormatter,
    startDate: MutableState<LocalDate>,
    endDate: MutableState<LocalDate>,
    activities: MutableState<List<Activity>>,
    activitiesDialog: MutableState<Boolean>,
    modeActivity: MutableState<List<Activity>>,
    errorMessageNameActivity: MutableState<String>,
    errorMessageDescriptionActivity: MutableState<String>,
    errorMessageDatesActivity: MutableState<String>,
    errorMessagePriceActivity: MutableState<String>,
    activityName: MutableState<String>,
    activityDescription: MutableState<String>,
    activityStartDate: MutableState<LocalDate>,
    activityPrice: MutableState<String>,
    extras: MutableState<List<Extra>>,
    extrasDialog: MutableState<Boolean>,
    extrasDescription: MutableState<String>,
    extrasPrice: MutableState<String>,
    modeExtras: MutableState<List<Extra>>,
    errorMessageDescriptionExtra: MutableState<String>,
    errorMessagePricesExtra: MutableState<String>,
) {
    val errorMessageName = remember { mutableStateOf("") }
    val errorMessageDescription = remember { mutableStateOf("") }
    val errorMessageDates = remember { mutableStateOf("") }
    val errorMessagePrice = remember { mutableStateOf("") }
    val showStartDateDialogTravel = remember { mutableStateOf(false) }
    val showEndDateDialogTravel = remember { mutableStateOf(false) }
    val priceTotalActivities = remember { mutableDoubleStateOf(0.0) }
    val priceTotalExtras = remember { mutableDoubleStateOf(0.0) }
    val priceTravelTotal = remember { mutableDoubleStateOf(0.0) }

    val showCurrencySpinner = remember { mutableStateOf(false) }

    val errorEmpty = remember {
        mutableStateOf("")
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    )
    {
        item {
            val error1 = stringResource(id = R.string.errormessagelns)
            val error2 = stringResource(id = R.string.errormessage20c)
            OutlinedTextField(
                value = tripName.value,
                onValueChange = {
                    when {
                        !allowedPattern.matches(it) -> {
                            error.value = true
                            errorMessageName.value =
                                error1
                        }

                        it.length > 20 -> {
                            error.value = true
                            errorMessageName.value =
                                error2
                        }

                        else -> {
                            errorMessageName.value = ""
                        }
                    }
                    tripName.value = it
                },
                singleLine = true,
                label = { Text(stringResource(id = R.string.travelname)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            if (errorMessageName.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageName.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errormessagelns)
            val error2 = stringResource(id = R.string.errormessage200c)
            OutlinedTextField(
                value = tripDescription.value,
                onValueChange = {
                    when {
                        !allowedPattern.matches(it) -> {
                            error.value = true
                            errorMessageDescription.value = error1
                        }

                        it.length > 120 -> {
                            error.value = true
                            errorMessageDescription.value = error2
                        }

                        else -> {
                            errorMessageDescription.value = ""
                        }
                    }
                    tripDescription.value = it
                },
                singleLine = false,
                label = { Text(stringResource(id = R.string.nametravel)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            if (errorMessageDescription.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageDescription.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Card(
                modifier = Modifier.clickable { showStartDateDialogTravel.value = true },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(style = MaterialTheme.typography.titleMedium, text = stringResource(id = R.string.travelstartdate))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = startDate.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.clickable { showEndDateDialogTravel.value = true },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(style = MaterialTheme.typography.titleMedium, text = stringResource(id = R.string.travelenddate))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = endDate.value.format(datePattern),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            val error1 = stringResource(id = R.string.errorfipff)
            val error2 = stringResource(id = R.string.errorfiapffa)
            when {
                startDate.value.dayOfYear > endDate.value.dayOfYear && startDate.value.year == endDate.value.year -> {
                    errorMessageDates.value =
                        error1
                    error.value = true
                }

                startDate.value.year > endDate.value.year -> {
                    errorMessageDates.value =
                        error2
                    error.value = true
                }

                else -> errorMessageDates.value = ""
            }

            if (errorMessageDates.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageDates.value
                )
            }
            MyDatePickerDialog(
                showDialog = showStartDateDialogTravel.value,
                onDismissRequest = { showStartDateDialogTravel.value = false },
                onDateSelected = { startDate.value = it }
            )
            MyDatePickerDialog(
                showDialog = showEndDateDialogTravel.value,
                onDismissRequest = { showEndDateDialogTravel.value = false },
                onDateSelected = { endDate.value = it }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(style = MaterialTheme.typography.titleLarge, text = stringResource(id = R.string.travelactivities))
                Spacer(modifier = Modifier.width(8.dp))
                Button(modifier = Modifier.clip(CircleShape), onClick = {
                    errorMessageNameActivity.value = ""
                    errorMessageDescriptionActivity.value = ""
                    errorMessageDatesActivity.value = ""
                    errorMessagePriceActivity.value = ""
                    activityName.value = ""
                    activityDescription.value = ""
                    activityStartDate.value = LocalDate.now()
                    activityPrice.value = ""

                    modeActivity.value = listOf()
                    activitiesDialog.value = true }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(text = stringResource(id = R.string.add))
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(imageVector = Icons.Default.Add, contentDescription = "addActivity")
                    }
                }
            }
        }
        priceTotalActivities.doubleValue = 0.0
        activities.value.forEach { priceTotalActivities.doubleValue += it.priceOrNot ?: 0.0 }
        items(activities.value) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    errorMessageNameActivity.value = ""
                    errorMessageDescriptionActivity.value = ""
                    errorMessageDatesActivity.value = ""
                    errorMessagePriceActivity.value = ""
                    activityName.value = item.name
                    activityDescription.value = item.description
                    activityStartDate.value = LocalDate.parse(item.initDate, datePattern)
                    activityPrice.value = item.priceOrNot.toString()

                    modeActivity.value = listOf(item)
                    activitiesDialog.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "editActivity"
                    )
                }
                IconButton(onClick = {
                    try {
                        activities.value = activities.value.filter { it != item }
                    } catch (e: Exception) {
                        Log.d("pruebas", e.message.toString())
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "deleteActivity"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = (stringResource(id = R.string.travelpriceactivities)) + priceTotalActivities.doubleValue + getSymbol(currency.value.currencyCode))
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(style = MaterialTheme.typography.titleLarge, text = stringResource(id = R.string.travelextra))
                Spacer(modifier = Modifier.width(8.dp))
                Button(modifier = Modifier.clip(CircleShape), onClick = {
                    errorMessageDescriptionExtra.value = ""
                    errorMessagePricesExtra.value = ""
                    extrasDescription.value = ""
                    extrasPrice.value = ""
                    modeExtras.value = listOf()
                    extrasDialog.value = true
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(text = stringResource(id = R.string.add))
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(imageVector = Icons.Default.Add, contentDescription = "addExtras")
                    }
                }
            }
        }
        priceTotalExtras.doubleValue = 0.0
        extras.value.forEach { priceTotalExtras.doubleValue += it.priceOrNot ?: 0.0 }
        items(extras.value) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.description)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    errorMessageDescriptionExtra.value = ""
                    errorMessagePricesExtra.value = ""
                    modeExtras.value = listOf(item)
                    extrasDescription.value = item.description
                    extrasPrice.value = item.priceOrNot.toString()
                    extrasDialog.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "editExtras"
                    )
                }
                IconButton(onClick = {
                    try {
                        extras.value = extras.value.filter { it != item }
                    } catch (e: Exception) {
                        Log.d("pruebas", e.message.toString())
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "deleteExtras"
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = (stringResource(id = R.string.travelpriceextras)) + priceTotalExtras.doubleValue + getSymbol(currency.value.currencyCode))
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ChangeCurrency(currency, showCurrencySpinner)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errordigit8)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = price.doubleValue.toString(),
                    onValueChange = {
                        when {
                            it.length > 8 -> {
                                error.value = true
                                errorMessagePrice.value =
                                    error1
                            }

                            else -> {
                                errorMessagePrice.value = ""
                            }
                        }
                        price.doubleValue = it.toDouble()
                    },
                    singleLine = true,
                    label = { Text(stringResource(id = R.string.travelprice)) },
                    suffix = { Text(" " + getSymbol(currency.value.currencyCode)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            priceTravelTotal.doubleValue =
                price.doubleValue + priceTotalActivities.doubleValue + priceTotalExtras.doubleValue
            Text(
                text = stringResource(id = R.string.travelactiviextraprice) + priceTravelTotal.doubleValue + getSymbol(currency.value.currencyCode)
            )
        }
        item {
            if(tripName.value.isEmpty() || tripDescription.value.isEmpty() )
            {
                error.value = true
                errorEmpty.value = stringResource(id = R.string.somethingempty)
            }
            else errorEmpty.value = ""

            if (errorEmpty.value != "") {
                Text(
                    color = Color.Red,
                    text = errorEmpty.value
                )
            }
            if (errorMessageName.value == "" && errorMessageDescription.value == "" && errorMessageDates.value == "" && errorEmpty.value == "") error.value =
                false
            if (error.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Warningedit")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.correct), color = Color.Red)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Button(onClick = { navController?.popBackStack() }) {
                    Text(text = stringResource(id = R.string.lcancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    if (!error.value) {
                        viewModel.apply {
                            getTravelsLocal()
                            travel.let {
                                if (it.id.isNotEmpty()) viewModel.deleteTravel(it.id)
                                saveTravelMain(
                                    Travel(
                                        it.id.ifEmpty { viewModel.getCurrentUser()?.uid + "_" + tripName.value + travels.value.size + 1},
                                        tripName.value,
                                        tripDescription.value,
                                        activities.value,
                                        startDate.value.format(datePattern),
                                        endDate.value.format(datePattern),
                                        extras.value,
                                        price.doubleValue,
                                        currency.value
                                    )
                                )
                            }
                        }
                        navController?.popBackStack()
                    }
                }) {
                    Text(text = stringResource(id = R.string.lcontinue))
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ActivityFormulary(
    paddingValues: PaddingValues,
    activitiesDialog: MutableState<Boolean>,
    modeActivity: MutableState<List<Activity>>,
    activities: MutableState<List<Activity>>,
    activityName: MutableState<String>,
    activityDescription: MutableState<String>,
    activityStartDate: MutableState<LocalDate>,
    activityPrice: MutableState<String>,
    allowedPattern: Regex,
    error: MutableState<Boolean>,
    errorMessageNameActivity: MutableState<String>,
    errorMessageDescriptionActivity: MutableState<String>,
    errorMessageDatesActivity: MutableState<String>,
    errorMessagePriceActivity: MutableState<String>,
    startDate: MutableState<LocalDate>,
    endDate: MutableState<LocalDate>,
    datePattern: DateTimeFormatter,
    currency: MutableState<Coin>
) {
    val errorEmpty = remember { mutableStateOf("") }
    val showStartDateDialogActivity = remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
            .fillMaxWidth()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = if (modeActivity.value.isEmpty()) stringResource(id = R.string.addactivity) else stringResource(
                    id = R.string.editactivity
                ),
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errormessagelns)
            val error2 = stringResource(id = R.string.errormessage20c)
            OutlinedTextField(
                value = activityName.value,
                onValueChange = {
                    when {
                        !allowedPattern.matches(it) -> {
                            error.value = true
                            errorMessageNameActivity.value =
                               error1
                        }

                        it.length > 20 -> {
                            error.value = true
                            errorMessageNameActivity.value =
                                error2
                        }

                        else -> {
                            errorMessageNameActivity.value = ""
                        }
                    }
                    activityName.value = it

                },
                singleLine = true,
                label = { Text(stringResource(id = R.string.nameactivity)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            if (errorMessageNameActivity.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageNameActivity.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errormessagelns)
            val error2 = stringResource(id = R.string.errormessage120c)
            OutlinedTextField(
                value = activityDescription.value,
                onValueChange = {
                    when {
                        !allowedPattern.matches(it) -> {
                            error.value = true
                            errorMessageDescriptionActivity.value =
                                error1
                        }

                        it.length > 120 -> {
                            error.value = true
                            errorMessageDescriptionActivity.value =
                                error2
                        }

                        else -> {
                            errorMessageDescriptionActivity.value = ""
                        }
                    }
                    activityDescription.value = it

                },
                singleLine = false,
                label = { Text(stringResource(id = R.string.descriptionactivity)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            if (errorMessageDescriptionActivity.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageNameActivity.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Card(
                modifier = Modifier.clickable { showStartDateDialogActivity.value = true },
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = stringResource(id = R.string.travelstartdate)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = activityStartDate.value.format(datePattern),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            val error1 = stringResource(id = R.string.errorfiamffv)
            val error2 = stringResource(id = R.string.errorfiagffv)
            val error3 = stringResource(id = R.string.erroraamaiv)
            val error4 = stringResource(id = R.string.erroraamafv)
            when {
                startDate.value.dayOfYear > activityStartDate.value.dayOfYear && startDate.value.year == activityStartDate.value.year -> {
                    errorMessageDatesActivity.value =
                        error1
                    error.value = true
                }

                endDate.value.dayOfYear < activityStartDate.value.dayOfYear && endDate.value.year == activityStartDate.value.year -> {
                    errorMessageDatesActivity.value =
                        error2
                    error.value = true
                }

                startDate.value.year > activityStartDate.value.year -> {
                    errorMessageDatesActivity.value =
                        error3
                    error.value = true
                }

                endDate.value.year < activityStartDate.value.year -> {
                    errorMessageDatesActivity.value =
                        error4
                    error.value = true
                }

                else -> errorMessageDatesActivity.value = ""
            }
            if (errorMessageDatesActivity.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageDatesActivity.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errordigit8)
            OutlinedTextField(
                value = activityPrice.value,
                onValueChange = {
                    if (it.length > 8) {
                        error.value = true
                        errorMessagePriceActivity.value =
                            error1
                    }
                    activityPrice.value = it
                },
                singleLine = true,
                label = { Text(stringResource(id = R.string.activityprice)) },
                suffix = { Text(" " + getSymbol(currency.value.currencyCode)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (errorMessagePriceActivity.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageDatesActivity.value
                )
            }
        }
        item {
            val error1 = stringResource(id = R.string.somethingempty)
            MyDatePickerDialog(
                showDialog = showStartDateDialogActivity.value,
                onDismissRequest = { showStartDateDialogActivity.value = false },
                onDateSelected = { activityStartDate.value = it }
            )
            if(activityName.value.isEmpty() || activityDescription.value.isEmpty() || activityStartDate.value.toString().isEmpty())
            {
                error.value = true
                errorEmpty.value = error1
            }
            else errorEmpty.value = ""

            if (errorEmpty.value != "") {
                Text(
                    color = Color.Red,
                    text = errorEmpty.value
                )
            }

            if (errorMessageNameActivity.value == "" && errorMessageDescriptionActivity.value == "" && errorMessageDatesActivity.value == "" && errorMessagePriceActivity.value == "" && errorEmpty.value == "") error.value =
                false
            if (error.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Warningedit")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.correct), color = Color.Red)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Button(onClick = { activitiesDialog.value = false }) {
                    Text(text = stringResource(id = R.string.lcancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    if(!error.value) {
                        if (modeActivity.value.isEmpty()) {
                            activities.value = activities.value.plus(
                                Activity(
                                    activityName.value,
                                    activityDescription.value,
                                    activityStartDate.value.format(datePattern),
                                    activityPrice.value.toDoubleOrNull() ?: 0.0
                                )
                            )
                        } else {
                            activities.value =
                                activities.value.filter { it != modeActivity.value.first() }.plus(
                                    Activity(
                                        activityName.value,
                                        activityDescription.value,
                                        activityStartDate.value.format(datePattern),
                                        activityPrice.value.toDoubleOrNull() ?: 0.0
                                    )
                                )
                        }
                        activitiesDialog.value = false
                    }
                }) {
                    Text(text = stringResource(id = R.string.lcontinue))
                }
            }
        }
    }
}

@Composable
fun ExtrasFormulary(
    paddingValues: PaddingValues,
    extrasDialog: MutableState<Boolean>,
    modeExtras: MutableState<List<Extra>>,
    extras: MutableState<List<Extra>>,
    extrasDescription: MutableState<String>,
    extrasPrice: MutableState<String>,
    errorMessageDescriptionExtra: MutableState<String>,
    errorMessagePriceExtras: MutableState<String>,
    allowedPattern: Regex,
    error: MutableState<Boolean>,
    currency: MutableState<Coin>
) {
    val errorEmpty = remember {
        mutableStateOf("")
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
            .fillMaxWidth()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = if (modeExtras.value.isEmpty()) stringResource(id = R.string.addextra) else stringResource(id = R.string.editextra),
                style = MaterialTheme.typography.titleLarge
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errormessagelns)
            val error2 = stringResource(id = R.string.errormessage20c)
            OutlinedTextField(
                value = extrasDescription.value,
                onValueChange = {
                    when {
                        !allowedPattern.matches(it) -> {
                            error.value = true
                            errorMessageDescriptionExtra.value =
                                error1
                        }

                        it.length > 20 -> {
                            error.value = true
                            errorMessageDescriptionExtra.value =
                                error2
                        }

                        else -> {
                            errorMessageDescriptionExtra.value = ""
                        }
                    }
                    extrasDescription.value = it

                },
                singleLine = true,
                label = { Text(stringResource(id = R.string.descriptionextra)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )
            if (errorMessageDescriptionExtra.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessageDescriptionExtra.value
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            val error1 = stringResource(id = R.string.errordigit8)
            OutlinedTextField(
                value = extrasPrice.value,
                onValueChange = {
                    if (it.length > 8) {
                        error.value = true
                        errorMessagePriceExtras.value = error1
                    }
                    extrasPrice.value = it
                },
                singleLine = true,
                label = { Text(stringResource(id = R.string.priceextra)) },
                suffix = { Text(" " + getSymbol(currency.value.currencyCode)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (errorMessagePriceExtras.value != "") {
                Text(
                    color = Color.Red,
                    text = errorMessagePriceExtras.value
                )
            }
            val error2 = stringResource(id = R.string.somethingempty)
            if(extrasDescription.value.isEmpty())
            {
                error.value = true
                errorEmpty.value = error2
            }
            else errorEmpty.value = ""

            if (errorEmpty.value != "") {
                Text(
                    color = Color.Red,
                    text = errorEmpty.value
                )
            }
        }
        item {
            if (errorMessageDescriptionExtra.value == "" && errorMessagePriceExtras.value == "" && errorEmpty.value == "") error.value =
                false
            if (error.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Warningedit")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.correct), color = Color.Red)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Button(onClick = { extrasDialog.value = false }) {
                    Text(text = stringResource(id = R.string.lcancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    if(!error.value) {
                        if (modeExtras.value.isEmpty()) {
                            extras.value = extras.value.plus(
                                Extra(
                                    extrasDescription.value,
                                    extrasPrice.value.toDoubleOrNull() ?: 0.0
                                )
                            )
                        } else {
                            extras.value =
                                extras.value.filter { it != modeExtras.value.first() }.plus(
                                    Extra(
                                        extrasDescription.value,
                                        extrasPrice.value.toDoubleOrNull() ?: 0.0
                                    )
                                )
                        }
                        extrasDialog.value = false
                    }
                }) {
                    Text(text = stringResource(id = R.string.lcontinue))
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ChangeCurrency(
    currency: MutableState<Coin>,
    showCurrencySpinner: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.accountcoin) + " ",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        ExposedDropdownMenuBox(
            modifier = Modifier.wrapContentWidth(),
            expanded = showCurrencySpinner.value,
            onExpandedChange = { showCurrencySpinner.value = !showCurrencySpinner.value }
        ) {
            TextField(
                readOnly = true,
                value = getSymbol(currency.value.currencyCode),
                onValueChange = { },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencySpinner.value) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(
                expanded = showCurrencySpinner.value,
                onDismissRequest = { showCurrencySpinner.value = false }
            ) {
                monedasMasFamosas.forEach { selectionOption ->
                    Currency.getInstance(selectionOption).let {
                        DropdownMenuItem(text = {
                            Text(text = it.currencyCode + " | " + it.getSymbol(Locale.US))
                        }, onClick = {
                            currency.value = Coin(selectionOption)
                            showCurrencySpinner.value = false
                        })
                    }
                }
            }
        }
    }
}
