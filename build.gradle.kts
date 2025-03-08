import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.serialization") version "1.8.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("com.google.osdetector") version "1.7.0"
    id("org.jooq.jooq-codegen-gradle") version "3.19.14"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    id("pl.allegro.tech.build.axion-release") version "1.18.17"
}

scmVersion {
    tag { prefix.set("") }
    versionCreator { tag, _ -> tag }
    snapshotCreator { _, _ -> "" }
}

group = "com.devooks"
version = scmVersion.version

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val jwtVersion = "0.11.5"
val testContainerVersion = "1.19.4"
val jooqVersion = "3.19.4"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    if (osdetector.arch.equals("aarch_64")) {
        implementation("io.netty:netty-resolver-dns-native-macos:4.1.89.Final:osx-aarch_64")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // r2dbc
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:3.0.4")
    implementation("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
    implementation("org.postgresql:postgresql:42.7.4")

    // feign client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jwt
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

    // email
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // jooq
    implementation("org.jooq:jooq:${jooqVersion}")
    implementation("org.jooq:jooq-kotlin:${jooqVersion}")
    // workaround of issue: https://github.com/etiennestuder/gradle-jooq-plugin/issues/209
    jooqCodegen("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    jooqCodegen("org.jooq:jooq-meta-extensions:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-meta-kotlin:${jooqVersion}")
    // workaround of array type codegen, see: https://github.com/jOOQ/jOOQ/issues/13322
    jooqCodegen("com.h2database:h2:2.3.232")

    // flyway
    implementation("org.flywaydb:flyway-core:10.22.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.22.0")
}

jooq {
    version = "$jooqVersion"  // the default (can be omitted)
    configuration { }

    executions {
        create("main") {  // name of the jOOQ configuration
            //generateSchemaSourceOnCompilation =true   // default (can be omitted)

            configuration {
                logging = org.jooq.meta.jaxb.Logging.DEBUG
                jdbc = null // only required for gen from active databases.

                generator {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase" // gen from ddl schema.

                        // commoutted out this, see: https://github.com/etiennestuder/gradle-jooq-plugin/issues/222
                        // inputSchema = "public"
                        properties {

                            // Specify the location of your SQL script.
                            // You may use ant-style file matching, e.g. /path/**/to/*.sql
                            //
                            // Where:
                            // - ** matches any directory subtree
                            // - * matches any number of characters in a directory / file name
                            // - ? matches a single character in a directory / file name
                            property {
                                key = "scripts"
                                value = "src/main/resources/db/jooq/schema.sql"
                            }

                            // The sort order of the scripts within a directory, where:
                            //
                            // - semantic: sorts versions, e.g. v-3.10.0 is after v-3.9.0 (default)
                            // - alphanumeric: sorts strings, e.g. v-3.10.0 is before v-3.9.0
                            // - flyway: sorts files the same way as flyway does
                            // - none: doesn't sort directory contents after fetching them from the directory
                            property {
                                key = "sort"
                                value = "semantic"
                            }

                            // The default schema for unqualified objects:
                            //
                            // - public: all unqualified objects are located in the PUBLIC (upper case) schema
                            // - none: all unqualified objects are located in the default schema (default)
                            //
                            // This configuration can be overridden with the schema mapping feature
                            property {
                                key = "unqualifiedSchema"
                                value = "none"
                            }

                            // The default name case for unquoted objects:
                            //
                            // - as_is: unquoted object names are kept unquoted
                            // - upper: unquoted object names are turned into upper case (most databases)
                            // - lower: unquoted object names are turned into lower case (e.g. PostgreSQL)
                            property {
                                key = "defaultNameCase"
                                value = "lower"
                            }
                        }
                    }
                    generate {
                        isPojosAsKotlinDataClasses = true // use data classes
                        // Allowing to turn off the feature for to-many join paths (including many-to-many).
                        // The default is true.
                        // see: https://stackoverflow.com/questions/77677549/new-jooq-gradle-plugin-can-not-process-self-reference-relation-correctly/77677816#77677816
                        isImplicitJoinPathsToMany = false
                    }
                    target {
                        packageName = "com.devooks.backend.jooq"

                        // can not resolve relative path, use
                        // basedir = "${projectDir}"
                        // or append `${projectDir}` to the beginning of the relative path.
                        // see: https://github.com/jOOQ/jOOQ/issues/15944
                        directory = "${projectDir}/build/generated/jooq"  // default (can be omitted)
                    }
                    strategy {
                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    }
                }
            }
        }
    }
}

val springCloudVersion by extra("2023.0.0")
// feign client
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}


tasks.withType<KotlinCompile> {
    dependsOn("jooqCodegenMain")
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

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs")
}

tasks.register<Exec>("publishTypeNpm") {
    dependsOn("test")
    val npmrcPassword = project.findProperty("password")?.toString()
        ?: throw GradleException("Please provide a password using -Ppassword=<value>")
    commandLine("./openapi-generate.sh", "-version", scmVersion.version, "-password", npmrcPassword)
}
