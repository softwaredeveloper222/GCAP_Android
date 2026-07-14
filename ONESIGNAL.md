# OneSignal / Firebase (Android)

## Current config

| Item | Value |
|------|--------|
| OneSignal App ID | `3acc100c-877e-49d4-9a51-d65fa4e77c86` |
| Firebase project | `gcap-fc9fd` |
| FCM Sender ID | `402368112352` |
| Package | `com.gcap` |
| `google-services.json` | `app/google-services.json` |
| SDK | `com.onesignal:OneSignal:[5.6.1, 5.9.99]` |

## App behavior

1. **`GcapApplication`** — VERBOSE logs + `initWithContext` + click listener (no permission here).
2. **`MainActivity`** (after splash) — `requestPermission(true)` on the main Activity, then `pushSubscription.optIn()`.

## Debug subscription

```bash
adb logcat -s GcapOneSignal OneSignal:V
```

Look for:
- `OneSignal initWithContext appId=3acc100c-...`
- `Notification permission accepted=true`
- `pushSubscription id=... token=... optedIn=true`

## Checklist if still "0 subscribers"

1. **Uninstall** the app, then reinstall (clears a bad first-prompt state).
2. Use a device/emulator with **Google Play services** + internet.
3. Tap **Allow** on the notification dialog.
4. OneSignal dashboard → confirm **Google Android (FCM)** uses the Firebase service account for `gcap-fc9fd`, Sender ID `402368112352`.
5. Refresh Audience → Subscriptions after granting permission.
