# Number Bond Kata

## Transformation Priority Premise

More information can be found here[Transformation Priority Premise](http://blog.cleancoder.com/uncle-bob/2013/05/27/TheTransformationPriorityPremise.html).
The following kata is based upon my understanding of the Premise and attempts to reenact decisions that were made in the 
Prime Factor kata.

1. Null
2. Null to Constant
3. Constant to Variable
4. Add Computation
5. Split Flow
6. Variable to Array
7. Array to Container
8. If to While
9. Recurse
10. Iterate
11. Assign
12. Add Case

## Initial requirements

Refer to [REQUIREMENTS.md](file://REQUIREMENTS.md) for the first set of requirements.

#### Null -> Constant transformation

Looking at the requirements, it is obvious which test case I want to pass first. It requires very little code and
covers the first four test cases. 

```
assertArrayEquals(new int[]{}, NumberBond.find(0, new int[]{}));

static class NumberBond {
    static int[] find(int t, int[] s) {
        return null;
    }
}
```

A Null -> Constant is needed to make this test pass:

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        return new int[]{};
    }
}
```

#### Constant -> Variable transformation

The next most interesting case, is the one that returns a number bond. In this case the `[0,0]`.

```
assertArrayEquals(new int[]{0,0}, NumberBond.find(0, new int[]{0,0}));
```

Obviously, the test will fail. In TDD, we want to pass a failing test as quickly as possible and with the fewest number
of key strokes to keep it simple. It makes sense that the next transformation is Constant -> Variable. We can pass the 
current failing test, by returning the input array of integers.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        return s;
    }
}
```

So far, so good. But there really isn't any behaviour at this point. We need a test and a transformation(s) that will
add some functionality.

#### Split Flow transformation

For my next test, I want to choose a test case where the input cannot be the result. A number bond is a pair of two 
numbers. Therefore, it makes sense to choose a test case where the input contains three numbers, but only two of them
are returned.

```
assertArrayEquals(new int[]{1,1}, NumberBond.find(2, new int[]{1,1,1}));
```

The following code passes the current failing test, but it fails the test case where the returned result is an empty 
array of integers.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        return new int[]{s[0], s[1]};
    }
}
```

If order to make the previous test case pass, I need to split the flow of execution to perform a check on the input to
see if it contains at least two numbers.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        if (s.length < 2) {
            return new int[]{};
        }

        return new int[]{s[0], s[1]};
    }
}
```

I am not overly happy with the logic thus far. The introduction of the guard made sense and therefore I went with my
instincts. However, I'm now stuck with two constants `int[]{s[0], s[1]}`. This assumes that the first two numbers in the
sequence are always the result. I need to think carefully about the next test case and transformation. I want to avoid 
making a decision about how I'll implement the remainder of the logic and worry about it when a test case presents
itself.

#### Add Computation

At the present moment, the primitive array of integers lack basic functionality that their object counterparts provide.
I am unable to use features likes `contains`.

Upon searching for a test case, I had an inkling to return to an earlier test case. One that would contain an array of 
two integers, but the numbers do not equal the target number.

```
assertArrayEquals(new int[]{}, NumberBond.find(2, new int[]{2,2}));
```

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        if (s.length < 2) {
            return new int[]{};
        }

        if (t == (s[0] + s[1])) {
            return new int[]{s[0], s[1]};
        }

        return new int[]{};
    }
}
```

Duplicate code! Refactor, refactor. Coming right up!

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        int[] bonds = {};

        if (s.length >= 2) {
            if (t == (s[0] + s[1])) {
                bonds = new int[]{s[0], s[1]};
            }
        }

        return bonds;
    }
}
```

I've thrown a bit of reassignment in there, but only because primitive arrays are initialised with a fixed length. I 
could had kept the early return statements, but I don't feel they read as well as a final return statement. The code 
still assumes that the first two numbers are either equal to, or not equal to the target number. I will need to change 
that in the next test case and transformation.

#### If -> While transformation

I cannot simply go ahead ane make the changes without first writing a test. The next most interesting case I could find,
is when the number bonds are not the first two numbers.

```
assertArrayEquals(new int[]{0, 1}, NumberBond.find(1, new int[]{0, 0, 1}));
```

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        int[] bonds = {};

        for (int index = 0; index < s.length - 1; index++) {
            if (t == (s[index] + s[index + 1])) {
                bonds = new int[]{s[index], s[index + 1]};
            }
        }

        return bonds;
    }
}
```

The code is a little more generalised at this point. The code no longer assumes that the first two numbers are the 
number bonds. Unfortunately, it still has the problem assuming that the numbers bonds are adjacent to one another.

#### Array -> Container transformation

I still have the problem that a primitive array lacks any functionality. I am unable to use some of the niceties that
Collections offer. I think I will address that. Otherwise, I'll need to select a lower transformation, such as another
iteration or perhaps even recurse in some fashion.

Let's start with a test:

```
assertArrayEquals(new int[]{1, 1}, NumberBond.find(2, new int[]{1,2,1}));
```

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        List<Integer> numbers = Arrays.stream(s)
            .boxed()
            .collect(Collectors.toList());

        int[] bonds = {};

        for (int number : numbers) {
            int bond = (t - number);

            if (numbers.contains(bond)) {
                bonds = new int[]{number, bond};
                break;
            }
        }

        return bonds;
    }
}
```

In fixing the test, I've introduced a bug. Contains does not take into account that a number has already been used. This
means that passing in `[1,2]` will still produce a result of `[1,1]`.

Time for another test:

```
assertArrayEquals(new int[]{}, NumberBond.find(2, new int[]{1,2}));
```

The test fails as expected. Unfortunately, we cannot shrink a Collection whilst it is being iterated over without 
causing an IndexOutOfBoundsException to be thrown. Therefore removing elements is not possible with a List, even if I 
add `numbers.remove(number)` at the beginning of the statement.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        Queue<Integer> numbers = Arrays.stream(s)
            .boxed()
            .collect(Collectors.toCollection(LinkedList::new));

        int[] bonds = {};

        while (!numbers.isEmpty()) {
            int number = numbers.poll();
            int bond = (t - number);

            if (numbers.contains(bond)) {
                bonds = new int[]{number, bond};
                break;
            }
        }

        return bonds;
    }
}
```

With a small amount of change this was solved easily. Poll made it easier to retrieve the next element, whilst the while
condition focused only on the fact that the Queue was not empty.

#### Interim commentary

I have made several attempts at this Kata. I have found that if I don't ask the right question, I end up either at a 
fork in the road or choosing a lower transformation than I like to. I am happy with the above code and feel that I have 
a simple solution.

## Change of requirements

Refer to [REQUIREMENT_CHANGE_1.md](file://REQUIREMENT_CHANGE_1.md) for the next set of requirements.

#### Variable -> Array

The first and most obvious change is the need to return a series of number bond pairs. They appear in the order they are 
discovered. I will not know upfront how many number bonds exist in a sequence. The current fixed length primitive array
will no longer be suitable. It is quick to assume that a expandable array would be more fitting, but is there another
way? Uncle Bob does recommend choosing higher transformations over lower ones. I will write a test and see what happens. 

```
assertArrayEquals(new int[]{1, 0, 0, 1}, NumberBond.find(1, new int[]{1, 0, 1}));
```

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        Queue<Integer> numbers = stream(s)
            .boxed()
            .collect(toCollection(LinkedList::new));

        StringBuilder bonds = new StringBuilder();

        while (!numbers.isEmpty()) {
            int number = numbers.poll();
            int bond = (t - number);

            if (numbers.contains(bond)) {
                if (bonds.length() > 0) {
                    bonds.append(",");
                }

                bonds.append(format("%s,%s", number, bond));
            }
        }

        return stream(bonds.toString().split(","))
            .filter(i -> i.length() > 0)
            .mapToInt(Integer::parseInt)
            .toArray();
    }
}
```

Unfortunately, this change fails two existing tests:

```
assertArrayEquals(new int[]{1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{0, 1}, NumberBond.find(1, new int[]{0, 0, 1}));
```

Fixing these tests to take into consideration of the new requirements is easy. What I like about the latter of these is
that it tests for the condition that number bonds appear in the order they are discovered. So, I've gained a test for 
free without having to write a new one.

```
assertArrayEquals(new int[]{1, 1, 1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{0, 1, 0, 1}, NumberBond.find(1, new int[]{0, 0, 1}));
```

After reviewing the tests after applying the change of requirements, I felt it was safe to remove some redundant ones:

```
assertArrayEquals(new int[]{1, 1, 1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{}, NumberBond.find(0, new int[]{1, 1}));
```

#### Alternative solutions

Using a combination of Variable, Assign, Add Computation and Arrays utils.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        Queue<Integer> numbers = stream(s)
            .boxed()
            .collect(toCollection(LinkedList::new));

        int[] bonds = {};
        int index = 0;

        while (!numbers.isEmpty()) {
            int number = numbers.poll();
            int bond = (t - number);

            if (numbers.contains(bond)) {
                bonds = Arrays.copyOf(bonds, bonds.length + 2);
                bonds[index++] = number;
                bonds[index++] = bond;
            }
        }

        return bonds;
    }
}
```

Using a combination of Container with Java 8 Streams.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        Queue<Integer> numbers = stream(s)
            .boxed()
            .collect(toCollection(LinkedList::new));

        List<Integer> bonds = new ArrayList<>();

        while (!numbers.isEmpty()) {
            int number = numbers.poll();
            int bond = (t - number);

            if (numbers.contains(bond)) {
                bonds.add(number);
                bonds.add(bond);
            }
        }

        return bonds.stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }
}
```

#### Interim commentary

Java 8 Streams make the code a little easier to read, but the implementation is not perfect. I'm not entirely satisfied
with my choice of using a higher transformation over a lower one as I believe it has made the code a little more complex 
and harder to read. I wanted to demonstrate that there is always an alternative solution and that the obvious one may 
not always simpler.

## Final change of requirements

Refer to [REQUIREMENT_CHANGE_2.md](file://REQUIREMENT_CHANGE_2.md) for the next set of requirements.

The first and most obvious change, is the need to return a multi-dimensional array of integers. All existing tests will
fail, so I'm tempted to comment out the existing tests and write a new one; then fix the failing tests after to prove if 
the code has changed behaviour. In addition, there is the unique constraint on the sequence that a number cannot be 
paired with another number twice, regardless if it makes up a number bond or not.

#### Coping with change

With any change of requirements, if it requires massive amounts of refactoring, it is most likely because the wrong 
transformation was chosen. If I had chosen a lower transformation, the changes may have been more significant that the 
changes made in the below code.

```
assertArrayEquals(new int[][]{{3, 1},{4, 0}}, NumberBond.find(4, new int[]{3, 4, 1, 0, 4}));
```

```
static class NumberBond {
    static int[][] find(int t, int[] s) {
        Queue<Integer> numbers = stream(s).boxed().collect(toCollection(LinkedList::new));

        List<int[]> bonds = new ArrayList<>();

        while (!numbers.isEmpty()) {
            int number = numbers.poll();
            int bond = (t - number);

            if (numbers.contains(bond)) {
                bonds.add(new int[]{number, bond});
                numbers.remove(bond);
            }
        }

        return bonds.toArray(new int[][]{});
    }
}
```

#### Cleaning up the tests

The current suite of tests now contain duplicates. Time to remove them. The following tests were selected to be removed:

```
assertArrayEquals(new int[][]{{0, 0}}, NumberBond.find(0, new int[]{0, 0}));
assertArrayEquals(new int[][]{{0, 1}}, NumberBond.find(1, new int[]{0, 0, 1}));
assertArrayEquals(new int[][]{{1, 0}}, NumberBond.find(1, new int[]{1, 0, 1}));
```

The following tests now cover all the requirements:

```
assertArrayEquals(new int[][]{}, NumberBond.find(0, new int[]{}));
assertArrayEquals(new int[][]{{1, 1}}, NumberBond.find(2, new int[]{1, 2, 1}));
assertArrayEquals(new int[][]{}, NumberBond.find(2, new int[]{1, 2}));
assertArrayEquals(new int[][]{{3, 1}, {4, 0}}, NumberBond.find(4, new int[]{3, 4, 1, 0, 4}));
```

#### Conclusion

I have found it is important to ask the right question, at the right time to make best use of the Transformation 
Priority Premise. On earlier attempts, I ended up at a fork in the road. The changes required were significant and 
carried more risk of breaking other tests or behaviour. Changes should be kept minimal, with the fewest number of
keystrokes, otherwise it would run the risk of being too complex and too risky.

What I liked about the Transformation Priority Premise is that it forces me to think of a test and transformation that 
is quick to implement, least complex and less risky. At times I could have chosen a lower transformation, but when I did 
that in previous attempts I ended up getting stuck.

Maintaining a higher transformation over a lower one is achievable, but in one case I found it proved more complicated
than a lower transformation. Perhaps there are trades offs between always choosing a higher transformation over a lower
one, and if the complexity of the higher one is greater than a lower one.