plugins {
    id("fabric-loom") version "1.10.5"
    id("java")
}

// Projekt-Properties aus gradle.properties
val baseGroup: String by project
val version: String by project
val modid: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_api_version: String by project

group = baseGroup

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    modImplementation("com.terraformersmc:modmenu:11.0.0")
}

loom {
    runs {
        named("client") {
            property("mixin.debug", "true")
        }
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java"))
            // Nur Fabric-Klassen kompilieren (alte Forge-Klassen portiert aber nicht entfernt)
            setIncludes(listOf("namelessju/scathapro/fabric/**"))
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
}

