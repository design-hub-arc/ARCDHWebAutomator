# American River College Design Hub Web Automator

An automation program used to speed up the American River College business systems.

## Project Purpose

American River College is a community college, and as such, it does not want to spend the money it has on upgrading its business systems.
On such system is an ancient PeopleSoft website, lacking in many quality-of-life features which would make it pleasant to use.
In order to extract any useful budgetary information, the user would have to submit several queries, remember the results, and input them into other forms.
This process is needlessly tedious, and so this project was created to automate much of the process.

## Getting Started

### Required Installations
To use this project, you need Java installed on your computer. You can check if Java is installed by attempting to run the project JAR file (see the section "Running the program"), or you can open your terminal, and type
```
java -version
```
if your computer can find java, you're good! Otherwise, you can download it [here](https://www.java.com/en/).

### Downloading and Installing

You can find the latest release on the "releases" page, [here](https://github.com/design-hub-arc/ARCDHWebAutomator/releases).
You can either download the entire project, or just Launcher.jar, both of which can handle installation.

To install the program, you will need to run Launcher.jar. If you downloaded the entire project, it is located under
```
Launcher/build/libs/Launcher.jar
```
Double click the Launcher.jar, and it will begin installing the latest version of the program.
On Windows computers, it will create a batch file on your desktop,
```
ARCDHWebAutomator.bat
```

### Where does the program install itself?
The program writes all its files to
```
USER_HOME/ARCDH/WebAutomator
```
Where USER_HOME is the user's home folder which is usually
```
C:\Users\NAME
```
for Windows,
```
/Users/NAME
```
for Mac,
```
/usr/NAME
```
for Linux.

Note that the program will automatically regenerate these folders under ARCDH if they are absent, so you needn't worry if they are deleted.

This folder contains the program's executable files (bin), output logs (logs), and saved web-drivers (webdrivers).


### Running the program (for users)

There are a handful of different ways you can run the program, but the most convenient way is by double-clicking
the batch file the program created when you ran the Launcher.jar file.

If you accidently delete the batch file, you can run the Launcher.jar file under the WebAutomator's bin folder 
(see 'where does the program install itself to?')

### Troubleshooting
The program writes log files to the 'logs' folder it creates.
If the program doesn't seem to work, you can email Matt the latest log file
in this folder, and he'll see what he can do.

### How to edit the application (for developers)

You will need Netbeans IDE version 8.2 with the Gradle plugin installed to run the project.
Obviously, Java is needed to allow the project to run. You can use
```
gradle build
```
from the ARCDHWebAutomator directory to rebuild the JAR file for the project.
Note that if the command is run from any of the sub-projects, Gradle will be unable to resolve the path
to sibling projects, so the build will fail.

## Project structure
The project is divided into 4 projects:
- ARCDHWebAutomator:
    the root project, used to hold the sub-projects.
    Gradle tasks should be run from this folder instead of folders of sub-projects.
- Application:
    the primary application. This project handles the GUI and web automation. Generally speaking,
    this is the project developers will work in.
- Launcher:
    Since JAR files cannot be updated when they are running, the Launcher.jar file allows us to have the launcher update
    the other JAR files, then have the main application update the launcher. Generally speaking, developers will not have
    to change or view this project.
- Shared:
    This contains classes used by the other sub-projects. You can include
    ```
    dependencies {
        compile project(':Shared')
    }
    ```
    in the build.gradle file of a project to use classes from this project. Note that you will
    likely have to reload the project to recompile dependencies.

## Built With

* [Selenium](https://selenium.dev/selenium/docs/api/java/index.html) - The automation framework used
* [Gradle](https://gradle.org/) - Build tool

## Contributing

Since this project is the property of the American River College Design Hub, it is not open to contributions by developers outside the company. If you are an intern or employee of the Design Hub, and are interested in working on the project, please contact Matt Crow (w# is 1599227), and he can get you set up.

## Authors

* **Matt Crow** - *Initial work* - [IronHeart7334](https://github.com/IronHeart7334)

See also the list of [contributors](https://github.com/design-hub-arc/ARCDHWebAutomator/contributors) who participated in this project.
