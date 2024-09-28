package es.jesus24041998.myvacations.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navOptions
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.BuildConfig
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.ui.datastore.Coin
import es.jesus24041998.myvacations.utils.CardInfo
import es.jesus24041998.myvacations.utils.MyAlertDialog
import es.jesus24041998.myvacations.utils.monedasMasFamosas
import java.util.Currency
import java.util.Locale

@Composable
@Preview(showBackground = true)
private fun ProfilePreview() {
    MyVacationsTheme {
        ProfileView(onNavigateToLogin = {}, onNavigateToPolitics =  {})
    }
}

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToPolitics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    ProfileView(viewModel, onNavigateToLogin, onNavigateToPolitics, loading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileView(
    viewModel: HomeViewModel? = null,
    onNavigateToLogin: () -> Unit,
    onNavigateToPolitics: () -> Unit,
    loading: Boolean = false,
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val user = viewModel?.getCurrentUser()
    val userName = if(user?.isAnonymous == true) stringResource(id = R.string.accountguest) else user?.displayName ?: stringResource(id = R.string.accountguest)
    var showInfo by remember { mutableStateOf(false) }
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // Solo email
        putExtra(Intent.EXTRA_EMAIL, arrayOf("my.vacations.es@gmail.com"))
    }

    BaseScreen(content = {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)) {
            Text(
                text = stringResource(id = R.string.accountprofile),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.accountusername)+ ": ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp,fontWeight = FontWeight.Bold)
                )
                if(user?.isAnonymous == true) {
                    IconButton(onClick = { showInfo = true }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "info"
                        )
                    }
                }
            }

            if(showInfo) {
                ModalBottomSheet(
                    onDismissRequest = { showInfo = false }
                ) {
                    CardInfo(
                        stringResource(id = R.string.accounttitleinfo),
                        stringResource(id = R.string.accountdescriptioninfo)
                    )
                }
            }
            BackHandler(enabled = showInfo) {
                showInfo = false
            }

            Spacer(modifier = Modifier.height(4.dp))

            if(user?.isAnonymous == true) {
                Text(
                    text = stringResource(id = R.string.accountnoconnect),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp,textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable {
                        onNavigateToLogin()
                    }
                )
                Spacer(modifier = Modifier.height(21.dp))
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
            }
            else{
                Spacer(modifier = Modifier.height(25.dp))
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
            }

            Text(
                text = stringResource(id = R.string.accountcontact),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.accountassitance),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable {
                        context.startActivity(Intent.createChooser(emailIntent, ""))
                    }
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = stringResource(id = R.string.accountlegal),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.accountpolitices),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable {
                        onNavigateToPolitics()
                    }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(25.dp))
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))

            if(user?.isAnonymous == false)  Text(
                text = stringResource(id = R.string.accountlogout),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .clickable {
                        showDialog = true
                    }
                    .padding(vertical = 8.dp)
            )
            if (showDialog) {
                MyAlertDialog(
                    onLogoutConfirm = {
                        showDialog = false
                        viewModel?.logout()
                        onNavigateToLogin()
                    },
                    title = R.string.lclose,
                    subtitle = R.string.lclosesesion
                ) {
                    showDialog = false
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.app_version) + " " + BuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }, isLoading = loading)
}
