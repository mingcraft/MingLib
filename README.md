# Docs
- [Javadoc](https://mingcraft.github.io/MingLib/)

# Gradle
```java
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/mingcraft/minglib")
        credentials {
            username = "username"
            password = "token"
        }
    }
}
```
```java
dependencies {
        compileOnly 'com.mingcraft:minglib:1.0.24'
        }
```