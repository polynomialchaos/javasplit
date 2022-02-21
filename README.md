# JavaSplit

A Java package for money pool split development.

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

* add member(s) if the command line option **-m** or **--member** is provided, or to

* add purchase(s) if the command line option **-p** or **--purchase** is provided, or to

* add transfer(s) if the command line option **-t** or **--transfer** is provided.

## Output

The **JavaSplit** stores the defined group information, members, pruchases and transfers in a JSON format file.

## External libraries

* com.google.code.gson (v. 2.9.0)
* info.picocli (v. 4.6.3)
* org.junit.jupiter (v. 5.8.2)