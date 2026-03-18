pluginManagement{
    repositories{
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://raw.githubusercontent.com/GglLfr/EntityAnnoMaven/main")
    }

    plugins{
        val entVersion: String by settings
        id("com.github.GglLfr.EntityAnno") version(entVersion)
    }

}
if(JavaVersion.current().ordinal < JavaVersion.VERSION_17.ordinal) throw GradleException("JDK 17 is a required minimum version. Yours: ${System.getProperty("java.version")}")


val modName: String by settings
rootProject.name = modName