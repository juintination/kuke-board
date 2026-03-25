dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation(project(":common:jpa"))
    implementation(project(":common:pagination"))
    implementation(project(":common:event"))
    implementation(project(":common:outbox"))

    testImplementation(platform("io.kotest:kotest-bom:5.9.1"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-framework-engine")
    testImplementation("io.mockk:mockk:1.13.10")
}

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}
