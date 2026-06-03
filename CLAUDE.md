# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

KuaiKuai is a JetBrains IDE plugin that displays the 乖乖 snack image — a lucky charm in Taiwanese programmer culture. The plugin shows the image in a side tool window anchored to the right.

## Build & Development Commands

All commands use the Gradle wrapper. Always use JDK 21 at `/Users/cytsai/Library/Java/JavaVirtualMachines/temurin-21.0.11/Contents/Home` and set `JAVA_HOME` accordingly.

```bash
./gradlew buildPlugin          # Build the plugin ZIP (output: build/distributions/)
./gradlew check                # Run tests
./gradlew verifyPlugin         # Run IntelliJ Plugin Verifier
./gradlew runIde               # Launch a sandbox IDE instance with the plugin loaded
```

Delete `build/idea-sandbox/` to reset the sandbox IDE state (e.g. to apply `preferredSize` changes).

The `.run/` directory contains pre-configured IntelliJ run configurations: **Run Plugin**, **Run Tests**, and **Run Verifications**.

## Architecture

- **`plugin.xml`** (`src/main/resources/META-INF/`) — registers the tool window (id `KuaiKuai`, `anchor="right"`), startup activity, and project service.
- **`MyToolWindowFactory`** (`toolWindow/MyToolWindowFactory.kt`) — creates a `KuaiKuaiPanel` and registers it as the tool window content.
- **`KuaiKuaiPanel`** — a `JPanel` that draws the snack image via `paintComponent`, scaling it to fill available space while maintaining aspect ratio with 12px padding. Image is loaded from `/images/kuaikuai.png` on the classpath.
- **`MyProjectService`** (`services/MyProjectService.kt`) — minimal project-scoped service, currently a stub.
- **`MyProjectActivity`** (`startup/MyProjectActivity.kt`) — minimal startup hook, currently a stub.

## Image Asset

Located at `src/main/resources/images/kuaikuai.png` (470×596, trimmed of transparent borders). Must be PNG — WebP is not supported by Java's `ImageIO`.

## Key Conventions

- Plugin targets IntelliJ IDEA `2025.2.6.2` (set in `build.gradle.kts`), plugin ID: `com.github.cytsai1008.kuaikuai`.
- Releases are driven by `CHANGELOG.md` (Keep a Changelog format); CI reads it to auto-generate release notes.
