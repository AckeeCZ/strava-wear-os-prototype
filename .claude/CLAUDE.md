# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Native Wear OS showcase app for the Strava RFP. **Only the "maps" slice of the RFP is implemented**: list mocked routes → tap one → render its polyline on Google Maps for Wear OS, with starred-segment highlights and a per-segment map. Single Gradle module (`:app`) targeting Wear OS 4+ (minSdk 30).

The Google Maps API key is intentionally committed in `app/src/main/AndroidManifest.xml` so the project builds without local setup.

## Build & verification

```bash
./gradlew :app:assembleDebug          # debug APK
./gradlew :app:installDebug           # install on connected Wear OS device/emulator
./gradlew :app:lint                   # Android lint (strict — lint.xml in :app)
./gradlew detekt --rerun-tasks        # detekt + detekt-formatting + detekt-rules-compose
./gradlew :app:testDebugUnitTest      # JUnit unit tests (src/test)
./gradlew :app:testDebugUnitTest --tests "cz.ackee.strava.wearos.feature.routes.data.mapper.RouteMapperTest"
```

Unit tests live under `app/src/test/`. The `test` source set is wired to read `src/main/assets` as resources (see `app/build.gradle.kts`), so tests can load the same mocked JSON the app does.

## Architecture

**Feature-first packaging, single module.** `core/` is shared infrastructure; every feature lives at `feature/<name>/` with its own `domain/`, `data/`, `presentation/` subpackages and owns its Koin module + nav destinations. Mirrors the multi-module convention used on larger Ackee projects, collapsed into packages for the showcase.

Layering rules (enforced by convention, not by build):
- `domain/` holds the canonical models and repository interfaces — no `@Serializable`, no DTOs.
- `data/` owns DTOs, JSON parsing, polyline decoding. Maps to domain via a `Mapper`.
- `presentation/` owns ViewModels, Compose screens, and per-feature nav destinations. Never imports `data/`.
- IDs are value classes on the domain model: `@JvmInline value class Id(val value: Long)`.

**MVI on top of `ViewModel` + `StateFlow`:**
- Screens are stateless `@Composable`s taking `State` + `onIntent: (Intent) -> Unit` lambdas.
- ViewModel exposes `StateFlow<State>` and a single `fun onIntent(intent: <Feature>Intent)` entry point.
- One-shot side effects (navigation, snackbars) go through a `SharedFlow<Event>` collected with `LaunchedEffect`.

**Navigation: AndroidX Navigation 3** (not Nav 2). All destinations are wired in `core/presentation/navigation/StravaNavDisplay.kt` using `NavDisplay` + `entryProvider`. Each feature defines its own `@Serializable data class ...Destination : NavKey`. Wear swipe-to-dismiss is handled by `rememberSwipeDismissableSceneStrategy<NavKey>()`. Back-stack mutations (`backStack.add(...)`, `backStack.removeLastOrNull()`) happen directly in the entry lambdas.

**DI:** Koin. Each feature defines `val <feature>Module = module { ... }`; `core/di/AppModule.kt` aggregates via `includes(...)` and is started from `StravaApplication.onCreate`. ViewModels use the explicit-lambda DSL (`viewModel { (id: Route.Id) -> ... }`) and are obtained in Compose with `koinViewModel { parametersOf(id) }`.

**Strava API mocking:** DTO shapes **must match the real Strava REST wire format exactly** (see the `RouteDto` references in README) so the mock can be swapped for a Ktor client later without touching domain or UI.
- One DTO file per resource, `@Serializable data class`, names mirror Strava docs verbatim.
- Field naming: Kotlin camelCase + `@SerialName("snake_case")` on the wire side.
- Mock payloads bundled under `app/src/main/assets/routes/` (one file per route + `index.json`). Parsed once at app start and cached.
- Strava `summary_polyline` is a Google-encoded polyline string — decoded with `PolyUtil.decode` at the **data-layer boundary** (`RouteMapper`/`LatLngSerializer`). The UI only sees decoded `List<LatLng>`.

**Maps on Wear OS:** Google Maps SDK (`play-services-maps`) wrapped in a Compose-friendly `WearMap` composable under `core/presentation/map/`. `MapsInitializer.initialize(..., Renderer.LATEST)` is called in `StravaApplication.onCreate`. Polyline drawing is split from camera fitting (see `PolylineDrawing.kt`).

**Theme:** Strava brand doesn't map cleanly to Material3 roles, so a custom design system lives in `core/presentation/theme/` (Colors, Typography, Theme). Do **not** customize `androidx.wear.compose.material3.MaterialTheme` — extend the local theme objects instead. The full design language (palette, typography scale, spacing tokens, component rules) is documented in `DESIGN.md` at the repo root.

## Adding a new feature

1. New package `feature/<name>/` with `domain/`, `data/`, `presentation/` subpackages.
2. Define domain models (with value-class IDs), repository interface in `domain/repository/`, use cases in `domain/usecase/`.
3. Implement DTOs in `data/dto/` (Strava-faithful), mapper in `data/mapper/`, repo impl in `data/repository/` (mock loads from `assets/`).
4. Add `<Feature>State`, `<Feature>Intent`, `<Feature>ViewModel`, `<Feature>Screen`, `@Serializable <Feature>Destination : NavKey` under `presentation/<name>/`.
5. Expose `val <feature>Module = module { ... }` at `feature/<name>/<Feature>Module.kt` and add it to `core/di/AppModule.kt` via `includes(...)`.
6. Wire the destination into `StravaNavDisplay.kt`.

## Git workflow

- One commit = one logical change. Theme + first screen = separate commits.
- Imperative subject, no body, no `Co-Authored-By`.
- Every commit must compile and the app must launch (no in-progress refactors
  in the middle of an unrelated commit).
- Feature co-location: `XScreen.kt` + `XScreenState.kt` + `XViewModel.kt` ship
  in one commit when they form one logical unit.

## Detekt overrides

`detekt-config.yml` carries a few project-specific exemptions worth knowing before adding annotations:
- `@Preview`, `@WearPreviewDevices`, `@WearPreviewFontScales` are exempt from `UnusedPrivateMember`.
- `@TestDataFactory` is exempt from `MagicNumber`.
- `@Composable` functions are exempt from `FunctionNaming` (so PascalCase composables don't trip the rule); test source sets are excluded entirely.
