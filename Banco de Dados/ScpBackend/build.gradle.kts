plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "scp.backend"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.hivemq:hivemq-mqtt-client:1.3.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    implementation("com.google.code.gson:gson:2.13.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "scp.backend.Main"
    }
}

tasks.build {
    dependsOn("shadowJar")
}