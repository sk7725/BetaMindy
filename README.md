# Mindustry Mod Template
A blank template for Mindustry mods with an existing Gradle configuration to compile and dex-ify.

## Compiling
JDK 8.

### Windows
Plain Jar: `gradlew build`\
Dexed Jar: `gradlew buildDex`

### *nix
Plain Jar: `./gradlew build`\
Dexed Jar: `./gradlew buildDex`

Plain Jar is for JVMs (desktop).\
Dexed Jar is for ARTs (Android). This requires `dx` on your path (Android build-tools).\
These two are separate in order to decrease size of mod download.
