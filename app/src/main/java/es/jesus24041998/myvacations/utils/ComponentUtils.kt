@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package es.jesus24041998.myvacations.utils

import android.content.Context
import android.content.res.Resources.Theme
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseUser
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.ui.datastore.Travel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@Composable
fun MyDialog(
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            content()
        }
    }
}

@Composable
fun SnackBarView(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                snackbarData = data
            )
        }
    )
}

@Composable
fun SnackBarViewError(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                snackbarData = data
            )
        }
    )
}

@Composable
fun CardInfo(title: String, message: String) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "info"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(title)
            }
            Text(message)
        }
    }
}

@Composable
fun CardError(title: String, message: String) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
            .background(Color.Red),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nointernet),
                    contentDescription = "nointernet"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(title)
            }
            Text(message)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun MyDatePickerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(onDismissRequest = {
            onDismissRequest()
        },
            confirmButton = {
                TextButton(
                    onClick = {
                        val date = datePickerState.selectedDateMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                        date?.let { onDateSelected(it) }
                        onDismissRequest()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) { Text("Cancel") }
            },
            content = {
                DatePicker(state = datePickerState)
            }
        )
    }
}

@Composable
fun MyAlertDialog(
    onConfirm: () -> Unit,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(subtitle)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.lcontinue))
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.lcancel))
            }
        }
    )
}


@Composable
fun MyListTravel(
    user: FirebaseUser?, items: List<Travel>, position: Int, callbackBackUp: () -> Unit,
    callbackNew: () -> Unit, callback: (Int) -> Unit
) {
    var dialogBackup by remember { mutableStateOf(false) }
    val floatingBackup = user?.isAnonymous?.not() ?: false
    Scaffold(
        floatingActionButton = {
            Column {
                if (floatingBackup) {
                    FloatingActionButton(modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape), onClick = { dialogBackup = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_backup),
                            contentDescription = "backup"
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                FloatingActionButton(modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape), onClick = { callbackNew() }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar")
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    ) { innerPadding ->
        if (dialogBackup) {
            MyAlertDialog(
                onConfirm = {
                    callbackBackUp()
                    dialogBackup = false
                },
                title = R.string.titleBackup,
                subtitle = R.string.subtitleBackup
            ) {
                dialogBackup = false
            }
        }
        if (items.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = stringResource(id = R.string.notravels)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Icon(
                    modifier = Modifier.size(MaterialTheme.typography.headlineSmall.fontSize.value.dp),
                    painter = painterResource(id = R.drawable.ic_explore),
                    contentDescription = "explore"
                )
            }
        }
        MyLazyColumn(
            items = items,
            position,
            header = {
                if (items.isNotEmpty()) {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(2.5f),
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.travelname)
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(id = R.string.totalspended)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            content = { index ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            callback(index)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.weight(2.5f),
                        style = MaterialTheme.typography.titleLarge,
                        text = ellipsizeTextScreen(
                            items[index].name,
                            LocalConfiguration.current
                        )
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        text = items[index].total.toString()
                            .plus(" " + getSymbol(items[index].coin.currencyCode))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        )
    }

}

@Composable
private fun MyLazyColumn(
    items: List<Any?>,
    position: Int,
    header: @Composable () -> Unit,
    content: @Composable (index: Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .height((screenHeight * 0.7).dp)
    ) {
        item {
            header()
        }

        items(items.size) { index ->
            content(index)
        }
    }
    LaunchedEffect(Unit) {
        scrollToPosition(listState, coroutineScope, position)
    }
}

private fun scrollToPosition(
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    position: Int
) {
    coroutineScope.launch {
        listState.animateScrollToItem(position)
    }
}