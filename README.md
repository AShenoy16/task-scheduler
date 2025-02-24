# SOFTENG 306 Project 2

This README provides an overview of our Project 2 submission for SOFTENG 306.

This project revolves around the application of artificial intelligence and parallel processing to solve a challenging scheduling problem, with a strong focus on speed and performance. It is conducted in collaboration with the client, Oliver Sinnen, from the Big-As Parallel Computing Centre. The primary objective is to develop a software engineering-based solution for efficient task scheduling on parallel computer systems while ensuring rapid execution. The project involves tackling the complex scheduling problem through a branch-and-bound type algorithm, coupled with parallelisation to utilise multiple processors for enhanced solution exploration, and enabling visualisation of this algorithm.

## Table of Contents

1. [Source Code](#source-code)
2. [Project Structure](#project-structure)
3. [Running The JAR File](#running-the-jar-file)
4. [Options](#options)
5. [Example Usage](#example-usage)
6. [Our Team](#our-team)

## Source Code

Our source code consists of two algorithms: A-star and Branch and Bound, each located in their separate folders, with the `scheduler.jar` file in the root directory.

### Project Structure

- `scheduler.jar`: Our executable JAR file for running the algorithms.
- `a-star/`: Contains the A-star algorithm code.
- `branch-and-bound/`: Contains the Branch and Bound algorithm code.
- `controller/`: Contains JavaFX files for visualisation
- `io/`: Contains IO reader and writer of dot file
- `model/`: Contains classes for graph algorithm; graph, node, edge e.g.
- `visualisation/`: Contains visualisation classes

## Running The JAR file

To execute the project, use the following command:

```bash
java -jar scheduler.jar INPUT.dot P [OPTIONS]
```
- `INPUT`: Name of dot file of the task graph with integer weights
- `P`: Number of processors to schedule the `INPUT` graph on

## Example:
  ```bash
  java -jar scheduler.jar .\src\test\graphs\Custom_14_Nodes.dot 2 -v
  ```

## Options

- `-o OUTPUT`: Write the algorithm's output to a custom-named output file. For example, you can use `-o custom_output` to specify a custom output filename. If not specified, the default output file will be named `INPUT-output.dot`. Feel free to customize the output filename as needed to analyze the results.
- `-p N`: Adjust 'N', uses 'N' cores for parallel execution (default is sequential)
- `-v`: Option display visualisation GUI the search algorithm 

**Note:** Ensure that the `INPUT.dot` file is in the same location as the `scheduler.jar` file for proper execution.

## Example Usage

To execute the A-star algorithm with 2 processors using the 'Nodes_7_OutTree.dot' input file and generate an output DOT file with the custom name 'customOutput.dot', run the following command:

```bash
java -jar scheduler.jar Nodes_7_OutTree.dot 2 -o customOutput
```

To execute the Branch and Bound algorithm with 2 processors using the 'Nodes_7_OutTree.dot' input file, enabling visualisation, enabling parallisation on 4 cores, and generate an output DOT file with the custom name 'customOutput.dot', run the following command:

```bash
java -jar scheduler.jar Nodes_7_OutTree.dot 2 -o customOutput -v -p 4
```

## Our Team
**Team 10 - Team Members**
- Kai Hirafune
- Jason Yang
- Matthew Wai
- Steven Li
- Adi Shenoy

For more information, please see our wiki: https://github.com/UOASOFTENG306/project-2-project-2-team-10/wiki
