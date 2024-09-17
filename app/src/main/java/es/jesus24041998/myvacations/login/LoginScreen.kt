package es.jesus24041998.myvacations.login

import android.content.Context
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.base.BaseScreen
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import es.jesus24041998.myvacations.utils.MyDialog
import es.jesus24041998.myvacations.utils.SnackBarState
import es.jesus24041998.myvacations.utils.getMessageForState
import kotlinx.coroutines.launch

@Composable
@Preview(showBackground = true)
private fun LoginScreenPreview() {
    MyVacationsTheme {
        LoginScreenView(
            onNavigateToHome = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun EmailModelPreview() {
    MyVacationsTheme {
        EmailModeView(
            LocalSoftwareKeyboardController.current,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun SnackBarPreview() {
    MyVacationsTheme {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(Unit) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Soy un snackbar",
                    actionLabel = "Y yo acción"
                )
            }
        }
        SnackBarView(snackbarHostState)
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
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loading by viewModel.isLoading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val authState by viewModel.authState.observeAsState()
    val toastState by viewModel.toastState.observeAsState()
    val throwHome by viewModel.throwHome.observeAsState()
    val messageToast = toastState?.let { getMessageForState(it) } ?: ""
    val resentButton = stringResource(id = R.string.lresent)

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(toastState) {
        toastState?.let {
            val result = snackbarHostState.showSnackbar(
                message = messageToast,
                actionLabel = if (toastState == SnackBarState.EMAIL_NOT_VERIFIED) resentButton else null
            )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.resendVerificationEmail()
                else -> {/*Not used*/
                }
            }
        }
    }

    LaunchedEffect(throwHome) {
        if (authState != null && authState?.isAnonymous == false && authState?.isEmailVerified == true) {
            onNavigateToHome()
        }
    }

    BaseScreen(content = {
        LoginScreenView(
            onNavigateToHome,
            snackbarHostState,
            keyboardController,
            viewModel
        )
    }, isLoading = loading)
}

@Composable
fun LoginScreenView(
    onNavigateToHome: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
    keyboardController: SoftwareKeyboardController? = null,
    viewModel: LoginViewModel? = null
) {
    Scaffold(
        topBar = {
            IconButton(onClick = {
                onNavigateToHome()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Botonback",
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        snackbarHost = {
            snackbarHostState?.let {
                SnackBarView(it)
            }
        },
        content = { paddingValues ->
            val modifier = Modifier
                .padding(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Inicia sesión o regístrate",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                EmailModeView(
                    keyboardController,
                    viewModel,
                    modifier
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("o")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateToHome() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.lcontinueguest),
                            modifier = Modifier.weight(4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        })
}

@Composable
private fun EmailModeView(
    keyboardController: SoftwareKeyboardController?,
    viewModel: LoginViewModel? = null,
    modifier: Modifier,
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    )
    {
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            singleLine = true,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image =
                    if (passwordVisible.value) R.drawable.ic_eye else R.drawable.ic_eyeoff
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                if (email.value != "" && password.value != "") viewModel?.onRegisterClick(
                    email.value,
                    password.value
                ) else viewModel?.emailPasswordMalformed()
            },

            enabled = email.value.matches(Regex("^[a-zA-Z0-9._%+-]+@(gmail|hotmail|yahoo)\\.com\$")) && password.value.matches(
                Regex("^\\S{6,}$")
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Continuar")
        }
    }
}