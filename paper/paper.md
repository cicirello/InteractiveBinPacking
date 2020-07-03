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
date: 3 July 2020
bibliography: paper.bib
---

# Summary

The Interactive Bin Packing Application provides a self-guided tutorial on combinatorial optimization, the bin packing problem, and constructive heuristics. It also enables the user to interact with bin packing instances to explore their own problem solving strategies, or to test their knowledge of the various constructive heuristics covered by the tutorial. The application is not a solver for bin packing, but rather it is a tool for learning about the bin packing problem, as well as for learning about heuristic techniques for solving instances of the problem.

The repository (https://github.com/cicirello/InteractiveBinPacking) contains source code of the application, JUnit tests for automated testing of its functionality, and documentation.

# The Bin Packing Problem

The bin packing problem is a combinatorial optimization problem in which we must pack a set of items into a set of bins while minimizing the number of bins used. This problem has many practical applications, including the obvious applications in logistics, such as packing shipments into trucks or airplanes, as well as many others such as creating compilations of musical works, scheduling when to air advertisements on television, placing files in fixed-length memory blocks on a file system, wireless network channel scheduling, job scheduling, among many others [@Leinberger1999; @Malkevitch2004a; @Malkevitch2004b; @Wang2006].

As an NP-Hard [@Garey1979] optimization problem, it is unlikely that any efficient algorithm exists capable of optimally solving the bin packing problem in polynomial time. Thus, heuristic algorithms, as well as metaheuristics, are often used. Among the topics covered by this tutorial application are constructive heuristics. A heuristic is a practical problem solving approach that quickly provides a solution of sufficient quality, while using relatively little time. A constructive heuristic is a type of heuristic that begins with an empty solution, and then repeatedly applies some rule to build that solution until it is complete. The result may or may not be optimal, and in most cases you have no way of knowing whether or not it is optimal. The application guides the user into learning about common constructive heuristics for bin packing.

# Origin of the Application

In my teaching, I have on occasion used the bin packing problem as a way of introducing heuristics to students in both courses for computer science majors, as well as for non-majors. Using the Computer Science Unplugged [@Nishida2009] methodology, I previously developed an in-class activity called Collective Bin Packing [@Cicirello2009], in which students take turns making individual decisions on which item to move to which bin. One of the objectives of that activity is to illustrate concepts of collective intelligence, or swarm intelligence, where intelligent behavior emerges from indirect interaction among the members of the swarm. A preliminary version of the Interactive Bin Packing application was used by the instructor to keep track of the state of the emerging solution. The Collective Bin Packing activity was later adapted to an online course environment [@Cicirello2013], and even used in a course for non-majors [@Cicirello2007], although without the aid of this application in the latter examples. 

This is the first public release of the Interactive Bin Packing Application, and it features significant functionality developed specifically for this release. In particular, it includes a self-guided tutorial that explains the key concepts from combinatorial optimization, bin packing, and constructive heuristics. It also now includes several heuristic modes, in which the user is able to test their knowledge of the different common constructive heuristics for the problem. When used in one of these heuristic modes, the user is expected to make decisions that match the selected heuristic's behavior. The application gives them feedback if they incorrectly use the heuristic.

# Target Audience and Objectives

The application is a software tool to assist students in learning. This can be within the context of an introductory course on artificial intelligence, or a related topic, where the application can be assigned as homework or a supplemental assignment to students. It can also be within a less formal learning context, used by anyone learning independently. The objectives of the Interactive Bin Packing Application include:

* gaining an understanding of the bin packing problem, and more generally how bin packing is an example of a combinatorial optimization problem;
* learning about constructive heuristics;
* learning about the most common constructive heuristics for bin packing, including first-fit, best-fit, first-fit decreasing, and best-fit decreasing; and
* serving as an interactive environment for students (whether in a formal course context, or just for informal self-guided learning) to explore potential problem solving methods for combinatorial optimization.

# Statement of Need

Many combinatorial optimization problems, including bin packing, traveling salesperson, largest common subgraph, and numerous others, are NP-Hard; and at the same time have practical real-world applications requiring quickly producing solutions. Bin packing offers a puzzle-like experience, which has been shown effective in engaging students in algorithmic thinking [@Levitin2005]. Heuristics, metaheuristics, and other approximate algorithms enable balancing the theoretical hardness of NP-Hard problems with the need for quality solutions to those problems. Constructive heuristics provide a gentle introduction to this category of problem solving techniques. The interactive environment of the application helps keep learners engaged. 

# References
