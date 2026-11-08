# ADR 0001: Kotlin Multiplatform Architecture and SQLCipher Encryption at Rest

## Context
Mobile Wallet requires a local database for offline transaction queuing (outbox) and local caching of account state. Because the application handles financial transaction data, storing SQLite data in plaintext presents a security vulnerability if the device is stolen or compromised.

## Decision
We adopt **SQLDelight** with **SQLCipher** encrypted drivers across Android and iOS.

### Key Management Strategy
1. The database key is generated randomly on first startup using `SecureRandom` / platform CSPRNG (256-bit entropy).
2. The raw key is wrapped/encrypted before storage:
   - **Android**: Uses `AndroidKeyStore` with `AES/GCM/NoPadding` master key to wrap the DB passphrase in `SharedPreferences`.
   - **iOS**: Stores the wrapped key inside iOS `Keychain` configured with `kSecAccessControlBiometryAny` or `kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`.
3. The raw passphrase is held only in ephemeral memory while the application database connection is open and wiped on app lock.

## Platform Integration Steps

### Android SQLCipher Setup
1. Include `net.zetetic:android-database-sqlcipher:4.5.4` in `androidMain`.
2. Initialize native binaries: `SQLiteDatabase.loadLibs(context)`.
3. Pass `SupportFactory(passphrase)` to `AndroidSqliteDriver`.

### iOS SQLCipher Setup
1. Configure `app.cash.sqldelight:native-driver`.
2. Configure `DatabaseConfiguration.Encryption(key = passphraseString)` in `NativeSqliteDriver`.

## MANUAL STEPs & Verification
- **Android Manual Verification**: Run `adb shell` to attempt reading `/data/data/com.chris.mobilewallet/databases/wallet_encrypted.db` using plain `sqlite3`. Verify that it returns `file is not a database` or `file is encrypted`.
- **iOS Manual Verification**: Inspect the simulator container path with `sqlite3` without providing the PRAGMA key. Verify query returns `file is encrypted`.
