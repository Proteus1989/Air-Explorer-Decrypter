[![Build Status](https://travis-ci.org/Proteus1989/Air-Explorer-Decrypter.svg?branch=master)](https://travis-ci.org/Proteus1989/Air-Explorer-Decrypter)
[![Codecov](https://img.shields.io/codecov/c/github/Proteus1989/Air-Explorer-Decrypter)](https://codecov.io/gh/Proteus1989/Air-Explorer-Decrypter)
[![GitHub](https://img.shields.io/github/license/Proteus1989/Air-Explorer-Decrypter)](https://github.com/Proteus1989/Air-Explorer-Decrypter/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/Proteus1989/Air-Explorer-Decrypter)](https://github.com/Proteus1989/Air-Explorer-Decrypter/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.proteus1989/AirExplorerDecrypter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.proteus1989%22%20AND%20a:%22AirExplorerDecrypter%22)
[![Github All Releases](https://img.shields.io/github/downloads/Proteus1989/Air-Explorer-Decrypter/total)](https://github.com/Proteus1989/Air-Explorer-Decrypter/releases)

# Air Explorer Decrypter
Java API able to decrypts Air Explorer encrypted files. This project includes simple graphic client binaries.


## Getting Started

### Prerequisites

At least, Oracle JRE 1.8 is needed.

### GUI Client
To run a GUI client just execute **AirExplorerFileDecrypter-v1.1.jar** file.
[Download latest GUI client build](https://github.com/Proteus1989/Air-Explorer-Decrypter/releases/latest)

### API Usage
#### Importing the library
##### Maven
```
<dependency>
  <groupId>com.github.proteus1989</groupId>
  <artifactId>AirExplorerDecrypter</artifactId>
  <version>1.0.1</version>
</dependency>
```
##### Gradle
```
implementation 'com.github.proteus1989:AirExplorerDecrypter:1.0.1'
```
#### Calling the API
- Decrypting file name
```java
AirExplorerDecrypterAPI.decryptName("encryptedFileName(.cloudencoded2)", "file_password")
```
- Decrypting a file
```java
AirExplorerDecrypterAPI.decrytp(new File("file_path"), "file_password")
```
```java
AirExplorerDecrypterAPI.decrytp(new File("file_path"), new File("dst_folder"), "file_password")
```
```java
AirExplorerDecrypterAPI.decrytp(your_input_stream, your_output_stream, "file_password")
```
[Download latest API build](https://github.com/Proteus1989/Air-Explorer-Decrypter/releases/latest)

## Authors

* **Antonio Suárez** - *Initial work* - [Proteus1989](https://github.com/Proteus1989)

See also the list of [contributors](https://github.com/Proteus1989/Air-Explorer-Decrypter/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

MS original PasswordDeriveBytes class contains a nonstandard extension of the PBKDF1 algorithm. Therefore, MS PasswordDeriveBytes is different of normal BKDF1.
Special thanks to **gilchris** for Java PasswordDeriveBytes port. Available at https://github.com/gilchris/PasswordDeriveBytesForJava.
