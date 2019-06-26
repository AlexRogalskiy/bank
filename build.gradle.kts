plugins {
    idea
    java
}

apply(plugin = "io.freefair.lombok")

tasks.jar {
    archiveBaseName.set("charges")
    manifest {
        attributes["Main-Class"] = "com.charges.ChargesApp"
    }
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

buildscript {
    dependencies {
        classpath("io.freefair.gradle:lombok-plugin:3.2.0")
    }
}

repositories {
    mavenCentral()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("com.sparkjava:spark-core:2.8.0")
    implementation("org.mybatis:mybatis:3.5.1")
    implementation("javax.transaction:javax.transaction-api:1.3")
    implementation("com.h2database:h2:1.4.199")

    testImplementation("junit:junit:4.12")
}

tasks.wrapper {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.ALL
}