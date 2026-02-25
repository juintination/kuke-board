plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	kotlin("plugin.jpa") version "2.2.21"

	id("org.springframework.boot") version "4.0.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "kuke"
version = "0.0.1"
description = "Large-scale system design to learn by making your own with Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

// 모든 모듈 공통 리포지토리
allprojects {
	repositories {
		mavenCentral()
	}
}

// 실제 공통 설정
subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
		compilerOptions {
			freeCompilerArgs.addAll(
				"-Xjsr305=strict",
				"-Xannotation-default-target=param-property",
			)
		}
	}
}