dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation(project(":common:event"))
    implementation(project(":common:serialization"))
    implementation(project(":common:pagination"))
}
