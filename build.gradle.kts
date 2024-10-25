import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.serialization") version "1.8.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("com.google.osdetector") version "1.7.0"
}

group = "com.devooks"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    if (osdetector.arch.equals("aarch_64")) {
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.89.Final:osx-aarch_64")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.0.4")

    // r2dbc
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:3.0.4")
    implementation("org.postgresql:r2dbc-postgresql:1.0.1.RELEASE")
    runtimeOnly("org.postgresql:postgresql")

    // feign client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // validation
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")

    // jwt
    val jwtVersion = "0.11.5"
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    // pdf
    implementation("org.apache.pdfbox:pdfbox:2.0.27")

    // test
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // test container
    val testContainerVersion = "1.19.4"
    testImplementation("org.testcontainers:testcontainers:$testContainerVersion")
    testImplementation("org.testcontainers:r2dbc:$testContainerVersion")
    testImplementation("org.testcontainers:postgresql:$testContainerVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainerVersion")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")

    // json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC")
}

val springCloudVersion by extra("2023.0.0")
// feign client
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("copyJar", Copy::class) {
    dependsOn("bootJar")
    val jarFile = "devooks-$version.jar"
    from("build/libs")
    into(file("docker"))
    include(jarFile)
}

tasks.named("build") {
    dependsOn("copyJar")
}
