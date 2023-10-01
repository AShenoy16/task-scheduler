# SOFTENG 306 Project 2 Milestone 1

**Team 10**

**Team Members:**
- Kai Hirafune
- Jason Yang
- Matthew Wai
- Steven Li
- Adi Shenoy


This README provides an overview of our Project 2 Milestone 1 submission for SOFTENG 306.

## Table of Contents

1. [Source Code](#source-code)
2. [Running The JAR File](#running-the-jar-file)
3. [Options](#options)
4. [Example Usage](#example-usage)

## Source Code

Our source code consists of two algorithms: A-star and Branch and Bound, each located in their separate folders, with the `scheduler.jar` file in the root directory.

### Folder Structure

- `a-star/`: Contains the A-star algorithm code.
- `branch-and-bound/`: Contains the Branch and Bound algorithm code.
- `scheduler.jar`: Our executable JAR file for running the algorithms.

## Running The JAR file

To execute the project, use the following command:

```bash
java -jar scheduler.jar INPUT.dot P [OPTIONS]

```
## Options

- `-o OUTPUT`: Write the algorithm's output to a custom-named output file. For example, you can use `-o custom_output` to specify a custom output filename. If not specified, the default output file will be named `INPUT-output.dot`. Feel free to customize the output filename as needed to analyze the results.

**Note:** Ensure that the `INPUT.dot` file is in the same location as the `scheduler.jar` file for proper execution.

## Example Usage

To execute the A-star algorithm with 2 processors using the 'Nodes_7_OutTree.dot' input file and generate an output DOT file with the custom name 'customOutput.dot', run the following command:

```bash
java -jar scheduler.jar Nodes_7_OutTree.dot 2 -o customOutput

