# Fonts

The layout uses two Google Fonts:

* **Anton** - the "WatchPoint" wordmark (heavy condensed display face)
* **Montserrat Bold / SemiBold** - headings and body copy

They are not bundled here because of licence packaging, but both are free
(SIL Open Font Licence). To use the real faces:

1. Download from fonts.google.com: `Anton-Regular.ttf`,
   `Montserrat-SemiBold.ttf`, `Montserrat-Bold.ttf`, `Montserrat-Medium.ttf`.
2. Rename to lowercase with underscores and drop them in this folder:
   `anton_regular.ttf`, `montserrat_semibold.ttf`, `montserrat_bold.ttf`,
   `montserrat_medium.ttf`.
3. Open `ui/theme/Type.kt` and uncomment the two `FontFamily(...)` blocks
   marked `REAL FONTS`, then delete the fallback lines above them.

Until then the app falls back to the platform sans-serif at matching weights,
so nothing breaks - it just looks slightly less like the mockup.
