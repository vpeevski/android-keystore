# android-keystore
From Philipp Lackner video tutorial

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
