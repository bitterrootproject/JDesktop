@file:Suppress("SpellCheckingInspection")

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "2.0.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "4.1.1"
    id("io.freefair.lombok") version "9.5.0"  // Data classes
}

group = "org.bitterrootproject"
version = "1.0.0"


var globalAppName = "JDesktop"
var globalMainModule = "org.bitterrootproject.jdesktop"
var globalMainClassFQ = "org.bitterrootproject.jdesktop.AppMain"


repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"
//val javafxVersion = providers.gradleProperty("org.openjfx.javafx.version")
//val javafxVersion = "21"
//val javafxClassifier = findMavenPlatformClassifier().get()
//val javafxModules = setOf("base", "graphics", "controls", "fxml")
//
//fun findMavenPlatformClassifier(): Provider<String> {
//    // Avoid internal API and use JVM system properties
//    return providers.systemProperty("os.name").zip(providers.systemProperty("os.arch")) { os, arch ->
//        val osPart = if (os.contains("windows", true)) "windows"
//        else if (os.contains("mac", true)) "mac"
//        else "linux"
//        if (arch.contains("aarch64", true)) "$osPart-aarch64"
//        else osPart
//    }


val osName = System.getProperty("os.name").lowercase()

enum class OsType {
    WINDOWS, LINUX, MAC, UNIX, OTHER
}

val osType: OsType = when {
    "windows" in osName -> OsType.WINDOWS
    "linux" in osName -> OsType.LINUX
    "mac" in osName -> OsType.MAC
    listOf("unix", "sunos", "solaris", "bsd").any { it in osName } -> OsType.UNIX
    else -> OsType.OTHER
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    modularity.inferModulePath = true
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    applicationName = globalAppName
    mainModule = globalMainModule
    mainClass = globalMainClassFQ
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("net.synedra:validatorfx:0.5.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    // ORMLite
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("org.xerial:sqlite-jdbc:3.53.4.0")

    // Logging
    var log4j2Version = "2.26.1"
    implementation(platform("org.apache.logging.log4j:log4j-bom:${log4j2Version}"))
    implementation("org.apache.logging.log4j:log4j-api:${log4j2Version}")

    implementation("org.apache.logging.log4j:log4j-core")
    runtimeOnly("org.apache.logging.log4j:log4j-layout-template-json")

    // SLF4J-to-Log4j2 wrapper (if needed)
    // runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")

    // Used to get the right folders on each OS
    implementation("net.harawata:appdirs:1.5.0")


    // Extra helpful stuff
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("org.jetbrains:annotations:26.1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    // jlink and jpackage need some extra help to detect slf4j
    forceMerge("log4j-api")

    // Need to explicitly add these so jlink works
    mergedModule {
        requires("java.logging")
        requires("java.sql")
        requires("java.desktop")
        requires("java.datatransfer")
        requires("java.sql.rowset")
        provides("org.apache.logging.log4j.util.PropertySource")
            .with(
                "org.apache.logging.log4j.util.EnvironmentPropertySource",
                "org.apache.logging.log4j.util.SystemPropertiesPropertySource"
            )

        uses("org.apache.logging.log4j.spi.Provider")
        uses("org.apache.logging.log4j.util.PropertySource")
        uses("java.sql.DriverManager")
    }

    launcher {
        name = globalAppName
    }

    jpackage {
        imageName = globalAppName
//        imageOptions = [
//            "--icon", "src/main/resources/org/bitterrootproject/jdesktop/icon.icns",
////            "--name", "JDesktop"
//        ]
        mainClass = globalMainClassFQ
        icon = when {
            osType == OsType.MAC -> "src/main/resources/org/bitterrootproject/jdesktop/mac-icon.icns"
            else -> ""
        }
//        description = "Bitterroot Project JDesktop"
//        vendor = "Bitterroot Project"
    }
}
