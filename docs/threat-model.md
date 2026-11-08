# Threat Model (STRIDE) - Mobile Wallet Prototype

This document outlines the security posture and threat analysis for the Mobile Wallet application following the STRIDE methodology.

---

## 1. Spoofing (Identity & Gateway Impersonation)
- **Threat**: Man-in-the-Middle (MITM) attacker intercepts HTTPS traffic using a rogue CA certificate or proxy tool (e.g. Charles Proxy, Burp Suite).
- **Impact**: Attacker can read sensitive transaction payloads or inject fraudulent transfer responses.
- **Mitigation**:
  - `CertificatePinningConfigurator` enforces public key hash pinning on Android (`CertificatePinner`) and iOS (`NSURLSession` challenge evaluation).
  - Configured with primary pin and backup rotation pin.
  - Local HTTP debug overrides are explicitly restricted to development builds.

---

## 2. Tampering (Data Modification at Rest & Transit)
- **Threat**: Physical theft of device or malware reading SQLite database files to alter transaction balances or extract historical transfers.
- **Impact**: Unauthorized balance modification or financial record tampering.
- **Mitigation**:
  - Database encrypted at rest using **SQLCipher** (AES-256-CBC).
  - Database passphrase generated using CSPRNG and wrapped via **AndroidKeyStore** (AES/GCM) on Android and **Keychain** on iOS.
  - Plaintext SQLite drivers are completely excluded from production builds.

---

## 3. Repudiation (Transaction Denial & Duplicate Execution)
- **Threat**: Network connection drops after server processes payment but before client receives response, leading to user retrying payment and getting charged twice.
- **Impact**: Double debit of sender's wallet balance.
- **Mitigation**:
  - Client generates a cryptographic UUID `idempotencyKey` at time of transfer creation in local outbox.
  - Server maintains an `idempotencyCache` in `InMemoryStore`. Identical idempotency keys return the exact original `TransferResponse` with status `SUCCEEDED` without re-executing balance deductions.

---

## 4. Information Disclosure (Data Leakage)
- **Threat**: Sensitive PII (phone numbers, account balances, PINs) written to system logs (logcat / console).
- **Impact**: Privacy violation and compliance breach under Kenya Data Protection Act (KDPA).
- **Mitigation**:
  - Strict zero-PII logging policy.
  - Loggers use `Napier` with production filters stripping sensitive user identifiers and amounts.

---

## 5. Denial of Service (Network Flooding & Flaky Retries)
- **Threat**: Flaky cellular network causing thousands of rapid retry attempts when network reconnects, overloading backend servers.
- **Impact**: Backend gateway crash or battery drain.
- **Mitigation**:
  - `SyncEngine` implements exponential backoff (`baseDelay * 2^retryCount`) capped at 30 seconds.
  - Random jitter multiplier (`0.8` to `1.2`) applied to break retry synchronization.

---

## 6. Elevation of Privilege (Device Compromise)
- **Threat**: App executed on rooted Android device (Magisk/Su) or jailbroken iOS device (Cydia).
- **Impact**: Memory inspection, hook injection (Frida), or key extraction.
- **Mitigation**:
  - `IntegrityChecker` inspects filesystem for `su` binaries, `test-keys` build tags, Cydia paths, dyld libraries, and sandbox write permissions.
  - Configurable `IntegrityPolicy` allows restricting high-risk transaction execution on compromised devices.
