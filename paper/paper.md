---
title: 'Interactive Bin Packing: A Java Application for Learning Constructive Heuristics for Combinatorial Optimization'
tags:
  - combinatorial optimization
  - bin packing
  - constructive heuristics
  - discrete optimization
  - Java
  - self-guided tutorial
authors:
  - name: Vincent A. Cicirello
    orcid: 0000-0003-1072-8559
    affiliation: 1
affiliations:
  - name: Computer Science, School of Business, Stockton University, Galloway, NJ 08205
    index: 1
date: 22 December 2021
bibliography: paper.bib
---

# Summary

Interactive Bin Packing provides a self-guided tutorial on combinatorial 
optimization, the bin packing problem, and constructive heuristics. It also 
enables users to interact with bin packing instances to explore their 
own problem solving strategies, or to test their knowledge of the 
constructive heuristics covered by the tutorial. The application is not a 
solver for bin packing, but rather it is a tool for learning about the 
bin packing problem, and for learning about heuristic techniques 
for solving instances of the problem.

The repository (https://github.com/cicirello/InteractiveBinPacking) contains 
the source code, documentation, and example assignments suitable for courses
on discrete mathematics, algorithms, or artificial intelligence (AI), or by 
self-directed learners. An executable jar of the application is available 
from GitHub Releases and Maven Central.

# Bin Packing

Bin packing is a combinatorial optimization problem involving 
packing a set of items into a set of bins to minimize the number of 
bins. It has many practical applications, including 
within logistics, such as packing shipments into trucks or airplanes, 
as well as applications such as creating compilations of musical works, 
scheduling television advertisements, placing files in fixed-length 
memory blocks on a file system, wireless network channel scheduling, 
job scheduling, among many 
others [@Leinberger1999; @Malkevitch2004a; @Malkevitch2004b; @Wang2006].

As an NP-Hard [@Garey1979] optimization problem, it is unlikely that any 
polynomial-time algorithm exists guaranteed to optimally solve bin packing. 
Thus, heuristics and metaheuristics are often used. Among the topics 
covered by this tutorial application are constructive heuristics. A 
heuristic is a practical problem solving approach that provides a 
solution of sufficient quality, while using relatively little time. A 
constructive heuristic iteratively applies some rule to build upon an 
initially empty solution until it is complete. 

# Origin of the Application

In my teaching, I have used bin packing to introduce heuristics to 
students in courses for computer science majors, as well as for 
non-majors. Using the Computer Science Unplugged [@Nishida2009] 
methodology, I previously developed an in-class activity called 
Collective Bin Packing [@Cicirello2009], in which students take turns 
making individual decisions on which item to place in which bin. One 
objective of that activity is to illustrate concepts of collective
intelligence, or swarm intelligence, where intelligent behavior emerges 
from indirect interaction within the swarm. A preliminary version of 
Interactive Bin Packing was used by the instructor to keep track of the 
state of the emerging solution. Without the aid of the application, 
the Collective Bin Packing activity was later adapted to an online 
course environment [@Cicirello2013], and even used in a course for 
non-majors [@Cicirello2007]. 

# Functionality

This first public release of Interactive Bin Packing features a 
self-guided tutorial that explains key concepts of combinatorial 
optimization, bin packing, and constructive heuristics. It also 
includes several heuristic modes, in which the user can test their 
knowledge of the different constructive heuristics. When used in a 
heuristic mode, the user is expected to make decisions that match 
their chosen heuristic's behavior. The application provides feedback 
on correctness throughout.

# Target Audience, Instruction Time, and Objectives

The application is a software tool to assist students in learning. It 
assumes prerequisite knowledge of introductory discrete mathematics concepts
including set theory and functions. It can be used in courses 
on: (a) discrete mathematics toward the end to introduce combinatorial 
optimization, (b) algorithms to provide an example of an NP-Hard problem 
and the use of heuristics to efficiently compute satisficing solutions 
despite the problem's complexity, or (c) AI as an 
example of heuristic problem solving or prior to coverage of local 
search algorithms. It can also be used by self-directed learners.

The GitHub repository also contains a directory with example assignments
that utilize the application. One of these is designed to provide an 
introduction to combinatorial optimization and heuristic problem solving,
and requires no pre-lecture. The time to complete this assignment, including 
working through the self-guided tutorial within the application
and completing self-check exercises, varies between 66 minutes 
and 135 minutes, depending upon the learner's prior background and course 
level. To use the application in your own teaching, we recommend 
utilizing this assignment as an introduction to your topic of 
choice. For example, in an AI course, you might then cover hill 
climbing to show how you can iteratively improve upon the heuristic's 
solution; or in an algorithms course, you might use this assignment 
while covering the theory of NP-Completeness.

The objectives of the Interactive Bin Packing Application include:

* gaining a general understanding of combinatorial optimization;
* gaining an understanding of the bin packing problem, its 
  applications, and how it is an example of combinatorial 
  optimization;
* learning about lower bounds;
* learning about constructive heuristics;
* learning about the most common constructive heuristics for bin 
  packing, including first-fit, best-fit, first-fit decreasing, 
  and best-fit decreasing; and
* serving as an interactive environment for students (whether in a 
  formal course context, or for informal self-directed learning) to 
  explore problem solving methods for combinatorial optimization.

# Statement of Need

Many combinatorial optimization problems, including bin packing, traveling 
salesperson, largest common subgraph, and numerous others, are NP-Hard; and 
at the same time have practical real-world applications requiring quickly 
producing solutions. Heuristics, metaheuristics, and other approximate 
algorithms enable balancing the theoretical hardness of NP-Hard problems 
with the need for quality solutions to those problems. Constructive 
heuristics provide a gentle introduction to this category of problem solving 
techniques. Existing bin packing tutorials are limited to web applications 
that calculate heuristic solutions [@PlanetcalcBinPacking], algorithm animations
that visualize heuristics in action [@Cernohorska2015], and optimization library 
tutorials that use bin packing to provide examples of using an optimization
API [@GoogleORTools]. However, existing tutorials lack interactivity as well
as feedback to the learner. The interactive environment of our application 
keeps learners engaged. Bin packing offers a puzzle-like experience, which has 
been shown effective in engaging students in algorithmic thinking [@Levitin2005].
 

# References
