package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

data class DemoAccount(
    val title: String,
    val roleName: String,
    val email: String,
    val pass: String,
    val role: UserRole,
    val icon: ImageVector,
    val badgeColor: Color
)

@Composable
fun AuthScreen(
    onLogin: (String, String, (Boolean, String) -> Unit) -> Unit,
    onRegister: (String, String, String, String, UserRole, (Boolean, String) -> Unit) -> Unit,
    onQuickLogin: (UserRole) -> Unit,
    onSendOtp: ((String, (Boolean, com.example.util.OtpDispatchInfo?, String) -> Unit) -> Unit)? = null,
    onVerifyOtp: ((String, String, UserRole, (Boolean, String) -> Unit) -> Unit)? = null,
    activeOtpDispatch: com.example.util.OtpDispatchInfo? = null,
    activeJwtToken: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Email OTP, 2: Register

    // Login state
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Email OTP state
    var otpEmail by remember { mutableStateOf("aarav.sharma@savebite.in") }
    var otpCode by remember { mutableStateOf("") }
    var otpRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var localOtpInfo by remember { mutableStateOf<com.example.util.OtpDispatchInfo?>(null) }
    var isSendingOtp by remember { mutableStateOf(false) }

    // Register state
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    // Loading & feedback
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val demoAccounts = remember {
        listOf(
            DemoAccount(
                title = "Aarav Sharma",
                roleName = "Customer",
                email = "aarav.sharma@savebite.in",
                pass = "password123",
                role = UserRole.CUSTOMER,
                icon = Icons.Default.Person,
                badgeColor = Color(0xFF10B981)
            ),
            DemoAccount(
                title = "Bikaner Sweets",
                roleName = "Merchant",
                email = "vikramaditya@bikanersweets.in",
                pass = "password123",
                role = UserRole.BAKERY,
                icon = Icons.Default.Store,
                badgeColor = Color(0xFFF59E0B)
            ),
            DemoAccount(
                title = "Rajat Verma",
                roleName = "Pickup Partner",
                email = "rajat.courier@savebite.in",
                pass = "password123",
                role = UserRole.PICKUP_AGENT,
                icon = Icons.Default.LocalShipping,
                badgeColor = Color(0xFF3B82F6)
            ),
            DemoAccount(
                title = "Rajesh Verma",
                roleName = "Admin HQ",
                email = "admin@savebite.in",
                pass = "password123",
                role = UserRole.ADMIN,
                icon = Icons.Default.AdminPanelSettings,
                badgeColor = Color(0xFF8B5CF6)
            ),
            DemoAccount(
                title = "Robin Hood Army",
                roleName = "NGO Partner",
                email = "ananya@robinhoodarmy.org",
                pass = "password123",
                role = UserRole.NGO,
                icon = Icons.Default.VolunteerActivism,
                badgeColor = Color(0xFFEC4899)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Brand Hero
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = CircleShape,
                    color = SaveBiteEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "SaveBite Logo",
                            tint = SaveBiteEmerald,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SaveBite India",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Zero Food Waste • High Value Rescues • Direct Impact",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Quick Demo Account Selector (High convenience for reviewers)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚡ Quick Demo Logins",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Tap any role to enter",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(demoAccounts) { demo ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 2.dp,
                                    modifier = Modifier
                                        .clickable {
                                            errorMessage = null
                                            successMessage = null
                                            loginIdentifier = demo.email
                                            loginPassword = demo.pass
                                            onQuickLogin(demo.role)
                                        }
                                        .testTag("demo_login_${demo.role.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = demo.badgeColor.copy(alpha = 0.2f),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = demo.icon,
                                                    contentDescription = null,
                                                    tint = demo.badgeColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = demo.roleName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = demo.title,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Auth Tabs: Password Login vs Email OTP vs Register
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SaveBiteEmerald,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SaveBiteEmerald
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                            successMessage = null
                        },
                        text = {
                            Text(
                                text = "Password",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.testTag("auth_tab_login")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                            successMessage = null
                        },
                        text = {
                            Text(
                                text = "Email OTP",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.testTag("auth_tab_otp")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            errorMessage = null
                            successMessage = null
                        },
                        text = {
                            Text(
                                text = "Register",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.testTag("auth_tab_register")
                    )
                }
            }

            // Error / Success Banners
            if (errorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (successMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = successMessage ?: "",
                            color = Color(0xFF065F46),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Form Fields
            if (selectedTab == 0) {
                // LOGIN FORM
                item {
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("Email Address or Phone") },
                        placeholder = { Text("e.g. aarav.sharma@savebite.in") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        placeholder = { Text("Enter password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        trailingIcon = {
                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                Icon(
                                    imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (!isLoading) {
                                    isLoading = true
                                    errorMessage = null
                                    onLogin(loginIdentifier, loginPassword) { success, msg ->
                                        isLoading = false
                                        if (!success) errorMessage = msg
                                    }
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (!isLoading) {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                onLogin(loginIdentifier, loginPassword) { success, msg ->
                                    isLoading = false
                                    if (!success) errorMessage = msg
                                }
                            }
                        },
                        enabled = !isLoading && loginIdentifier.isNotBlank() && loginPassword.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_login"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaveBiteEmerald
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign In to Your Dashboard",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // EMAIL OTP LOGIN FORM
                item {
                    OutlinedTextField(
                        value = otpEmail,
                        onValueChange = { otpEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. roshankurkutiya285@gmail.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                // Send OTP Button
                item {
                    Button(
                        onClick = {
                            if (!isSendingOtp && otpEmail.isNotBlank()) {
                                isSendingOtp = true
                                errorMessage = null
                                successMessage = null
                                if (onSendOtp != null) {
                                    onSendOtp(otpEmail) { success, info, msg ->
                                        isSendingOtp = false
                                        if (success) {
                                            localOtpInfo = info
                                            successMessage = msg
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                } else {
                                    val dispatchRes = com.example.util.EmailOtpManager.dispatchOtp(otpEmail)
                                    isSendingOtp = false
                                    dispatchRes.onSuccess { info ->
                                        localOtpInfo = info
                                        successMessage = "Verification code dispatched to $otpEmail!"
                                    }.onFailure { err ->
                                        errorMessage = err.message ?: "Failed to generate OTP"
                                    }
                                }
                            }
                        },
                        enabled = !isSendingOtp && otpEmail.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_send_otp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (localOtpInfo != null || activeOtpDispatch != null) MaterialTheme.colorScheme.secondary else SaveBiteEmerald
                        )
                    ) {
                        if (isSendingOtp) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (localOtpInfo != null || activeOtpDispatch != null) "Resend 6-Digit OTP" else "Send 6-Digit OTP to Email",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // In-App Email Dispatch Simulation Preview Card
                val activeDispatch = activeOtpDispatch ?: localOtpInfo
                if (activeDispatch != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_email_simulation_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📬 In-App Email Dispatch Simulator",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Just Now",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "To: ${activeDispatch.email}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Subject: SaveBite Security: Your One-Time Login Code",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Verification Code:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = activeDispatch.code,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 4.sp,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                            modifier = Modifier.clickable {
                                                otpCode = activeDispatch.code
                                            }
                                        ) {
                                            Text(
                                                text = "⚡ Quick Fill",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF38BDF8),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Valid for 5 minutes. Never share this code with anyone.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 6-digit OTP Code Input
                item {
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { input ->
                            if (input.length <= 6) {
                                otpCode = input.filter { it.isDigit() }
                            }
                        },
                        label = { Text("6-Digit OTP Verification Code") },
                        placeholder = { Text("e.g. 849201") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                // Role selection for OTP login if new account
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Login As Role (for new accounts):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                UserRole.CUSTOMER to "Customer",
                                UserRole.BAKERY to "Merchant",
                                UserRole.PICKUP_AGENT to "Pickup",
                                UserRole.ADMIN to "Admin"
                            ).forEach { (role, label) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (otpRole == role) SaveBiteEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (otpRole == role) androidx.compose.foundation.BorderStroke(1.5.dp, SaveBiteEmerald) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { otpRole = role }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (otpRole == role) FontWeight.Bold else FontWeight.Normal,
                                        color = if (otpRole == role) SaveBiteEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Submit Verify OTP
                item {
                    Button(
                        onClick = {
                            if (!isLoading && otpCode.length >= 6) {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                if (onVerifyOtp != null) {
                                    onVerifyOtp(otpEmail, otpCode, otpRole) { success, msg ->
                                        isLoading = false
                                        if (!success) errorMessage = msg
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "OTP verification callback not configured."
                                }
                            }
                        },
                        enabled = !isLoading && otpEmail.isNotBlank() && otpCode.length == 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_verify_otp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaveBiteEmerald)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Verify OTP & Generate JWT Session",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // REGISTER FORM
                item {
                    OutlinedTextField(
                        value = registerName,
                        onValueChange = { registerName = it },
                        label = { Text("Full Name / Business Name") },
                        placeholder = { Text("e.g. Rohan Kurkutiya / Royal Bakes") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = registerEmail,
                        onValueChange = { registerEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. roshan@example.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = registerPhone,
                        onValueChange = { registerPhone = it },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("+91 98765 43210") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = registerPassword,
                        onValueChange = { registerPassword = it },
                        label = { Text("Set Password") },
                        placeholder = { Text("Min 4 characters") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SaveBiteEmerald)
                        },
                        trailingIcon = {
                            IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                                Icon(
                                    imageVector = if (registerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaveBiteEmerald,
                            focusedLabelColor = SaveBiteEmerald
                        )
                    )
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Select Account Role:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Your dashboard is customized based on this selection",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                item {
                    RoleSelectionCard(
                        title = "Customer / Rescuer",
                        description = "Browse surplus food bags, reserve at 50-70% discount, earn eco badges",
                        icon = Icons.Default.Person,
                        role = UserRole.CUSTOMER,
                        isSelected = selectedRole == UserRole.CUSTOMER,
                        accentColor = Color(0xFF10B981),
                        onClick = { selectedRole = UserRole.CUSTOMER }
                    )
                }

                item {
                    RoleSelectionCard(
                        title = "Merchant / Food Business",
                        description = "Bakery, Restaurant, Dhaba or Cafe selling daily surplus batches & managing inventory",
                        icon = Icons.Default.Store,
                        role = UserRole.BAKERY,
                        isSelected = selectedRole == UserRole.BAKERY || selectedRole == UserRole.RESTAURANT,
                        accentColor = Color(0xFFF59E0B),
                        onClick = { selectedRole = UserRole.BAKERY }
                    )
                }

                item {
                    RoleSelectionCard(
                        title = "Pickup & Logistics Partner",
                        description = "Store-to-customer food collection, transit handovers & QR pass verification",
                        icon = Icons.Default.LocalShipping,
                        role = UserRole.PICKUP_AGENT,
                        isSelected = selectedRole == UserRole.PICKUP_AGENT,
                        accentColor = Color(0xFF3B82F6),
                        onClick = { selectedRole = UserRole.PICKUP_AGENT }
                    )
                }

                item {
                    RoleSelectionCard(
                        title = "Platform Admin",
                        description = "Pan-India oversight, merchant FSSAI audits, emergency alerts & user directory",
                        icon = Icons.Default.AdminPanelSettings,
                        role = UserRole.ADMIN,
                        isSelected = selectedRole == UserRole.ADMIN,
                        accentColor = Color(0xFF8B5CF6),
                        onClick = { selectedRole = UserRole.ADMIN }
                    )
                }

                item {
                    RoleSelectionCard(
                        title = "NGO Partner (Annadaan)",
                        description = "Rescue banquet wedding surplus, claim free food bags for shelter distribution",
                        icon = Icons.Default.VolunteerActivism,
                        role = UserRole.NGO,
                        isSelected = selectedRole == UserRole.NGO,
                        accentColor = Color(0xFFEC4899),
                        onClick = { selectedRole = UserRole.NGO }
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (!isLoading) {
                                isLoading = true
                                errorMessage = null
                                successMessage = null
                                onRegister(
                                    registerName,
                                    registerEmail,
                                    registerPhone,
                                    registerPassword,
                                    selectedRole
                                ) { success, msg ->
                                    isLoading = false
                                    if (!success) {
                                        errorMessage = msg
                                    } else {
                                        successMessage = "Account created successfully! Welcome to SaveBite."
                                    }
                                }
                            }
                        },
                        enabled = !isLoading && registerName.isNotBlank() && registerEmail.isNotBlank() && registerPassword.length >= 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_register"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaveBiteEmerald
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Create Account & Enter Dashboard",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_security_specs_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = SaveBiteEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enterprise Security & Payment Stack",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• JWT Session: Cryptographic HMAC-SHA256 tokens with role-claim signing & auto-refresh.\n" +
                                   "• Email OTP: 6-digit one-time passcode with 5-minute expiry & rate-limiting.\n" +
                                   "• Razorpay Integration: UPI QR, NetBanking, Cards with verified signature callbacks.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            lineHeight = 16.sp
                        )
                        if (activeJwtToken != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🔑 Active Token: ${activeJwtToken.take(18)}... (Verified)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SaveBiteEmerald
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = SaveBiteEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SaveBite India • FSSAI Certified Partners",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    description: String,
    icon: ImageVector,
    role: UserRole,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, accentColor)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("role_card_${role.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
