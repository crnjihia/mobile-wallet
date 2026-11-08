# Mobile Wallet Architecture Documentation

## Overview
Mobile Wallet is designed using Clean Architecture principles and Kotlin Multiplatform (KMP), enabling 100% shared domain models, security checks, database persistence, network synchronization, and Compose UI across Android and iOS.

## Layering Strategy
1. **`shared/domain`**: Pure Kotlin interfaces and value objects (`Money` in minor units, `PhoneNumber` formatted to `+254`, `Transfer` state machine). Zero platform or framework dependencies.
2. **`shared/data`**: Implementation of repositories, SQLDelight schema with platform-specific SQLCipher encrypted drivers, and Ktor client HTTP communication.
3. **`shared/security`**: Platform-native security mechanisms via Kotlin `expect/actual`:
   - **Android**: `AndroidKeyStore`, `BiometricPrompt`, `CertificatePinner`, `File` system root checks.
   - **iOS**: `Keychain`, `LAContext` (LocalAuthentication), Darwin `NSURLSession` challenge handling, sandbox write tests.
4. **`composeApp`**: Shared Compose Multiplatform Material 3 design system themed with Kenyan mobile money branding (Safaricom green `#00A859` and M-Pesa accent red `#E21B23`).
5. **`server`**: Standalone JVM Ktor mock backend providing sandbox APIs with latency simulation and `/chaos` failure injection.

## Offline Transaction Synchronization & Idempotency
- When a user sends money in airplane mode or under flaky network conditions, the transfer is immediately persisted into the `outboxEntity` database table with a locally generated `UUID` idempotency key.
- The UI immediately reflects the queued transfer without waiting for network ACK.
- `SyncEngine` monitors connectivity and processes pending outbox rows sequentially using exponential backoff retry with random jitter (`expDelay * jitter`).
- Re-transmitting requests with the same `X-Idempotency-Key` header ensures that even if a server response is lost due to connection dropped after processing, the backend returns the cached transfer record without deducting funds a second time.
