# Number Bond Kata

To create a simple utility class that will find the first number bond in a sequence of numbers, that is equal to the sum 
of a target number.

## Acceptance criteria

* __Given__ a number of _< target >_
* __When__ supplied a sequence of _< input >_
* __Then__ return a number bond of  _< output >_

| target | input     | output  |
|--------|-----------|---------|
| 0      | {}        | {}      |
| 0      | {0,}      | {}      |
| 0      | {1,1,0}   | {}      |
| 3      | {1,1,1}   | {}      |
| 0      | {0,0}     | {0,0}   |
| 1      | {0,0,1}   | {0,1}   |
| 2      | {1,1,1}   | {1,1}   |
| 2      | {1,1,2,0} | {1,1}   |
| 0      | {-0,1,0}  | {-0,0}  |
| 0      | {-1,0,1}  | {-1,1}  |
| -2     | {-1,1,-1} | {-1,-1} |

## Specification

1. Target is of type int.
2. Input and output are of type int[], not a List or Collection.
3. Input may contain zero or more integers. There is no fixed limit.
4. Input may contain both negative and positive integers.
5. Output should never be null.
6. Output should contain an empty array, if a number bond is not found.
7. Output should contain a single pair of numbers, if a number bond is found.
8. Output should contain the first number bonds found in the input, regardless if other number bonds exist.
9. A number bond must be returned in the order it appears in the input.

## Guidelines

1. Apply TDD throughout. No production code is written until you have written a failing test.
2. Do NOT write more code than is necessary to pass the failing test.
3. A failing test is any code that either fails to compile, or produce an expected result.
4. Internal implementation is of your discretion.
5. Try to keep it as simple as possible.
6. Third party libraries are prohibited for production code.
7. Use of Java 8 SDK is permitted.
8. Use of JUnit, TestNG or other testing framework are permitted.
9. Use of the Internet is permitted, but only for reference. Try to use what you know.
10. Tail call optimisation is not supported in Java. Please bear this in mind.

## Terminology

A number bond is the sum of two distinct numbers that are equal to a given number.