# ADR 0002: Certificate Pinning and Network Security Policy

## Context
Mobile Wallet communicates with external financial API gateways and the Ktor mock server. To protect against Man-in-the-Middle (MITM) attacks and rogue CA certificate injection, strict certificate pinning must be implemented.

## Decision
1. **Primary & Backup Pin Strategy**: Always configure both a primary SHA-256 public key hash and a backup pin (rotation pin) to prevent complete app outage when server certificates expire or rotate.
2. **Platform Implementation**:
   - **Android**: Enforced via OkHttp `CertificatePinner` with host matching `api.chris-mobilewallet.co.ke`.
   - **iOS**: Enforced via Darwin engine challenge handling.
3. **Debug Override**: Controlled via `PinConfig.allowDebugOverride = true` during local development with the HTTP JVM mock server (`10.0.2.2:8080` / `localhost:8080`).

## MANUAL STEP Verification Command
To test certificate pinning manually against an invalid pin:
```bash
# Override pin to invalid hash 'sha256/INVALID_HASH=' and observe SSLHandshakeException / CertificatePinner exception:
./gradlew :shared:test --tests "com.chris.mobilewallet.data.CertificatePinningTest"
```
