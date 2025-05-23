# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 2025-05-15

### Added
  
### Changed

### Deprecated

### Removed

### Fixed

### CI/CD

### Other


## [3.1.6] - 2025-05-15

### Other
* Migrated deployment from the old OSSRH to the new Central.


## [3.1.5] - 2023-08-07

### Fixed
* Improvements based on SpotBugs and FindSecBugs scans:
  * LogRecord.toString() changed to use platform-specific line separators.
  * LogRecord.equals(Object): correctly handle null and other types.
  * RecordList.equals(Object): correctly handle null and other types.
  * Bin.contentsToString(): StringBuilder instead of iterated concat.
  * SessionLog.moveCountToString(): StringBuilder instead of iterated concat.
  * SessionLogFormatter: fixed potential resource leak.
  * SessionLog: implemented readObject to properly initialize transient fields during deserialization.
  * Set most classes to package access.

### CI/CD
* Integrated SpotBugs into build process.
* Integrated FindSecBugs into build process.


## [3.1.4] - 2023-01-12

### Fixed
* Refactored the following based on RefactorFirst scan:
  * SessionLog class
  * GUI-related test cases
  * Non-GUI-related test cases


## [3.1.3] - 2022-12-01

### Changed
* Reformatted all sourcecode to [Google Java Style](https://google.github.io/styleguide/javaguide.html).
* Migrated test cases to JUnit Jupiter 5.9

### Fixed
* Misc refactoring based on feedback from Sonatype Lift, including:
  * Added explicit Charset when loading SessionLog template.
  * Added explicit cast in narrowing compound assignment in initialization of item list.
  * In SessionLog, replaced usage of Java's Date class, which is known to be buggy, with Instant.
  * Removed single use constant with format string for SessionLog. 
  * Fixed empty catch blocks.

### CI/CD
* Configured [Spotify's fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin) to format to Google Java Style during builds.
* Configured the [refactor-first-maven-plugin](https://github.com/jimbethancourt/RefactorFirst) within a profile in the pom.xml.

### Other
* Adopted [Google Java Style](https://google.github.io/styleguide/javaguide.html).


## [3.1.2] - 2022-04-01

### Other
* Release is to force an update of the metadata on the archiving site Zenodo to 
  include link to paper in Journal of Open Source Education. This release otherwise 
  contains no new or changed functionality.


## [3.1.1] - 2022-03-31

### Other
* Release is for paper in Journal of Open Source Education (JOSE), and also
  to start archiving in Zenodo for same purpose. This release otherwise contains
  no new or changed functionality.

### CI/CD
* Modified CI/CD workflow to comment coverage percentages on PRs.


## [3.1.0] - 2021-06-24

### Added
* A directory of example homework assignments that can be used or adapted by
  course instructors using the application in a class. The example assignments
  can also be used by self-directed learners.
* Session Logs
  * Feature that logs most user interaction with the application, and
    generates an in-application view of the session log. 
  * The session log summarizes total time in the application, instances 
    solved by the user correctly using their chosen heuristic, number of 
    successful and unsuccessful solving actions taken in each of the 
    application modes, as well as additional information that may be useful 
    to instructors using the application within a course context.
  * In addition to viewing the current session log, session logs can be
    saved to a file. The motivation is to ease instructor grading of 
    assignments that use the application. For example, instructors can
    assign each student a different problem instance and have students save
    and submit the session log, which the instructor can then use for 
    validation.
  * The load session log feature loads a session log, validates it, and displays
    a human readable summary. The validation verifies that instances that are
    claimed solved using a particular heuristic were actually solved using that
    heuristic. That is, the session log doesn't include the solution, but rather
    it includes the actions taken to reach the solution, the problem instance
    number, and the chosen heuristic, and the application validates that the actions
    taken are those that the heuristic would have chosen. The validation also
    validates timestamp sequences, etc.
  * Note to instructors: We don't guarantee that the session logs cannot be
    faked. However, the effort required to fake a session log that will fool the
    validation is significantly more than the effort needed to work through the
    exercises. Additionally, to successfully fake a session log, the student would
    still need the correct sequence of actions to solve the specified instance
    using the specified heuristic. Simply assign each student a different
    problem instance number (it is a value of type long), perhaps based on their 
    student id number.

### Fixed
* Reorganized resources files (html content of dialogs, and images)
  to use a namespace (i.e., within a directory hierarchy based on package name).
  

## [3.0.1] - 2021-05-26

### Changed
* Updates to documentation.

### Fixed
* Code improvements to eliminate potentially errorprone 
  code as identified in MuseDev's static analysis reports.


## [3.0.0] - 2021-05-14

### Added
* Adopted SemVer versioning.
* First release to Maven Central Repository and to the GitHub Package Registry.

### Fixed
* Minor resource leak in unclosed Scanner.


## [3.0] - 2020-07-09

First public release.

### Added
* Automated tutorial features.
* Self-guided tutorial on combinatorial optimization, the bin 
  packing problem, and constructive heuristics for bin packing.
* Checks user's progress and provides feedback enabling user to test 
  their knowledge of the various constructive heuristics covered by 
  the tutorial.
* Help dialog box.
* Tutorial dialog box.
* Computation of lower bounds.
* Several heuristic modes that monitor user's interaction with
  bin packing instance and provides feedback on correctness of their
  usage of different constructive heuristics: first-fit, first-fit decreasing,
  best-fit, and best-fit decreasing.
  
### Changed
* Significant refactoring of existing code.


## [2.0] - 2010

### Added
* Generate instances by a seed for reproducibility.
* Ability to sort items by size.
* About dialog box.

### Changed
* Improvements to interface.


## [1.0] - 2008

### Added
* Interactively manipulate random instances of bin packing.
* Visualize random instances of bin packing.
