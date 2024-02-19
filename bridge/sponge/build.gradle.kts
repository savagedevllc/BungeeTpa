import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow").version("8.1.1")
    id("org.spongepowered.gradle.plugin").version("2.0.2")
}

group = project.parent?.group!!
version = project.parent?.version!!

dependencies {
    implementation("org.bstats:bstats-sponge:3.0.2")
    implementation(project(":bridge:common"))
    implementation(project(":common"))
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.parent?.name}-${project.name}-${project.version}.jar")

        relocate("org.bstats", "net.savagedev.tpa.metrics")

        minimize()
    }

    sponge {
        apiVersion("8.1.0")
        license("MIT")
        loader {
            name(PluginLoaders.JAVA_PLAIN)
            version("1.0")
        }
        plugin("bungeetpasponge") {
            displayName("${rootProject.name}SpongeBridge")
            entrypoint("net.savagedev.tpa.sponge.BungeeTpSpongePlugin")
            description("Teleport across a BungeeCord network.")
            dependency("spongeapi") {
                loadOrder(PluginDependency.LoadOrder.AFTER)
                optional(false)
            }
        }
    }
}

