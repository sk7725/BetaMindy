# Mindustry Mod Template
A blank template for Mindustry mods with an existing Gradle configuration to compile and dex-ify, and Github Actions to automatically do both and upload the artifacts.

## Compiling
JDK 8.\
Task `dexify` requires `d8` from Android `build-tools` > `28.0.1`.

Plain Jar is for JVMs (desktop).\
Dexed Jar is for for JVMs (desktop) and ARTs (Android).\
These two are separate in order to decrease size of mod download.

### Windows
Plain Jar: `gradlew build`\
Dexify Plain Jar: `gradlew dexify`\
Build Plain & Dexify Jar: `gradlew buildDex`

### *nix
Plain Jar: `./gradlew build`\
Dexify Plain Jar: `./gradlew dexify`\
Build Plain & Dexify Jar: `./gradlew buildDex`

## I have no idea what I'm doing with this

1. Rename directories in `src/` to avoid collisions with other mods that didn't.
2. Change `pGroup` in `gradle.properties` to match up with the new directories.
3. Update `main` in `assets/mod.json` to line up with renamed directories.
4. Refer to [Compiling](#compiling).
