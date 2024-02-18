package com.example.keystore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.intent.and.android_keystore_philipp_lackner.CryptoManager
import com.example.intent.and.android_keystore_philipp_lackner.ui.theme.Android_KeyStore_Philipp_LacknerTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// KeyStore System - TEE (Trusted Execution Environment) - specific hardware
// separated from the Android OS.
// Even attacker has a root access to Android, he does not have root access to TEE.
// Keystore TEE can't prevent attacker with root access to use our keys, but prevents
// from extracting keys outside Android system.
// App flow:
// 1. Enter text, click encrypt:
// 1.1. Encrypted value is shown on screen under buttons.
// 1.2. Encrypted value is saved as file secret.txt in app's internal storage -
// see Device Explorer: path: /data/data/com.example.keystore/files/secret.txt
// 2. Click decrypt: See original text decrypted in text field.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cryptoManager = CryptoManager()
        setContent {
            Android_KeyStore_Philipp_LacknerTheme {
                val messageToEncrypt = remember {
                    mutableStateOf("")
                }
                val messageToDecrypt = remember {
                    mutableStateOf("")
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CryptoUi(
                        cryptoManager = cryptoManager,
                        messageToEncrypt = messageToEncrypt,
                        messageToDecrypt = messageToDecrypt,
                        messageToEncryptChanged = { message ->
                            messageToEncrypt.value = message
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoUi(
    cryptoManager: CryptoManager,
    messageToEncrypt: MutableState<String>,
    messageToDecrypt: MutableState<String>,
    modifier: Modifier = Modifier,
    messageToEncryptChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    Column {
        TextField(
            value = messageToEncrypt.value,
            onValueChange = { value ->
                messageToEncryptChanged(value)
            },
            modifier = modifier,
            placeholder = { Text(text = "Enter encrypt string") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = {
                val bytes = messageToEncrypt.value.encodeToByteArray()
                val file = File(context.filesDir, "secret.txt")
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file)

                messageToDecrypt.value =
                    cryptoManager.encrypt(
                        bytes = bytes,
                        outpuStream = fos
                    ).decodeToString()
                messageToEncrypt.value = ""
            }) {
                Text(text = "Encrypt")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val file = File(context.filesDir, "secret.txt")
                messageToEncrypt.value = cryptoManager.decrypt(
                    inputStream = FileInputStream(file)
                ).decodeToString()
            }) {
                Text(text = "Decrypt")
            }
        }
        Text(text = messageToDecrypt.value)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android_KeyStore_Philipp_LacknerTheme {
        val cryptoManager = CryptoManager()
        CryptoUi(
            cryptoManager = CryptoManager(),
            messageToEncrypt = remember {
                mutableStateOf("Preview encrypt message")
            },
            messageToDecrypt = remember {
                mutableStateOf("Preview decrypt message")
            },
        )
    }
}