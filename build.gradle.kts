plugins {
    id("java")
}

group = "io.bota5ky"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.1.39.Final")
    implementation("org.projectlombok:lombok:1.16.18")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.google.guava:guava:19.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
