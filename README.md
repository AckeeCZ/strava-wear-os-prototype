# Strava Wear OS

## Project

Native Wear OS showcase app built for the Strava RFP. The RFP covers activity
metrics, segments, and maps & navigation; **this codebase implements the maps
slice only** — a list of routes fetched from a mocked Strava API and tapping a
route to render its polyline on Google Maps for Wear OS.

### Maps API key

Google Maps SDK for Wear OS requires an API key. For the showcase the key is
**intentionally committed in plaintext** inside
[`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) (the `com.google.android.geo.API_KEY`
`<meta-data>` element) so the project builds and runs out of the box without any
local setup.

### Emulator / device

Run target is a Wear OS 4+ image (API 30+). Google Maps requires a Wear OS
image **with Google Play Services**.

## Tech stack

The app is written in Kotlin using Jetpack Compose for Wear OS. It follows MVI on
top of Android architecture components (`ViewModel`, `StateFlow`) and a layered
domain / data / presentation split per feature. Koin is used for dependency injection.
Remote data is currently mocked from bundled JSON fixtures whose DTO shapes mirror
the real Strava REST API. Coroutines and Flow are used for asynchronous operations and
for communicating between the ViewModel and View layers.

Some of the important libraries used on the project are:

* Android Architecture components for app architecture
* Jetpack Compose for Wear OS (`androidx.wear.compose:compose-material3`) for UI
* Navigation 3 (`androidx.navigation3` + `androidx.wear.compose:compose-navigation3`) for navigation
* Google Maps SDK for Wear OS (`play-services-maps`) + `android-maps-utils` for polyline decoding
* Coroutines & Flow for asynchronous and reactive operations
* Koin for DI
* `kotlinx.serialization` for parsing the mocked Strava JSON fixtures

Versions are pinned in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Build and run

The Gradle wrapper is the best option to build the project.

```bash
./gradlew :app:assembleDebug          # debug APK
./gradlew :app:installDebug           # install on a connected Wear OS device / emulator
./gradlew :app:lint                   # lint (strict — lint.xml is in :app)
```

## Module / package layout

Single Gradle module (`:app`), **feature-first** package layout. Shared
infrastructure lives under `core/`, every feature lives under `feature/<name>/`
with its own `domain/`, `data/`, `presentation/` subpackages. This mirrors the
multi-module feature convention used on larger Ackee projects, collapsed into
packages because the showcase ships as a single module.

Rules of the road:

- `core/` is shared infrastructure consumed by every feature. No feature-specific code lives here.
- Each feature lives in `feature/<name>/` and owns its full `domain/`, `data/`,
  `presentation/` stack plus its own Koin module and nav destinations. No central `nav/` package —
  the feature owns its destinations.
- `domain/` holds the canonical models and repository interfaces. No `@Serializable`, no DTOs.
- `data/` owns DTOs and JSON parsing. DTOs map to domain via a `Mapper`.
- `presentation/` owns ViewModels, Compose screens and the per-feature nav destination.
  Never imports `data/` directly.
- Screens are **stateless composables** receiving `State` + lambdas; the ViewModel owns
  mutable state via `StateFlow<State>`.
- User actions flow through a single `fun onIntent(intent: <Feature>Intent)` entry point on
  the ViewModel (MVI). The screen takes `onIntent: (<Feature>Intent) -> Unit` and dispatches
  concrete `<Feature>Intent` data objects/classes.
- One-shot side effects (navigation, snackbars) flow through a `SharedFlow<Event>` collected
  by the screen with `LaunchedEffect`.
- IDs are value classes: `@JvmInline value class Id(val value: Long)` on the domain model.

## DI

We use Koin for dependency injection. Each feature exposes a `<feature>Module = module { ... }`
and all modules are aggregated in `core/di/AppModule.kt`, started from
`StravaApplication.onCreate`. ViewModels use the explicit-lambda form so wiring is visible:

Inside Compose: `koinViewModel<T>()` / `koinViewModel { parametersOf(id) }`.

## Strava API mocking

Mocked, but DTO shapes **must exactly match the real Strava REST API** so the
mock can be swapped for a Ktor client later without touching domain or UI.

References:

- [GET /routes/{id}](https://developers.strava.com/docs/reference/#api-Routes-getRouteById)
- [Playground](https://developers.strava.com/playground/) — useful for capturing real example responses

Conventions:

- One DTO file per resource (`RouteDto.kt`), one `@Serializable data class` per
  Strava resource. Names mirror Strava's docs verbatim.
- Field naming: Kotlin camelCase + `@SerialName("snake_case")` to keep Kotlin idiom
  on our side and a faithful wire format that survives the swap.
- Mock payloads are bundled as JSON under `app/src/main/assets/routes/` (one file per
  route + an `index.json` for the list). Parsed once at app start — no hand-written
  Kotlin literal DTOs.
- Polylines in Strava responses are Google encoded polylines (`map.summary_polyline`).
  They are decoded with `PolyUtil.decode` at the data-layer boundary; the UI only ever
  sees decoded `List<LatLng>`.

## Theme

The Strava brand does not map cleanly onto Material3 roles, so we define our own
design system in `core/presentation/theme/` instead of customising
`androidx.wear.compose.material3.MaterialTheme`.

## Static analysis

For static analysis of the Kotlin source code [detekt](https://github.com/detekt/detekt) is used.
You can run it locally by running

```
./gradlew detekt --rerun-tasks
```

in the root folder. The default ruleset is applied on top of project-specific overrides
in [`detekt-config.yml`](detekt-config.yml). The `detekt-formatting` and `detekt-rules-compose`
plugins are pulled in so Compose-specific rules (stable parameters, hoisted state, …) are
checked alongside the default formatting rules.
