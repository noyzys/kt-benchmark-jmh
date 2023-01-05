import org.jetbrains.kotlin.com.google.common.reflect.ClassPath
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("kapt") version "1.7.21"
    kotlin("plugin.allopen") version "1.7.21"
    id("me.champeau.jmh") version "0.6.8"
    id("io.morethan.jmhreport") version "0.9.0"
    application
}

group = "dev.noyzys.jmh.setup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javaVersion = 17
val jmhVersion = "1.35"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.openjdk.jmh:jmh-core:$jmhVersion")
    implementation("org.openjdk.jmh:jmh-generator-bytecode:$jmhVersion")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")
    //implementation(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.apply {
        release.set(javaVersion)
        encoding = "UTF-8"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = javaVersion.toString()
    }
}

allOpen {
    val jmhAnnotationClasses = ClassPath.from(this::class.java.classLoader)
        .getTopLevelClassesRecursive("org.openjdk.jmh.annotations")
        .map { it.load() }
        .filter { it.isAnnotation }
        .map { it.canonicalName }

    annotations(jmhAnnotationClasses)
}

application {
    mainClass.set("dev.noyzys.jmh.setup.MainKt")
}

// Enables the usage of Java classes inside 'kotlin' package
sourceSets {
    val javaMain = "src/main/java"
    val kotlinMain = "src/main/kotlin"
    val allSourceMain = listOf(javaMain, kotlinMain)

    main {
        listOf(java, kotlin).forEach {
            it.setSrcDirs(allSourceMain)
        }
    }
}

jmh {
    val jmh = tasks.getByName("jmh")
    jmh.finalizedBy(tasks.jmhReport)

    val resultPath = file("${project.buildDir}/results/jmh")
    humanOutputFile.set(resultPath.resolve("humanResults.txt"))
    resultsFile.set(resultPath.resolve("results.json"))
    resultFormat.set("JSON")  // Result format type (one of NONE, TEXT, JSON, CSV, SCSV)

    jmh.doLast {
        println(humanOutputFile.get().asFile.readText())
    }
}

jmhReport {
    val jmhResultFile = tasks.jmh.get().resultsFile.get().asFile
    val outputFolder = jmhResultFile.parentFile.resolve("html").path
    jmhResultPath = jmhResultFile.path
    jmhReportOutput = outputFolder

    val jmhReport = tasks.getByName("jmhReport")
    jmhReport.doFirst {
        file(outputFolder).mkdirs()
    }
}
