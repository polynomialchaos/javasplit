# JavaSplit

A Java package for money pool split development.

## Prerequisites

* Java OpenJDK-17 on Ubuntu 20.04

```sh
sudo apt-get install openjdk-17-jdk openjdk-17-demo openjdk-17-doc openjdk-17-jre-headless openjdk-17-source
```

* Gradle (VERSION: https://services.gradle.org/distributions/)

```sh
wget https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip -P /tmp
sudo unzip -d /opt/gradle /tmp/gradle-${VERSION}-bin.zip
sudo ln -s /opt/gradle/gradle-${VERSION} /opt/gradle/latest
```

```sh
sudo vim /etc/profile.d/gradle.sh
>> export GRADLE_HOME=/opt/gradle/latest
>> export PATH=${GRADLE_HOME}/bin:${PATH}
```

```sh
sudo chmod +x /etc/profile.d/gradle.sh
```

## Installation

To generate an executable of the **JavaSplit** package simply call

```sh
gradle build
```

## Usage

The **JavaSplit** application either loads an existing case from the specified file (JSON format) or generates a new one if no file is provided.

```sh
java -jar build/libs/javasplit.jar
```

The user hase the options to

* add exchange rate(s) if the command line option **-e** or **--exchange** is provided, or to

* add member(s) if the command line option **-m** or **--member** is provided, or to

* add purchase(s) if the command line option **-p** or **--purchase** is provided, or to

* add transfer(s) if the command line option **-t** or **--transfer** is provided.

## Output

The **JavaSplit** stores the defined group information, members, pruchases and transfers in a JSON format file.

## External libraries

* com.google.code.gson (v. 2.9.0)
* info.picocli (v. 4.6.3)
* org.junit.jupiter (v. 5.8.2)