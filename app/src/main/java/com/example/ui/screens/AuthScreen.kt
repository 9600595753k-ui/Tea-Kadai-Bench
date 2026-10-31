package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.SignalGreen

enum class AuthMode {
    SIGN_IN,
    REGISTER
}

enum class AuthStep {
    PHONE_INPUT,
    OTP_VERIFY,
    PROFILE_DETAILS // for new registration
}

@Composable
fun AuthScreen(
    onAuthSuccess: (phone: String, name: String, handle: String, bio: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var step by remember { mutableStateOf(AuthStep.PHONE_INPUT) }

    var phoneNumber by remember { mutableStateOf("+1 (555) 234-8901") }
    var otpCode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("Alex Vance") }
    var handle by remember { mutableStateOf("@alexvance") }
    var bio by remember { mutableStateOf("Acoustic enthusiast & founder. Building high-fidelity live audio spaces.") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B1612),
                        Color(0xFF13100E),
                        Color(0xFF0D0B0A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Name
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26211C))
                    .border(2.dp, AmberGoldPrimary, CircleShape)
            ) {
                Text(
                    text = "☕",
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tea Kadai Bench",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnDarkTextPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Roadside chai shop banter • Live audio rooms & zero data tracking",
                fontSize = 12.sp,
                color = OnDarkTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Switcher: Sign In vs Register (only shown on initial step)
            if (step == AuthStep.PHONE_INPUT) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF231E19))
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (mode == AuthMode.SIGN_IN) AmberGoldPrimary else Color.Transparent)
                            .clickable {
                                mode = AuthMode.SIGN_IN
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_sign_in")
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode == AuthMode.SIGN_IN) Color(0xFF1E1405) else OnDarkTextSecondary
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (mode == AuthMode.REGISTER) AmberGoldPrimary else Color.Transparent)
                            .clickable {
                                mode = AuthMode.REGISTER
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_register")
                    ) {
                        Text(
                            text = "Register",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mode == AuthMode.REGISTER) Color(0xFF1E1405) else OnDarkTextSecondary
                        )
                    }
                }
            } else {
                // Back button to previous step
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (step == AuthStep.PROFILE_DETAILS) step = AuthStep.OTP_VERIFY
                            else if (step == AuthStep.OTP_VERIFY) step = AuthStep.PHONE_INPUT
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AmberGoldPrimary
                        )
                    }
                    Text(
                        text = if (step == AuthStep.OTP_VERIFY) "SMS Code Verification" else "Complete Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content
            when (step) {
                AuthStep.PHONE_INPUT -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E1915),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382F26)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = if (mode == AuthMode.SIGN_IN) "Sign in with Mobile Number" else "Create Your Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnDarkTextPrimary
                            )
                            Text(
                                text = "Only registered with mobile phone number for authenticated security. We do not track or sell any user data.",
                                fontSize = 11.sp,
                                color = OnDarkTextSecondary,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = {
                                    phoneNumber = it
                                    errorMessage = null
                                },
                                label = { Text("Mobile Phone Number") },
                                placeholder = { Text("+1 (555) 234-8901") },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = AmberGoldPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        if (phoneNumber.isNotBlank()) step = AuthStep.OTP_VERIFY
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_phone_input_field"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGoldPrimary,
                                    unfocusedBorderColor = Color(0xFF453B32)
                                )
                            )

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFDF5353),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (phoneNumber.isBlank() || phoneNumber.length < 6) {
                                        errorMessage = "Please enter a valid mobile number"
                                    } else {
                                        step = AuthStep.OTP_VERIFY
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGoldPrimary,
                                    contentColor = Color(0xFF1E1405)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("auth_submit_phone_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (mode == AuthMode.SIGN_IN) "Send SMS Login Code" else "Send Verification Code",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                AuthStep.OTP_VERIFY -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E1915),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382F26)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SignalGreen)
                                Text(
                                    text = "Enter 6-Digit Code",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnDarkTextPrimary
                                )
                            }

                            Text(
                                text = "A simulated SMS confirmation was sent to $phoneNumber. Use default code 123456.",
                                fontSize = 11.sp,
                                color = OnDarkTextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = {
                                    otpCode = it
                                    errorMessage = null
                                },
                                label = { Text("SMS Verification Code") },
                                placeholder = { Text("123456") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (mode == AuthMode.SIGN_IN) {
                                            onAuthSuccess(phoneNumber, name, handle, bio)
                                        } else {
                                            step = AuthStep.PROFILE_DETAILS
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_otp_input_field"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGoldPrimary,
                                    unfocusedBorderColor = Color(0xFF453B32)
                                )
                            )

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFDF5353),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (otpCode.isBlank()) {
                                        otpCode = "123456" // autofill if blank for smooth testing
                                    }
                                    if (mode == AuthMode.SIGN_IN) {
                                        onAuthSuccess(phoneNumber, name, handle, bio)
                                    } else {
                                        step = AuthStep.PROFILE_DETAILS
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGoldPrimary,
                                    contentColor = Color(0xFF1E1405)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("auth_verify_otp_button")
                            ) {
                                Text(
                                    text = if (mode == AuthMode.SIGN_IN) "Verify & Enter Live Lounge" else "Next: Profile Setup",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                AuthStep.PROFILE_DETAILS -> {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E1915),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382F26)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Setup Your Voice Identity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnDarkTextPrimary
                            )
                            Text(
                                text = "How others on stage and in audience will see and hear you.",
                                fontSize = 11.sp,
                                color = OnDarkTextSecondary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Display Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = handle,
                                onValueChange = { handle = it },
                                label = { Text("Username Handle") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_handle_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("Short Bio") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val finalName = name.ifBlank { "Club Member" }
                                    val finalHandle = if (handle.startsWith("@")) handle else "@$handle"
                                    onAuthSuccess(phoneNumber, finalName, finalHandle, bio)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGoldPrimary,
                                    contentColor = Color(0xFF1E1405)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("auth_complete_registration_button")
                            ) {
                                Text(
                                    text = "Complete Registration & Enter Hall",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Zero Data Privacy Guarantee
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1B1612))
                    .border(1.dp, Color(0xFF2E2721), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Encrypted On-Device • Zero Data Collection",
                    fontSize = 11.sp,
                    color = OnDarkTextSecondary
                )
            }
        }
    }
}
