# WatchPoint — Android front end

A local, front-end-only Android app built from the `[WatchPoint] APP LAYOUT` deck.
Kotlin + Jetpack Compose, single activity, no backend and no network calls.
Every one of the 15 screens in the deck is implemented, plus a closing summary
screen so the flow can be demonstrated end to end.

---

## Opening it

1. **Android Studio → File → Open** → select this folder (the one with
   `settings.gradle.kts`).
2. Let Gradle sync. If Studio reports a missing Gradle wrapper, accept its offer
   to generate one, or run `gradle wrapper --gradle-version 8.9` from the project
   root. `gradle/wrapper/gradle-wrapper.properties` is already set up; only the
   binary `gradle-wrapper.jar` is absent, because it can't be distributed as
   source.
3. Run on a device or emulator with **API 24 or newer**.

Built against AGP 8.7.3 / Kotlin 2.0.21 / compileSdk 35. If your Studio is newer
and prompts for an AGP upgrade, accepting it is safe.

---

## The flow

| # | Screen | Route | Notes |
|---|--------|-------|-------|
| 1 | Welcome | `welcome` | Wordmark, Wabby, cream sheet |
| 2 | Loading | `loading` | Auto-advances after 1.6 s, pops itself off the back stack |
| 3 | Wabby greeting | `greeting` | |
| 4 | Sign in | `auth` | Apple / Google / Email — all three just advance |
| 5 | Quote | `quote` | |
| 6 | How did you first join? | `source` | Next appears only once an option is picked (deck pages 6→7) |
| 7 | How have you been lately? | `mood` | Tapping an answer advances immediately — no Next in the deck |
| 8 | So glad to hear that | `reassurance` | |
| 9 | Final step | `final_step` | |
| 10 | Wake time | `wake_time` | step 1/5, opens a time picker |
| 11 | Bedtime | `bed_time` | step 2/5 |
| 12 | Interests | `interests` | step 3/5, multi-select 3×3 grid |
| 13 | Support system | `support` | step 4/5 |
| 14 | Age group | `age` | step 5/5 |
| 15 | Summary | `summary` | Not in the deck — reads back every answer |

---

## Structure

```
app/src/main/java/com/watchpoint/app/
├── MainActivity.kt                  single activity, edge to edge
├── navigation/
│   ├── Route.kt                     one constant per destination
│   └── WatchPointNavHost.kt         the whole graph + slide transitions
├── onboarding/
│   ├── OnboardingViewModel.kt       answers + the enums they come from
│   └── screens/
│       ├── IntroScreens.kt          deck 1–5
│       ├── QuestionScreens.kt       deck 6–10
│       ├── PersonalizeScreens.kt    deck 11–15
│       └── SummaryScreen.kt         closing screen
└── ui/
    ├── theme/                       Color.kt, Type.kt, Theme.kt
    └── components/
        ├── Scaffold.kt              ForestBackground, WpScreen, ProgressDashes
        ├── Controls.kt              PrimaryButton, AnswerPill, RadioRow, TimeChip
        ├── Mascot.kt                MascotWithRing, Wordmark, QuestionTitle
        └── InterestIcons.kt         the nine practice icons, drawn on Canvas
```

`WpScreen` is the frame nearly every screen uses: textured background, optional
back arrow, optional 5-dash progress indicator, and a bottom slot for the orange
action. Adding a screen usually means writing a composable, wrapping it in
`WpScreen`, and adding two lines to `WatchPointNavHost`.

---

## Design tokens

Sampled from the deck rather than guessed:

| Token | Hex | Used for |
|-------|-----|----------|
| `Forest` | `#072C07` | base background, under the texture |
| `ForestInk` | `#08210A` | "Continue with Apple" |
| `ForestPanel` | `#0D3A0C` | "Continue with Google", summary card |
| `Orange` | `#E1781C` | the single primary action per screen |
| `Mint` | `#96E882` | answer pills, interest cards, time chip |
| `SelectGreen` | `#5CC94B` | anything currently selected |
| `AccentGreen` | `#8FD07E` | highlighted headline, filled progress dashes |
| `Cream` | `#F1F0EC` | the account sheet |

Rhythm and radii live in `WpSpace` and `WpShape` in `Theme.kt`, so spacing
changes happen in one place.

---

## Assets

The Wabby renders, HUD ring, sun, moon and forest texture were extracted from
the source PDF with their transparency intact, so the app matches the mockup
rather than approximating it. Twelve mascot poses ship in `res/drawable`,
roughly 4 MB total, plus launcher icons at all five densities.

The nine interest icons are **not** bitmaps — they're stroked paths drawn on a
shared 100×100 grid in `InterestIcons.kt`. They stay sharp at any density and
pick up the tint of whatever card they sit on.

### Fonts

The deck uses **Anton** for the wordmark and **Montserrat** for everything else.
Neither is bundled. The app falls back to the platform sans-serif at matching
weights, so it builds and runs as-is. To use the real faces, follow
`app/src/main/res/font/README.md` — it's a four-file drop-in and a two-line edit
in `Type.kt`.

### Brand marks

`ic_apple` and `ic_google` are placeholders lifted from the layout. Before
shipping, replace them with the official assets from Apple's *Sign in with Apple*
and Google's *Sign in with Google* branding guidelines — both companies require
their own artwork on those buttons.

---

## What's deliberately not here

This is the front end only, so:

- **No persistence.** `OnboardingViewModel` holds answers in memory and they're
  gone when the process dies. Swapping it for a DataStore- or Room-backed
  repository is the obvious next move and touches only that one file.
- **No authentication.** All three buttons on the sign-in screen call the same
  `onAuthenticated` callback. Wire your provider in `WatchPointNavHost`.
- **No home screen.** The flow ends at the summary. That's the seam where the
  actual product — journaling, breathing exercises, mood tracking — begins.

## Copy changes

Four typos in the deck were corrected in `strings.xml`: *daailly* → daily,
*So glad to here that* → hear, *TouTube* → YouTube, *dificult* → difficult.
Revert them in `strings.xml` if the originals were intentional.
