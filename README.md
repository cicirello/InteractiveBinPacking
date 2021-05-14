# Interactive Bin Packing Application

Copyright (C) 2008, 2010, 2020-2021 Vincent A. Cicirello.

https://www.cicirello.org/

| | |
| :--- | :--- |
| __Packages and Releases__ | [![GitHub release (latest by date)](https://img.shields.io/github/v/release/cicirello/InteractiveBinPacking?logo=GitHub)](https://github.com/cicirello/InteractiveBinPacking/releases) |
| __Build Status__ | [![build](https://github.com/cicirello/InteractiveBinPacking/workflows/build/badge.svg)](https://github.com/cicirello/InteractiveBinPacking/actions/workflows/build.yml) |
| __License__ | [![License](https://img.shields.io/github/license/cicirello/InteractiveBinPacking)](https://github.com/cicirello/InteractiveBinPacking/blob/master/LICENSE) |

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

## Java 11+

The prebuilt jar of the application is built with the OpenJDK 11.

## Building and Testing the Application (with Maven)

The Interactive Bin Packing Application is built using Maven. The 
root of the repository contains a Maven `pom.xml`.  To build the library, 
execute `mvn package` at the root of the repository, which
will compile all classes, run all tests, and generate jar files of the 
application, the sources, and the javadocs.  The file names
make this distinction explicit.  All build artifacts will then
be found in the directory `target`.

To include generation of a code coverage report during the build,
execute `mvn package -Pcoverage` at the root of the repository to 
enable a Maven profile that executes JaCoCo during the test phase.

The jar file of the application is executable, so you then simply
double click it to run.

## Installing and Running the Application

To install, do any one of the following:
* Build (see above section), and then find the jar file in the target directory.
* Simply download a prebuilt jar from any of 
  the [GitHub releases](https://github.com/cicirello/InteractiveBinPacking/releases).

To run, assuming that you have a Java runtime environment (JRE version 11 or higher)
on your system, then simply double click the jar file.

## User Documentation

All user documentation is contained within the application itself, which you can access via two
commands in the Info menu:

* The Tutorial command in the Info menu opens a self-guided tutorial 
  on combinatorial optimization, the bin packing problem, and 
  constructive heuristics for bin packing. It will also walk you through 
  using the application, and all of its functionality. 
* The Help command (also in the Info menu) provides documentation of all 
  menu functions and other user interface elements.

The first time that you use the application, we recommend that you 
begin by choosing the Tutorial command from the Info menu, and working 
your way through the self-guided tutorial.  

## License

The Interactive Bin Packing Application is licensed under 
the [GNU General Public License 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

## Contribute

Report bugs, suggestions, feature requests, etc via 
the [issues tracker](https://github.com/cicirello/InteractiveBinPacking/issues).
If you would like to contribute to Interactive Bin Packing in any way, such 
as reporting bugs, suggesting new functionality, or code contributions 
such as bug fixes or implementations of new functionality, then start 
by reading 
the [contribution guidelines](https://github.com/cicirello/.github/blob/main/CONTRIBUTING.md).
This project has adopted 
the [Contributor Covenant Code of Conduct](https://github.com/cicirello/.github/blob/main/CODE_OF_CONDUCT.md).
