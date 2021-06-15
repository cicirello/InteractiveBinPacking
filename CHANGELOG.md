# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 2021-06-15

### Added
* Feature that logs most user interaction with the application, and
  generates an in-application view of the session log. This session log
  summarizes total time in the application, instances solved by the user
  correctly using their chosen heuristic, number of successful and
  unsuccessful solving actions taken in each of the application modes,
  as well as additional information that may be useful to instructors
  using the application within a course context.
  
### Changed

### Deprecated

### Removed

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
