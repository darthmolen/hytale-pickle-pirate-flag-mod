# Gradle Automation & Testing Plan

## Overview

Implement automated build, deployment, and server testing for the Pickle Pirate Flag mod based on the [official Hytale modding documentation](https://britakee-studios.gitbook.io/hytale-modding-documentation/plugins-java-development/13-gradle-automation-testing).

## Goals

1. Single-command build and test workflow
2. Automatic server download/caching
3. Hot-reload during development
4. CI/CD ready configuration

## Implementation Steps

### Phase 1: Custom Gradle Plugin Setup

#### 1.1 Create buildSrc Directory
```
pickle_pirate_flag_mod/
├── buildSrc/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── RunHytalePlugin.kt
```

#### 1.2 Configure buildSrc/build.gradle.kts
```kotlin
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
```

#### 1.3 Implement RunHytalePlugin.kt
Create custom Gradle plugin with:
- Server JAR download with SHA-256 caching
- Isolated `run/` directory for server
- Interactive console forwarding
- Graceful shutdown handling
- Automatic `shadowJar` dependency

### Phase 2: Build Configuration

#### 2.1 Update build.gradle.kts
```kotlin
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
    id("run-hytale")
}

runHytale {
    // Server JAR URL (from hytale-downloader or official source)
    jarUrl = file("libs/HytaleServer.jar").absolutePath

    // Pack directory to deploy
    packDir = file("pack")

    // Server directory
    serverDir = file("run/server")
}
```

#### 2.2 Configure gradle.properties
```properties
org.gradle.daemon=true
org.gradle.jvmargs=-Xmx4G -XX:+UseG1GC
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
```

### Phase 3: Development Tasks

#### 3.1 runServer Task
- Downloads/caches server JAR
- Builds plugin JAR via shadowJar
- Copies plugin to mods folder
- Deploys pack to packs folder
- Starts server with interactive console

#### 3.2 deployPack Task
- Copies pack folder to server packs directory
- Syncs manifest.json, Common/, Server/, Client/

#### 3.3 watchAndBuild Task
- File watcher on src/main/java/**
- Auto-rebuilds on changes
- Notifies developer of completion

#### 3.4 quickTest Task
- Compiles without full build
- Validates manifest.json exists
- Scans for common issues (System.out.println)

### Phase 4: Multi-Version Testing

#### 4.1 Version Tasks
```kotlin
// Test against different server versions
tasks.register("runLatest") { /* ... */ }
tasks.register("runStable") { /* ... */ }
tasks.register("runBeta") { /* ... */ }
```

### Phase 5: CI/CD Integration

#### 5.1 GitHub Actions Workflow
```yaml
name: Build and Test
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '25'
      - name: Build
        run: ./gradlew shadowJar
      - name: Archive artifacts
        uses: actions/upload-artifact@v4
        with:
          name: plugin-jar
          path: build/libs/*.jar
```

### Phase 6: Debug Support

#### 6.1 Remote Debugging
```kotlin
// Enable debug mode with -Pdebug flag
val debugMode = project.hasProperty("debug")
if (debugMode) {
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
}
```

## File Structure After Implementation

```
pickle_pirate_flag_mod/
├── buildSrc/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── RunHytalePlugin.kt
├── build.gradle.kts
├── gradle.properties
├── settings.gradle
├── libs/
│   └── HytaleServer.jar
├── pack/
│   ├── manifest.json
│   ├── Common/
│   ├── Server/
│   └── Client/
├── src/main/java/
│   └── dev/smolen/pickleflag/
├── run/                        # Created by tasks
│   └── server/
│       ├── HytaleServer.jar
│       ├── mods/
│       └── packs/
├── .github/
│   └── workflows/
│       └── build.yml
└── .gitignore
```

## Commands After Implementation

| Command | Description |
|---------|-------------|
| `./gradlew shadowJar` | Build plugin JAR only |
| `./gradlew runServer` | Build and start server with plugin |
| `./gradlew deployPack` | Deploy pack without rebuilding |
| `./gradlew watchAndBuild` | Watch for changes and auto-rebuild |
| `./gradlew quickTest` | Fast validation check |
| `./gradlew runServer -Pdebug` | Start with remote debugging |

## Best Practices

### .gitignore Updates
```
build/
run/
.gradle/
*.jar
!gradle-wrapper.jar
```

### Dependencies
- `compileOnly` for server SDK (HytaleServer.jar)
- `implementation` for bundled libraries
- `testImplementation` for test frameworks

## Success Criteria

1. `./gradlew runServer` starts server with mod loaded
2. Changes to Java files trigger rebuild notification
3. Pack changes sync automatically to server
4. Server console accepts commands interactively
5. CI builds and produces artifact

## References

- [Gradle Automation & Testing - Hytale Docs](https://britakee-studios.gitbook.io/hytale-modding-documentation/plugins-java-development/13-gradle-automation-testing)
- [Plugin Project Template](https://github.com/realBritakee/hytale-template-plugin)
