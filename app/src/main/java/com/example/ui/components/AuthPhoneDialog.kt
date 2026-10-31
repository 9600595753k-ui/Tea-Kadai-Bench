package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.SignalGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthPhoneDialog(
    initialPhone: String,
    initialName: String,
    initialHandle: String,
    onDismiss: () -> Unit,
    onRegister: (phone: String, name: String, handle: String) -> Unit
) {
    var phoneInput by remember { mutableStateOf(initialPhone) }
    var nameInput by remember { mutableStateOf(initialName) }
    var handleInput by remember { mutableStateOf(initialHandle) }
    var otpCode by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Enter details, 2: Verify SMS OTP

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1915),
        contentColor = OnDarkTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = SignalGreen,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = if (step == 1) "Mobile Number Registration" else "Enter Verification Code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnDarkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ClubAudio uses mobile number verification to authenticate membership and protect live rooms from unauthorized access with zero user tracking.",
                fontSize = 12.sp,
                color = OnDarkTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (step == 1) {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Mobile Phone Number") },
                    placeholder = { Text("+1 (555) 000-0000") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AmberGoldPrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = handleInput,
                    onValueChange = { handleInput = it },
                    label = { Text("Username Handle") },
                    placeholder = { Text("@username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phoneInput.isNotBlank()) {
                            step = 2
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
                        .testTag("send_otp_button")
                ) {
                    Text(text = "Send SMS Code", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "A 6-digit verification code was sent to $phoneInput",
                    fontSize = 13.sp,
                    color = AmberGoldPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text("Enter 6-digit SMS Code") },
                    placeholder = { Text("123456") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_code_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onRegister(phoneInput, nameInput, handleInput)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SignalGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_otp_button")
                ) {
                    Text(text = "Verify & Complete Registration", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
