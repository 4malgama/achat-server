plugins {
    id("java")
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
    compileOnly("org.projectlombok:lombok:1.18.24")
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

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}