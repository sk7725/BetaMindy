![Logo](assets/icon-small.png)

[![Java CI with Gradle](https://github.com/sk7725/BetaMindy/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/sk7725/BetaMindy/actions) [![Discord](https://img.shields.io/discord/704355237246402721.svg?logo=discord&logoColor=white&logoWidth=20&labelColor=7289DA&label=Discord)](https://discord.gg/RCCVQFW) [![Language](https://img.shields.io/badge/made%20with-Scratch%202.0-orange)]() [![Stars](https://img.shields.io/github/stars/sk7725/BetaMindy?label=Please%20Star%20Me%21&style=social)]()


# BetaMindy
A java mod for testing and doing chaotic fun.   
혼돈의 카오스의 모드.   

## Enjoying
### Releases   
Go to the releases, some may have a `.jar` attached to it that you can download. If it does not have it, follow the steps below(recommended) or bother me with a new issue so I can attach the compiled mod.   
After you have the `.jar`, paste it into your mod folder(locate your mod folder in the "open mod folder" of Mindustry).   

Releases를 클릭하고, 최신 버전에 달린 `.jar`를 다운로드하세요. 원하는 버전에 `.jar`가 첨부되어 있지 않다면, 아래의 과정을 따르거나 새로운 Issue로 `.jar`를 달아달라고 저를 괴롭히세요.   
`.jar`를 다운로드 한 후, 모드 디렉토리(민더스트리에서 모드 파일 열기로 확인 가능)에 옮기세요.   

### Actions   
[EN]:
1. Go to [Actions](https://github.com/sk7725/BetaMindy/actions), and click on the latest workflow, starting with the name "Stable".   
2. Select the "normal" Artifact (with the box icon), it will download the zip.   
3. Unzip and paste the `BetaMindy.jar` into your mod folder(locate your mod folder in the "open mod folder" of Mindustry).   
4. Enjoy!   
   
[KO]:
1. [Actions](https://github.com/sk7725/BetaMindy/actions) 탭으로 가서, "Stable"로 시작하는 가장 최근의 Workflow를 클릭하세요.   
2. "normal"이라는 이름의 Artifact(상자 모양 아이콘)을 클릭하면, 압축 파일이 다운로드됩니다.   
3. 압축 해제 후 `BetaMindy.jar`를 모드 디렉토리(민더스트리에서 모드 파일 열기로 확인 가능)에 옮기세요.   
4. 끝!   

## Compiling
JDK 8.

### Windows
Plain Jar: `gradlew build`\
Dexify Plain Jar: `gradlew dexify`\
Build Plain & Dexify Jar: `gradlew buildDex`

### *nix
Plain Jar: `./gradlew build`\
Dexify Plain Jar: `./gradlew dexify`\
Build Plain & Dexify Jar: `./gradlew buildDex`

Plain Jar is for JVMs (desktop).\
Dexed Jar is for ARTs (Android). This requires `dx` on your path (Android build-tools).\
These two are separate in order to decrease size of mod download.
