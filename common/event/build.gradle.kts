dependencies {
    implementation(project(":common:serialization"))
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
}

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}
