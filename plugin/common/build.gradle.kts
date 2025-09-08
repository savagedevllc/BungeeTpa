plugins {
    id("com.gradleup.shadow").version("9.1.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.10.1")

    implementation(project(":common"))
}
