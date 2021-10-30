plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "ac.paladin"
version = "1.0"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")
}

tasks.processResources {
    filteringCharset = "UTF-8"

    val tokens = mapOf("VERSION" to version)
    inputs.properties(tokens)

    from("src/main/resources") {
        duplicatesStrategy = DuplicatesStrategy.WARN
        filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokens)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveFileName.set("AutoPaladin.jar")

    relocate("co.aikar.commands", "ac.paladin.auto.shadow.acf")
    relocate("co.aikar.locales", "ac.paladin.auto.shadow.locales")
    relocate("retrofit2", "ac.paladin.auto.shadow.retrofit2")
    relocate("okhttp3", "ac.paladin.auto.shadow.okhttp3")
    relocate("com.fasterxml", "ac.paladin.auto.shadow.com.fasterxml")
    relocate("okio", "ac.paladin.auto.shadow.okio")
}

