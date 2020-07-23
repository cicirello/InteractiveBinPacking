# Interactive Bin Packing Application

Copyright (C) 2008, 2010, 2020 Vincent A. Cicirello.

https://www.cicirello.org/

[![build](https://github.com/cicirello/InteractiveBinPacking/workflows/build/badge.svg)](https://github.com/cicirello/InteractiveBinPacking/actions?query=workflow%3Abuild)
[![GitHub](https://img.shields.io/github/license/cicirello/InteractiveBinPacking)](https://github.com/cicirello/InteractiveBinPacking/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cicirello/InteractiveBinPacking)](https://github.com/cicirello/InteractiveBinPacking/releases)

## Overview

The Interactive Bin Packing Application provides a self-guided tutorial on combinatorial optimization, the bin packing problem, and constructive heuristics for bin packing. It also enables the user to interact with bin packing instances to explore their own problem solving strategies, or to test their knowledge of the various constructive heuristics covered by the tutorial. The application is not a solver for bin packing. The Interactive Bin Packing Application is a tool for learning about the bin packing problem, as well as for learning about heuristic techniques for solving instances of the problem. 

The objectives of the Interactive Bin Packing Application include:

* gaining an understanding of the bin packing problem, and more generally how
the bin packing problem is an example of a combinatorial optimization
problem;
* learning about constructive heuristics;
* learning about the most common constructive heuristics for the bin
packing problem, including first-fit, best-fit, first-fit decreasing, and
best-fit decreasing; and
* serving as an interactive environment for students (whether in the formal 
context of a class, or just for informal self-guided learning) to explore
potential problem solving methods for combinatorial optimization.

## Repository Organization

The GitHub repository is organized as follows:

* The /build directory contains an ant build file, and other resources related to building the application.
* The /dist directory contains an executable jar file of the application.
* The /src directory contains all of the source code for the application.
* The /tests directory contains JUnit test cases for nearly all functionality of the application, including automated tests for most of the GUI. Also in that directory is a markdown file listing a few manual testing steps for certain GUI elements for which it is especially inconvenient to automate.

## Building and Testing the Application (with ant)

The /build directory contains an ant build file.  The build directory also contains the relevant jar files for the JUnit libraries for testing via JUnit. If you prefer, you can replace these with the latest versions available from https://junit.org/junit4/ (just be sure to edit the property fields in the build.xml to point to the locations of the JUnit jar files).

To execute the build process do one of the following. If your working directory is the build directory, then simply execute `ant` from the command line. If your working directory is the parent of build, then execute: `ant -f build/build.xml`

The default of the provided ant build file, compiles all source files and generates the executable  jar of the application. If you wish to execute the JUnit tests, then you can execute `ant test` from the command line. If your working directory is the parent of build, then execute: `ant -f build/build.xml test`.  This command will compile the application, execute the JUnit test cases, and generate the executable jar file.

The build generates the following directories: bin (for the compiled Java classes) and testbin (for the compiled JUnit tests).

See the markdown file in the /tests directory for additional information on how to manually test the few GUI elements not included among the JUnit tests.

## Installing and Running the Application

To install the application, simply copy the jar file from the /dist directory to the location of your choice on your system. The current version is already available in the /dist directory here on GitHub (if you don't wish to execute the build yourself). We assume that you have Java 8 or higher already installed on your system.

To run the application, you can do one of two things.  First, you can simply double click on the jar file.  It is executable so assuming your Java is up to date on your system, double clicking is all you need to do to run it.  Alternatively, from the command line navigate to the directory containing the jar file, and execute `java -jar interactive-bin-packing-3.0.jar` (you may also rename the jar file to something shorter if you prefer).

## Dependencies

The application doesn't depend on any external libraries.  To build the application, you must have: (1) Java 8 or higher, and (2) Apache Ant (https://ant.apache.org/).  Most of the source code is in Java.  The /src/res/html directory contains a couple html files used for formatting the content of a few dialog windows. 

Running the application via the jar file provided in the /dist directory only requires that you have a Java runtime environment installed on your system (JRE version 8 or higher).

## User Documentation

All user documentation is contained within the application itself, which you can access via two
commands in the Info menu:

* The Tutorial command in the Info menu opens a self-guided tutorial on combinatorial optimization, the bin packing problem, and constructive heuristics for bin packing. It will also walk you through using the application, and all of its functionality. 
* The Help command (also in the Info menu) provides documentation of all menu functions and other user interface elements.

The first time that you use the application, we recommend that you begin by choosing the Tutorial command from the Info menu, and working your way through the self-guided tutorial.  

## License

The Interactive Bin Packing Application is licensed under the [GNU General Public License 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

## Contribute

Report bugs, suggestions, feature requests, etc via the [issues tracker](https://github.com/cicirello/InteractiveBinPacking/issues). If you would like to directly contribute new code, you may also fork the repository, and create pull requests.  All code contributions must include JUnit tests (whenever feasible), and all existing JUnit tests must pass for your code contribution to be accepted.  If your code contribution is a bug-fix, then you must include JUnit tests (or manual steps if JUnit not feasible) that recreate the bug prior to your fix, and which pass afterwards.



