plugins {
    kotlin("jvm") version "1.8.20"
    application
}

application {
    mainClass.set("amalgama.Main")
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "org.amalgama.Main"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

group = "org.amalgama"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api-parent:4.0.1")
    implementation("org.apache.logging.log4j:log4j-api:2.22.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.22.0")
    implementation("io.netty:netty:3.10.6.Final")
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("org.hibernate:hibernate-core:6.2.7.Final")
    implementation("org.postgresql:postgresql:42.6.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    runtimeOnly("org.apache.logging.log4j:log4j-core")
}

rootProject.tasks.named("processResources", Copy::class.java) {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<ProcessResources>() {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Copy>() {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        resources {
            srcDirs ("src/main/resources")
        }
    }
}