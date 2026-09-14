package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.SaveBiteAmber
import com.example.ui.theme.SaveBiteEmerald
import com.example.util.OtpDispatchInfo

private enum class AuthMode {
    SIGN_IN,
    REGISTER_FORM,
    REGISTER_OTP_VERIFY,
    FORGOT_PASSWORD_EMAIL,
    FORGOT_PASSWORD_RESET
}

@Composable
fun AuthScreen(
    onLogin: (String, String, (Boolean, String) -> Unit) -> Unit,
    onSendOtp: (String, (Boolean, OtpDispatchInfo?, String) -> Unit) -> Unit,
    onRegisterWithOtp: (String, String, String, String, UserRole, String, (Boolean, String) -> Unit) -> Unit,
    onForgotPassword: (String, (Boolean, OtpDispatchInfo?, String) -> Unit) -> Unit,
    onResetPasswordWithOtp: (String, String, String, (Boolean, String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    // Sign In form fields
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Registration form fields
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regOtpCode by remember { mutableStateOf("") }
    var regOtpDispatchInfo by remember { mutableStateOf<OtpDispatchInfo?>(null) }

    // Forgot Password form fields
    var resetEmail by remember { mutableStateOf("") }
    var resetOtpCode by remember { mutableStateOf("") }
    var resetNewPassword by remember { mutableStateOf("") }
    var resetConfirmPassword by remember { mutableStateOf("") }
    var resetPasswordVisible by remember { mutableStateOf(false) }

    // General status state
    var isLoading by remember { mutableStateOf(false) }
    var statusError by remember { mutableStateOf<String?>(null) }
    var statusSuccess by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SaveBiteEmerald.copy(alpha = 0.07f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(36.dp))

                // Brand Hero Header
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SaveBiteEmerald.copy(alpha = 0.12f))
                        .border(2.dp, SaveBiteEmerald.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "SaveBite Logo",
                        tint = SaveBiteEmerald,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SaveBite India",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Surplus Food Rescue & Community Marketplace",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Error or Success Banner
            if (statusError != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = statusError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (statusSuccess != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SaveBiteEmerald.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusSuccess!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = SaveBiteEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Mode Content Transition
            item {
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth_screens"
                ) { currentMode ->
                    when (currentMode) {
                        AuthMode.SIGN_IN -> {
                            SignInView(
                                identifier = loginIdentifier,
                                onIdentifierChange = { loginIdentifier = it; statusError = null },
                                password = loginPassword,
                                onPasswordChange = { loginPassword = it; statusError = null },
                                passwordVisible = loginPasswordVisible,
                                onTogglePasswordVisibility = { loginPasswordVisible = !loginPasswordVisible },
                                isLoading = isLoading,
                                onForgotPasswordClick = {
                                    statusError = null
                                    statusSuccess = null
                                    resetEmail = loginIdentifier
                                    mode = AuthMode.FORGOT_PASSWORD_EMAIL
                                },
                                onSignInClick = {
                                    statusError = null
                                    statusSuccess = null
                                    isLoading = true
                                    onLogin(loginIdentifier, loginPassword) { success, msg ->
                                        isLoading = false
                                        if (!success) {
                                            statusError = msg
                                        }
                                    }
                                },
                                onGoToRegisterClick = {
                                    statusError = null
                                    statusSuccess = null
                                    mode = AuthMode.REGISTER_FORM
                                }
                            )
                        }

                        AuthMode.REGISTER_FORM -> {
                            RegisterFormView(
                                name = regName,
                                onNameChange = { regName = it; statusError = null },
                                email = regEmail,
                                onEmailChange = { regEmail = it; statusError = null },
                                phone = regPhone,
                                onPhoneChange = { regPhone = it; statusError = null },
                                password = regPassword,
                                onPasswordChange = { regPassword = it; statusError = null },
                                confirmPassword = regConfirmPassword,
                                onConfirmPasswordChange = { regConfirmPassword = it; statusError = null },
                                role = regRole,
                                onRoleChange = { regRole = it },
                                passwordVisible = regPasswordVisible,
                                onTogglePasswordVisibility = { regPasswordVisible = !regPasswordVisible },
                                isLoading = isLoading,
                                onSendOtpClick = {
                                    if (regName.isBlank()) {
                                        statusError = "Please enter your full name."
                                        return@RegisterFormView
                                    }
                                    if (regEmail.isBlank() || !regEmail.contains("@")) {
                                        statusError = "Please enter a valid email address."
                                        return@RegisterFormView
                                    }
                                    if (regPassword.length < 6) {
                                        statusError = "Password must be at least 6 characters long."
                                        return@RegisterFormView
                                    }
                                    if (regPassword != regConfirmPassword) {
                                        statusError = "Passwords do not match."
                                        return@RegisterFormView
                                    }

                                    statusError = null
                                    isLoading = true
                                    onSendOtp(regEmail.trim().lowercase()) { success, info, msg ->
                                        isLoading = false
                                        if (success) {
                                            regOtpDispatchInfo = info
                                            statusSuccess = "Verification code dispatched to ${regEmail.trim()}."
                                            mode = AuthMode.REGISTER_OTP_VERIFY
                                        } else {
                                            statusError = msg
                                        }
                                    }
                                },
                                onBackToSignInClick = {
                                    statusError = null
                                    statusSuccess = null
                                    mode = AuthMode.SIGN_IN
                                }
                            )
                        }

                        AuthMode.REGISTER_OTP_VERIFY -> {
                            RegisterOtpVerifyView(
                                email = regEmail,
                                otpCode = regOtpCode,
                                onOtpCodeChange = { regOtpCode = it; statusError = null },
                                isLoading = isLoading,
                                onResendOtpClick = {
                                    statusError = null
                                    isLoading = true
                                    onSendOtp(regEmail.trim().lowercase()) { success, info, msg ->
                                        isLoading = false
                                        if (success) {
                                            regOtpDispatchInfo = info
                                            statusSuccess = "A new verification code was sent to ${regEmail.trim()}."
                                        } else {
                                            statusError = msg
                                        }
                                    }
                                },
                                onVerifyAndRegisterClick = {
                                    if (regOtpCode.trim().length != 6) {
                                        statusError = "Please enter the complete 6-digit OTP code."
                                        return@RegisterOtpVerifyView
                                    }
                                    statusError = null
                                    isLoading = true
                                    onRegisterWithOtp(
                                        regName.trim(),
                                        regEmail.trim().lowercase(),
                                        regPhone.trim().ifBlank { "+91 98000 12345" },
                                        regPassword.trim(),
                                        regRole,
                                        regOtpCode.trim()
                                    ) { success, msg ->
                                        isLoading = false
                                        if (!success) {
                                            statusError = msg
                                        }
                                    }
                                },
                                onEditEmailClick = {
                                    statusError = null
                                    statusSuccess = null
                                    mode = AuthMode.REGISTER_FORM
                                }
                            )
                        }

                        AuthMode.FORGOT_PASSWORD_EMAIL -> {
                            ForgotPasswordEmailView(
                                email = resetEmail,
                                onEmailChange = { resetEmail = it; statusError = null },
                                isLoading = isLoading,
                                onSendResetCodeClick = {
                                    if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                                        statusError = "Please enter a valid registered email address."
                                        return@ForgotPasswordEmailView
                                    }
                                    statusError = null
                                    isLoading = true
                                    onForgotPassword(resetEmail.trim().lowercase()) { success, _, msg ->
                                        isLoading = false
                                        if (success) {
                                            statusSuccess = "Reset verification code sent to ${resetEmail.trim()}."
                                            mode = AuthMode.FORGOT_PASSWORD_RESET
                                        } else {
                                            statusError = msg
                                        }
                                    }
                                },
                                onBackToSignInClick = {
                                    statusError = null
                                    statusSuccess = null
                                    mode = AuthMode.SIGN_IN
                                }
                            )
                        }

                        AuthMode.FORGOT_PASSWORD_RESET -> {
                            ForgotPasswordResetView(
                                email = resetEmail,
                                otpCode = resetOtpCode,
                                onOtpCodeChange = { resetOtpCode = it; statusError = null },
                                newPassword = resetNewPassword,
                                onNewPasswordChange = { resetNewPassword = it; statusError = null },
                                confirmPassword = resetConfirmPassword,
                                onConfirmPasswordChange = { resetConfirmPassword = it; statusError = null },
                                passwordVisible = resetPasswordVisible,
                                onTogglePasswordVisibility = { resetPasswordVisible = !resetPasswordVisible },
                                isLoading = isLoading,
                                onResetPasswordClick = {
                                    if (resetOtpCode.trim().length != 6) {
                                        statusError = "Please enter the 6-digit reset code."
                                        return@ForgotPasswordResetView
                                    }
                                    if (resetNewPassword.length < 6) {
                                        statusError = "Password must be at least 6 characters."
                                        return@ForgotPasswordResetView
                                    }
                                    if (resetNewPassword != resetConfirmPassword) {
                                        statusError = "Passwords do not match."
                                        return@ForgotPasswordResetView
                                    }

                                    statusError = null
                                    isLoading = true
                                    onResetPasswordWithOtp(
                                        resetEmail.trim().lowercase(),
                                        resetOtpCode.trim(),
                                        resetNewPassword.trim()
                                    ) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            statusSuccess = "Password updated! Please sign in with your new credentials."
                                            loginIdentifier = resetEmail
                                            loginPassword = ""
                                            mode = AuthMode.SIGN_IN
                                        } else {
                                            statusError = msg
                                        }
                                    }
                                },
                                onBackToEmailClick = {
                                    statusError = null
                                    statusSuccess = null
                                    mode = AuthMode.FORGOT_PASSWORD_EMAIL
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ---------------- SUBVIEWS ----------------

@Composable
private fun SignInView(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    onForgotPasswordClick: () -> Unit,
    onSignInClick: () -> Unit,
    onGoToRegisterClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Welcome back! Enter your registered credentials to access your dashboard.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            OutlinedTextField(
                value = identifier,
                onValueChange = onIdentifierChange,
                label = { Text("Email Address or Phone") },
                placeholder = { Text("e.g. roshan@example.com") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_login_identifier")
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSignInClick() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_login_password")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelMedium,
                        color = SaveBiteEmerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSignInClick,
                enabled = !isLoading && identifier.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_sign_in")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                TextButton(onClick = onGoToRegisterClick) {
                    Text(
                        text = "Create Account",
                        fontWeight = FontWeight.Bold,
                        color = SaveBiteEmerald
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterFormView(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    role: UserRole,
    onRoleChange: (UserRole) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    onSendOtpClick: () -> Unit,
    onBackToSignInClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToSignInClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Sign In")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Step 1 of 2: Profile & Role Details",
                style = MaterialTheme.typography.labelMedium,
                color = SaveBiteEmerald,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Select Role Selector
            Text(
                text = "Select your account type:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val roleOptions = listOf(
                Triple(UserRole.CUSTOMER, "Consumer / Rescuer", Icons.Default.Person),
                Triple(UserRole.RESTAURANT, "Restaurant / Bakery", Icons.Default.Store),
                Triple(UserRole.PICKUP_AGENT, "Delivery Partner", Icons.Default.LocalShipping),
                Triple(UserRole.NGO, "Registered NGO", Icons.Default.VolunteerActivism)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                roleOptions.forEach { (roleOpt, label, icon) ->
                    val isSelected = role == roleOpt
                    Surface(
                        onClick = { onRoleChange(roleOpt) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SaveBiteEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SaveBiteEmerald) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SaveBiteEmerald else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Priya Sharma") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email Address (OTP will be sent here)") },
                placeholder = { Text("priya@example.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Mobile Phone Number") },
                placeholder = { Text("+91 98765 43210") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password (min. 6 characters)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSendOtpClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Send OTP & Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onBackToSignInClick) {
                    Text(
                        text = "Already have an account? Sign In",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterOtpVerifyView(
    email: String,
    otpCode: String,
    onOtpCodeChange: (String) -> Unit,
    isLoading: Boolean,
    onResendOtpClick: () -> Unit,
    onVerifyAndRegisterClick: () -> Unit,
    onEditEmailClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SaveBiteEmerald.copy(alpha = 0.12f))
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = SaveBiteEmerald,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Verify Your Email",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "We sent a 6-digit verification code to:\n$email",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
            )

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) onOtpCodeChange(it.filter { char -> char.isDigit() }) },
                label = { Text("6-Digit Verification Code") },
                placeholder = { Text("e.g. 123456") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onVerifyAndRegisterClick() }),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_otp")
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onVerifyAndRegisterClick,
                enabled = !isLoading && otpCode.length == 6,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_verify_and_create")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Verify & Create Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onEditEmailClick) {
                    Text("Change Email", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                TextButton(onClick = onResendOtpClick) {
                    Text("Resend Code", color = SaveBiteEmerald, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordEmailView(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onSendResetCodeClick: () -> Unit,
    onBackToSignInClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToSignInClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Enter your registered email address and we'll dispatch a 6-digit recovery code to verify your identity.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Registered Email Address") },
                placeholder = { Text("e.g. roshan@example.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSendResetCodeClick() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSendResetCodeClick,
                enabled = !isLoading && email.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Send Recovery Code", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = onBackToSignInClick) {
                    Text("Remember password? Sign In", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordResetView(
    email: String,
    otpCode: String,
    onOtpCodeChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    onResetPasswordClick: () -> Unit,
    onBackToEmailClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToEmailClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Password",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Enter the 6-digit recovery code sent to $email and your new password.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) onOtpCodeChange(it.filter { c -> c.isDigit() }) },
                label = { Text("6-Digit Recovery Code") },
                placeholder = { Text("123456") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = { Text("New Password (min. 6 chars)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onResetPasswordClick() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onResetPasswordClick,
                enabled = !isLoading && otpCode.length == 6 && newPassword.length >= 6,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Update Password & Return to Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
