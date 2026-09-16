# WatchPoint — Android app

A daily mental-wellness companion for CCJEF (Criminology and Forensic Science)
students. Kotlin + Jetpack Compose, single activity. Onboarding, a full daily
check-in flow, a home dashboard, trends/weekly summaries, an exercise library,
a 5-day starter program, streak goals, a private journal, and reminders all
persist locally and back up to Firebase.

---

## Opening it

1. **Android Studio → File → Open** → select this folder (the one with
   `settings.gradle.kts`).
2. Let Gradle sync.
3. This project is linked to a Firebase project (`watchpoint-da5f3`) via
   `app/google-services.json`. To point it at your own Firebase project,
   create one, enable Email/Password sign-in and Cloud Firestore, and replace
   that file.
4. To build a signed release, copy `keystore.properties.example` to
   `keystore.properties` (gitignored) and fill in your own keystore path and
   passwords, or set the equivalent `WATCHPOINT_STORE_FILE` /
   `WATCHPOINT_STORE_PASSWORD` / `WATCHPOINT_KEY_ALIAS` /
   `WATCHPOINT_KEY_PASSWORD` environment variables. The signing key itself
   (`watchpoint-release.jks`) is intentionally not included in the project.
5. Run on a device or emulator with **API 24 or newer**.

Built against AGP 8.7.3 / Kotlin 2.0.21 / compileSdk 35.

---

## What it's built with

| Tool | Role |
|------|------|
| Kotlin | Application language |
| Jetpack Compose | UI |
| Room | Local persistence (check-ins, journal, onboarding answers, exercise completions, program progress, streak goal, weekly reflections) |
| DataStore | Small device-level preferences (reminder settings, quick-mode default) |
| Firebase Authentication | Email/password accounts |
| Cloud Firestore | Online backup of each account's data, scoped to `users/{uid}/...` |
| Android AlarmManager | Daily check-in reminder and motivational-quote notifications |

Room is the source of truth the UI reads from. `FirestoreSyncManager` pushes
unsynced rows to Firestore whenever the device is online, and downloads the
account's existing backup right after sign-in so a fresh install or a new
device isn't empty. Logging out (or deleting the account) clears the local
Room database so the next person signed in on the same phone can't see the
previous account's data.

---

## Structure

```
app/src/main/java/com/watchpoint/app/
├── MainActivity.kt                  single activity, edge to edge
├── WatchPointApplication.kt         wires AppContainer, starts sync + reminders
├── navigation/
│   ├── Route.kt                     one constant per destination
│   └── WatchPointNavHost.kt         the whole graph + slide transitions
├── auth/                            sign-up / sign-in / profile form state
├── onboarding/                      first-time questions (enums + screens)
├── checkin/                         daily check-in flow, dashboard, exercises,
│                                     journal, reminders, streak goals
├── settings/                        reminder preferences, account actions
└── data/
    ├── AppContainer.kt              manual service locator (no DI framework)
    ├── db/                          Room entities, DAOs, migrations
    ├── prefs/                       DataStore-backed settings
    ├── remote/                      Firebase Auth + Firestore sync
    └── repository/                  one repository per feature area
```

`WpScreen` is the frame nearly every onboarding/check-in screen uses: textured
background, optional back arrow, optional progress indicator, and a bottom
slot for the primary action.

---

## Design tokens

| Token | Hex | Used for |
|-------|-----|----------|
| `Forest` | `#072C07` | base background, under the texture |
| `ForestInk` | `#08210A` | dark surfaces |
| `ForestPanel` | `#0D3A0C` | cards, panels |
| `Orange` | `#E1781C` | the single primary action per screen |
| `Mint` | `#96E882` | answer pills, interest cards, time chip |
| `SelectGreen` | `#5CC94B` | anything currently selected |
| `AccentGreen` | `#8FD07E` | highlighted headline, filled progress dashes |
| `Cream` | `#F1F0EC` | the account sheet |

Rhythm and radii live in `WpSpace` and `WpShape` in `Theme.kt`.

---

## Assets

Wabby (the mascot), the HUD ring, sun, moon and forest texture ship in
`res/drawable`, plus launcher icons at all five densities. The interest icons
are stroked paths drawn on a shared grid in `InterestIcons.kt` rather than
bitmaps, so they stay sharp at any density.

### Fonts

The deck uses **Anton** for the wordmark and **Montserrat** for everything
else. Neither is bundled; the app falls back to the platform sans-serif at
matching weights. To use the real faces, see `app/src/main/res/font_README.md`.

---

## Data and privacy

- `firestore.rules` scopes every read/write to `users/{uid}` - deploy it with
  `firebase deploy --only firestore:rules` after any change.
- The Room database is excluded from Android's automatic cloud backup and
  device-to-device transfer (see `res/xml/backup_rules.xml` and
  `res/xml/data_extraction_rules.xml`) since it's already backed up to
  Firestore under the user's own account.
- Settings → "Delete my account and data" removes the Firestore subtree and
  the Firebase Auth account itself, alongside the local copy.
