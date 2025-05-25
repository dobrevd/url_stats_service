plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.dobrev"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    implementation(platform("software.amazon.awssdk:bom:2.31.47"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.configureEach {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("org.apache.logging.log4j", "log4j-to-slf4j")
    exclude("commons-logging", "commons-logging")
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}