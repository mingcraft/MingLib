# Docs
- [Javadoc](https://mingcraft.github.io/MingLib/)

# Gradle
```java
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/mingcraft/minglib")
        credentials {
            username = env.GPR_USER.value
            password = env.GPR_KEY.value
        }
    }
}
```
```java
dependencies {
    compileOnly 'com.mingcraft:minglib:1.0.6'
}
```
