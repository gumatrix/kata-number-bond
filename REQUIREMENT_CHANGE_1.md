# Number Bond Kata

To create a simple utility class that will find all number bonds in a sequence of numbers, that are equal to the sum of 
a given number.

## Acceptance criteria amendment

* __Given__ a number of _< target >_
* __When__ supplied a sequence of _< input >_
* __Then__ return array of number bonds of  _< output >_

| target | input   | output    |
|--------|---------|-----------|
| 0      | {}      | {}        |
| 2      | {1,0,1} | {1,1}     |
| 1      | {1,0,1} | {1,0,0,1} |

## Specification

1. Target is of type int.
2. Input and output are of type int[], not a List or Collection.
3. Input may contain zero or more integers. There is no fixed limit.
4. Input may contain both negative and positive integers.
5. Output should never be null.
6. Output should contain an empty array, if number bonds are not found.
7. Output should contain one or more number pairs, if number bonds are found.
8. A number bond must be returned in the order it appears in the input.
9. Each number bond returned in the array must appear side-by-side, in pairs of two.

## Guidelines

1. Apply TDD throughout. No production code is written until you have written a failing test.
2. Do NOT write more code than is necessary to pass the failing test.
3. A failing test is any code that either fails to compile, or produce an expected result.
4. Internal implementation is of your discretion.
5. Try to keep it as simple as possible.
6. Third party libraries are prohibited for production code.
7. Use of Java 8 SDK is permitted.
8. Use of JUnit, TestNG or other testing framework are permitted.
9. Use of the Internet is permitted, but only for reference. Try to use what current knowledge you hold.
10. Tail call optimisation is not supported in Java. Please bear this in mind.

## Terminology

A number bond is the sum of two distinct numbers that are equal to a given number.