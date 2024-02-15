plugins {
    id("java")
}

group = "net.savagedev"
version = "1.0.0-SNAPSHOT"

dependencies {
    compileOnly("com.google.code.gson:gson:2.10.1")

    implementation(project(":common"))
}