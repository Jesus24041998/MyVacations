package es.jesus24041998.myvacations.ui.politics

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jesus24041998.myvacations.R
import es.jesus24041998.myvacations.ui.home.HomeViewModel
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme
import java.util.Locale

@Composable
@Preview(showBackground = true)
private fun PoliticsPreview() {
    MyVacationsTheme {
        PoliticsView(false,"https://sites.google.com/view/myvacationses/politica")
    }
}

@Composable
fun Politics(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val loading by viewModel.isLoading.observeAsState(true)
    val urlPolitica =
        if (Locale.getDefault().language == "es") "https://sites.google.com/view/myvacationses/politica" else "https://sites.google.com/view/myvacationses/politics"
    LaunchedEffect(Unit) {
        viewModel.loadingState(true)
    }
    PoliticsView(loading,urlPolitica,navController,viewModel)
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PoliticsView(loading: Boolean, urlPolitica:String, navController: NavHostController? = null, viewModel: HomeViewModel? = null)
{
    Scaffold(
        topBar = {
            IconButton(onClick = {
                navController?.popBackStack()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
            }
        },
        content = { paddingValues ->
            AndroidView(modifier = Modifier.padding(paddingValues), factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            viewModel?.loadingState(false)
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(urlPolitica)
                }
            })
            if(loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .padding(paddingValues)
                            .align(Alignment.Center)
                    )
                }
            }
        },
        containerColor = Color.White
    )
}