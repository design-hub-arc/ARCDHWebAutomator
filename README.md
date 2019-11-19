# PSDataImprovementSelenium

An automation program used to speed up the American River College business systems.

## Project Purpose

American River College is a community college, and as such, it does not want to spend the money it has on upgrading its business systems.
On such system is an ancient PeopleSoft website, lacking in many quality-of-life features which would make it pleasant to use.
In order to extract any useful budgetary information, the user would have to submit several queries, remember the results, and input them into other forms.
This process is needlessly tedious, and so this project was created to automate much of the process.

## Getting Started

To use this project, you need Java installed on your computer. You can check if Java is installed by attempting to run the project JAR file (see the section "Running the program"), or you can open your terminal, and type
```
java -version
```
if your computer can find java, you're good! Otherwise, you can download it [here](https://www.java.com/en/). 
To start, you will want to download the project by clicking the "clone or download button", clicking "download zip", and extracting the files on your computer.

### Running the program (for users)

Once you've downloaded and unzipped the project, you can ignore most of the files. Go to PSDataImprovementSelenium-master/build/libs, and you will find the project JAR file, PSDataImprovementSelenium.jar. Feel free to move this to anywhere on your computer, as it is completely self-contained. Double click the JAR file to run it, or if that doesn't work, you can can see what's going wrong by opening up your terminal (Command Prompt for Windows, or Terminal for Mac), and type
```
java -jar
```
(make sure you have a space after -jar) Drag and drop the JAR file to the terminal window, and you should see something like this:
```
java -jar C:\Users\****\Desktop\PSDataImprovementSelenium.jar
```
You can then hit enter, at which point, if the application doesn't start, the terminal will tell you what went wrong. Feel free to email Matt, and he can tell you what went wrong (screenshots are helpful).

### How to edit the application (for developers)

You will need Netbeans IDE version 8.2 with the Gradle plugin installed to run the project.
Obviously, Java is needed to allow the project to run. You can use
```
gradle jar
```
to rebuild the JAR file for the project.

## Built With

* [Selenium](https://selenium.dev/selenium/docs/api/java/index.html) - The automation framework used
* [Gradle](https://gradle.org/) - Build tool

## Contributing

Since this project is the property of the American River College Design Hub, it is not open to contributions by developers outside the company. If you are an intern or employee of the Design Hub, and are interested in working on the project, please contact Matt Crow (w# is 1599227), and he can get you set up.

## Authors

* **Matt Crow** - *Initial work* - [IronHeart7334](https://github.com/IronHeart7334)

See also the list of [contributors](https://github.com/design-hub-arc/PSDataImprovementSelenium/contributors) who participated in this project.
