plugins {
    id("com.gradleup.shadow").version("9.1.0")
    id("net.kyori.blossom").version("2.0.1")
}

group = project.parent!!.group
version = project.parent!!.version

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation("org.bstats:bstats-velocity:3.0.2")

    implementation(project(":plugin:common"))
    implementation(project(":common"))
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.name}-${project.version}.jar")

        relocate("org.bstats", "net.savagedev.tpa.metrics")

        relocate("redis.clients", "net.savagedev.tpa.redis")
        relocate("com.rabbitmq", "net.savagedev.tpa.rabbitmq")

        minimize()
    }
}
