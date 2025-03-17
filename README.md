# GracefulShutdown Plugin for Ktor

"Graceful shutdown" is a term for shutting down a process once it is complete any ongoing work. 


This plugin accomplishes this by tracking all active requests Ktor is fulfilling and delaying the shutdown of Ktor until all active requests have been completed (or the specified timeout has elapsed).

This can be useful in an environment where you are doing rolling deployments with 0 downtime.

### Basic Setup

```kotlin
import dev.hayden.GracefulShutdown

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(GracefulShutdown)
        
        // Your routes and other configurations
    }.start(wait = true)
}
```

### Advanced Configuration

The plugin offers several configuration options:

```kotlin
install(GracefulShutdown) {
    // Maximum time to wait for requests to complete (default: 30 seconds)
    shutdownTimeout = 45.seconds
    
    // Enable in development mode (default: false)
    enableInDevelopmentMode = true
    
    // Add 'Connection: close' header to new requests during shutdown (default: true)
    enableCloseConnectionHeader = true
}
```

## Installation
This package uses Jitpack as its repository. This means you will need to add a custom repository for
Jitpack if you don't already have it.

For Maven:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
  <groupId>dev.hayden</groupId>
  <artifactId>graceful-shutdown</artifactId>
  <version>1.0.1</version>
</dependency>
```

For gradle:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
```groovy
dependencies {
  implementation 'dev.hayden:graceful-shutdown:1.0.1'
}
```

For additional build systems check out: https://jitpack.io/#dev.hayden/graceful-shutdown
