import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.palantir.docker") version "0.33.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "com.AlMLand"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}
// for testcontainers
extra["testcontainersVersion"] = "1.17.6"
// for testcontainers
dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.0.0")

    /////////////////////////////////////////
    implementation("org.springframework.boot:spring-boot-starter-aop:3.0.2")
    // implementation("org.aspectj:aspectjrt:1.9.19") // runtimeOnly
    // implementation("org.aspectj:aspectjweaver:1.9.19") // runtimeOnly
    // implementation("org.springframework:spring-aop:6.0.4")
    // implementation("org.springframework:spring-aspects:6.0.4")
    implementation("org.springframework:spring-instrument:6.0.4")
    ////////////////////////////////////////

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // liquibase
    implementation("org.liquibase:liquibase-core:4.17.2")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    // for testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

docker {
    name = "${project.name}:${project.version}"
    setDockerfile(File("/src/main/docker/Dockerfile"))
    files("/build/libs/course-catalog-service-${project.version}.jar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    test {
        java.setSrcDirs(listOf("src/test/integrationtest", "src/test/unittest"))
    }
}