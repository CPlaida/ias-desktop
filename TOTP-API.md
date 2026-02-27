# TOTP Backend API (for IAS Desktop)

## QR code in desktop app

The "Setup Two-Factor Authentication" dialog can show a scannable QR code. To enable it, add the ZXing library to the project: see `lib/README.txt`. Without it, the secret is still shown for manual entry.

The desktop app expects the following endpoints from your backend (`http://localhost:8080`) to support TOTP 2FA.

## 1. Session login with TOTP flag

**POST** `/api/sessionLogin`

- Request: `{ "idToken": "<firebase-id-token>" }`
- On success (200): set session cookie as usual. Optionally include in response body:
  ```json
  { "requiresTotp": true }
  ```
- If `requiresTotp` is `true`, the desktop app will show a 6-digit TOTP dialog and then call the verify endpoint.

## 2. Verify TOTP at login

**POST** `/api/totp/verify`

- Request: session cookie (Cookie header) + body `{ "code": "123456" }`
- Response: 200 if the code is valid for the logged-in user; 4xx otherwise.
- After 200, the app considers the user fully logged in and opens the dashboard.

## 3. Setup TOTP (enable 2FA)

**GET** `/api/totp/setup`

- Request: session cookie (user must be logged in).
- Response (200): JSON with the new TOTP secret for this user, e.g.:
  ```json
  { "secret": "JBSWY3DPEHPK3PXP", "issuer": "IAS" }
  ```
- The desktop app shows this secret and the `otpauth://` URI so the user can add the account to Google Authenticator (or similar). Store the secret on your side associated with the user.

## 4. Confirm TOTP setup

**POST** `/api/totp/confirm`

- Request: session cookie + body `{ "code": "123456" }`
- The user enters the first 6-digit code from their app. Your backend should verify the code against the stored secret (TOTP algorithm, 30s step, 6 digits) and then mark 2FA as enabled for that user.
- Response: 200 if the code is valid and 2FA is now enabled; 4xx otherwise.

---

**TOTP algorithm (RFC 6238):** 30-second time step, 6-digit code, HMAC-SHA1. The desktop app uses the same (see `ias.dekstop.TOTP`).
