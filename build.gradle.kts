@file:Suppress("SpellCheckingInspection")

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "2.0.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
//    id("org.beryx.jlink") version "2.25.0"
    id("org.beryx.jlink") version "4.1.1"
    id("io.freefair.lombok") version "9.5.0"  // Data classes
//    id("de.infolektuell.jpackage") version "0.4.1"
}

group = "org.bitterrootproject"
version = "1.0.0"

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
    applicationName = "JDesktop"
//    mainClass = "org.example.App"
    mainModule = "org.bitterrootproject.jdesktop"
//    mainClass.set("org.bitterrootproject.jdesktop.HelloApplication")
//    mainClass.set("org.bitterrootproject.jdesktop.DatabaseManager")
    mainClass = if (project.hasProperty("mainClass")) project.property("mainClass").toString() else "org.bitterrootproject.jdesktop.GuiApplication"
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
    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("org.slf4j:slf4j-simple:2.0.18")

    implementation("org.apache.commons:commons-lang3:3.20.0")
//    implementation("org.slf4j:slf4j-nop:2.0.18")
//    implementation("org.apache.logging.log4j:log4j-api:2.26.1")
//    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.26.1")

//    implementation("org.tinylog:tinylog-api:2.7.0")
//    implementation("org.tinylog:tinylog-impl:2.7.0")

//    javafxModules.forEach { implementation("org.openjfx:javafx-${it}:${javafxVersion}:${javafxClassifier}") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    // jlink and jpackage need some extra help to detect slf4j
    forceMerge("slf4j")

    launcher {
        name = "JDesktop"
    }

    jpackage {
        imageName = "JDesktop"
//        imageOptions = [
//            "--icon", "src/main/resources/org/bitterrootproject/jdesktop/icon.icns",
////            "--name", "JDesktop"
//        ]
        mainClass = "org.bitterrootproject.jdesktop.GuiApplication"
        icon = "src/main/resources/org/bitterrootproject/jdesktop/icon.icns"
//        description = "Bitterroot Project JDesktop"
//        vendor = "Bitterroot Project"
    }
}
