package com.app.planify.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.planify.R
import com.app.planify.components.PlButton
import com.app.planify.components.PlInput
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToOtp: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val serverClientId = stringResource(R.string.default_web_client_id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlColors.Background)
            .padding(horizontal = PlSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PlLogo()

        Spacer(Modifier.height(PlSpacing.xl))

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(serverClientId)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(
                            context = context,
                            request = request
                        )

                        val credential = result.credential
                        if (
                            credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            val googleCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            viewModel.signInWithGoogle(
                                idToken = googleCredential.idToken,
                                onSuccess = onNavigateToHome
                            )
                        } else {
                            viewModel.showGoogleError()
                        }
                    } catch (e: GetCredentialException) {
                        viewModel.showGoogleError()
                    }
                }
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PlColors.TextHint)
        ) {
            Text("G  ", color = PlColors.Primary, style = PlTypography.titleMedium)
            Text("Continuar con Google", style = PlTypography.labelLarge)
        }

        Spacer(Modifier.height(PlSpacing.md))
        PlDivider()
        Spacer(Modifier.height(PlSpacing.md))

        PlInput(
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico"
        )

        Spacer(Modifier.height(PlSpacing.md))

        PlButton(
            text = "Continuar",
            enabled = viewModel.email.isNotBlank() && !viewModel.isLoading,
            onClick = { viewModel.continuar(onNavigateToOtp) }
        )

        Spacer(Modifier.height(PlSpacing.sm))

        viewModel.errorMessage?.let { message ->
            Text(
                text = message,
                style = PlTypography.bodyMedium,
                color = PlColors.Error,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(PlSpacing.sm))
        }

        Text(
            text = "Ingresa tu email, detectamos si es cuenta nueva",
            style = PlTypography.bodyMedium,
            color = PlColors.TextHint,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlLogo() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(PlColors.Primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("P", style = PlTypography.headlineMedium, color = PlColors.OnPrimary)
    }
    Spacer(Modifier.height(PlSpacing.md))
    Text("Planify", style = PlTypography.headlineLarge, color = PlColors.TextMain)
    Text("tu kit de estudio", style = PlTypography.bodyMedium, color = PlColors.TextHint)
}

@Composable
private fun PlDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = PlColors.TextHint.copy(alpha = 0.3f))
        Text("o", style = PlTypography.bodyMedium, color = PlColors.TextHint)
        HorizontalDivider(modifier = Modifier.weight(1f), color = PlColors.TextHint.copy(alpha = 0.3f))
    }
}
