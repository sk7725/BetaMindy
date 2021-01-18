![Logo](assets/icon-small.png)

[![Java CI with Gradle](https://github.com/sk7725/BetaMindy/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/sk7725/BetaMindy/actions) [![Discord](https://img.shields.io/discord/704355237246402721.svg?logo=discord&logoColor=white&logoWidth=20&labelColor=7289DA&label=Discord)](https://discord.gg/RCCVQFW) [![Language](https://img.shields.io/badge/made%20with-Scratch%202.0-orange)]() [![Stars](https://img.shields.io/github/stars/sk7725/BetaMindy?label=Please%20Star%20Me%21&style=social)]()


# BetaMindy
A java mod for testing and doing chaotic fun.   
혼돈의 카오스의 모드.   
Java мод для тестирования и хаотичного веселья.   
Check planned features/known bugs in the [Trello](https://trello.com/b/AiElGCt1/betamindy) before suggesting contents/improvemts or submitting bugs!   

## Enjoying   
[![Download](https://img.shields.io/github/v/release/sk7725/BetaMindy?color=green&include_prereleases&label=DOWNLOAD%20LATEST%20RELEASE&logo=github&logoColor=green&style=for-the-badge)](https://github.com/sk7725/BetaMindy/releases)
### Releases   
Go to the releases, some may have a `.jar` attached to it that you can download. If it does not have it, follow the steps below(recommended) or bother me with a new issue so I can attach the compiled mod.   
After you have the `.jar`, paste it into your mod folder(locate your mod folder in the "open mod folder" of Mindustry).   

Releases를 클릭하고, 최신 버전에 달린 `.jar`를 다운로드하세요. 원하는 버전에 `.jar`가 첨부되어 있지 않다면, 아래의 과정을 따르거나 새로운 Issue로 `.jar`를 달아달라고 저를 괴롭히세요.   
`.jar`를 다운로드 한 후, 모드 디렉토리(민더스트리에서 모드 파일 열기로 확인 가능)에 옮기세요.   

Перейдите в релизы. К некоторым из них могут быть прикреплены `.jar` файлы, которые необходимо скачать. Если их нет, то следуйте шагам ниже (рекомендуется), или же побеспокойте меня, открыв новое issue, чтобы я прикрепил скомпилированную версию.
После того, как у вас есть `.jar` файл, переместите его в вашу папку с модификациями (найдите её с помощью кнопки "Открыть папку с модификациями" в игре).

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
   
[RU]:
1. Перейдите во вкладку [Actions](https://github.com/sk7725/BetaMindy/actions) и выберите последний рабочий процесс, начинающийся со "Stable".
2. Нажмите на надпись "normal" (со значком коробки). Начнётся загрузка zip архива.
3. Распакуйте и вставьте `BetaMindy.jar` в папку модификаций (найдите её с помощью кнопки "Открыть папку с модификациями" в игре).
4. Наслаждайтесь!
   
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
