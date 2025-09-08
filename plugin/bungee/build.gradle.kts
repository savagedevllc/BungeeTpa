plugins {
    id("com.gradleup.shadow").version("9.1.0")
}

group = project.parent?.group!!
version = project.parent?.version!!

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.21-R0.3")

    implementation("org.bstats:bstats-bungeecord:3.0.2")
    implementation(project(":plugin:common"))
    implementation(project(":common"))
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.name}-${project.version}.jar")

        relocate("org.bstats", "net.savagedev.tpa.metrics")

        relocate("redis.clients", "net.savagedev.tpa.redis")
        relocate("com.rabbitmq", "net.savagedev.tpa.rabbitmq")

        minimize()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            expand(Pair("version", project.version))
                .include("bungee.yml")
        }
    }
}
