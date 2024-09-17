package es.jesus24041998.myvacations.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jesus24041998.myvacations.R


enum class SnackBarState(@StringRes val messageResId: Int) {
    LOGIN_OK(R.string.lsuccess),
    LOGIN_FAILED(R.string.lerror),
    EMAIL_NOT_VERIFIED(R.string.lemailv),
    EMAIL_RESENT_VERIFICATION(R.string.lemailresend),
    EMAIL_RESENT_VERIFICATION_FAIL(R.string.lemailresendError),
    SESSION_CLOSED(R.string.lclose),
    EMAIL_SEND_VERIFICATION(R.string.lemailsend),
    EMAIL_PASSWORD_MALFORMED(R.string.lemailbadformed),
    WRONG_CREDENTIALS(R.string.lwcredentials),
    ACCOUNT_BLOCK(R.string.lablock)
}

@Composable
fun getMessageForState(state: SnackBarState): String {
    return stringResource(id = state.messageResId)
}

