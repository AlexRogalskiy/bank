plugins {
    idea
    java
}

apply(plugin = "io.freefair.lombok")

val jacksonVersion = "2.5.1"
val myBatisVersion = "3.5.1"
val myBatisGuiceVersion = "3.10"
val slf4jVersion = "1.7.26"
val googleInjectGuiceVersion = "4.2.2"
val h2Version = "1.4.199"
val transactionApiVersion = "1.3"
val sparkCoreVersion = "2.8.0"
val junitVersion = "4.12"

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
    implementation("com.sparkjava:spark-core:$sparkCoreVersion")
    implementation("javax.transaction:javax.transaction-api:$transactionApiVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.google.inject:guice:$googleInjectGuiceVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.mybatis:mybatis-guice:$myBatisGuiceVersion")
    implementation("org.mybatis:mybatis:$myBatisVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("junit:junit:$junitVersion")
}

tasks.wrapper {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.ALL
}