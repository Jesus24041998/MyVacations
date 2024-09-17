package es.jesus24041998.myvacations.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import es.jesus24041998.myvacations.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
fun CardInfo(title:String,message:String) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier= Modifier.padding(16.dp)) {
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
fun MyAlertDialog(
    onLogoutConfirm: () -> Unit,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(subtitle)) },
        confirmButton = {
            Button(onClick = onLogoutConfirm) {
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
fun MyDialogList(
    items: List<String>, position: Int, callback: (Int) -> Unit ,onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            MyLazyColumn(
                items = items,
                position,
                callback = callback,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
private fun MyLazyColumn(items: List<String>, position: Int, callback: (Int) -> Unit,onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val listState = rememberLazyListState()
    LazyColumn(state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height((screenHeight * 0.7).dp)
    ) {
        items(items.size) { index ->
            Text(
                text = items[index],
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        callback(index)
                        onDismissRequest()
                    }
                    .padding(8.dp)
            )

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