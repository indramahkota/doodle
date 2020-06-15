apply (from = "../jacoco.gradle")

plugins {
    kotlin("multiplatform")
}

kotlin {
    jsTargets ()
    jvmTargets()

    val mockkVersion  : String by project
    val junitVersion  : String by project
    val log4jVersion  : String by project
    val logbackVersion: String by project
    val mockkJsVersion: String by project

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                api(project(":core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation("junit:junit:$junitVersion")
                implementation(kotlin("test-junit"))

                implementation("org.slf4j:slf4j-api:$log4jVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.mockk:mockk-js:$mockkJsVersion")
            }
        }
    }
}