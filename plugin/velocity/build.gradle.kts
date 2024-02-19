plugins {
    id("com.github.johnrengelman.shadow").version("8.1.1")
    id("net.kyori.blossom").version("2.0.0")
}

group = project.parent!!.group
version = project.parent!!.version

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    flatDir {
        dirs("D:\\Java Projects\\SavageDev\\BungeeTpa\\plugin\\velocity\\build\\libs")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.0")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.0")

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

        minimize()
    }
}
