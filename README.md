# Number Bond Kata

## Transformation Priority Premise

This kata applies the ideas presented in the [Transformation Priority Premise](http://blog.cleancoder.com/uncle-bob/2013/05/27/TheTransformationPriorityPremise.html)
by Uncle Bob and attempts to reenact decisions that were made in the [Prime Factor Kata](http://butunclebob.com/ArticleS.UncleBob.ThePrimeFactorsKata)
to further my understanding.

### Transformations

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

Refer to [REQUIREMENTS.md](REQUIREMENTS.md) for the first set of requirements.

#### Null -> Constant transformation

Looking at the requirements, it is obvious which test case to pass first. It requires very little code and covers the 
first four test cases. 

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

The next most interesting case is the one that returns a number bond, in this case `[0,0]`.

```
assertArrayEquals(new int[]{0,0}, NumberBond.find(0, new int[]{0,0}));
```

In TDD we want to pass a failing test as quickly as possible with the fewest number of keystrokes. It makes sense that 
the next transformation is Constant -> Variable. We pass the current failing test by returning the input.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        return s;
    }
}
```

So far, so good. But there is still no behaviour.

#### Split Flow transformation

A number bond is a pair of two numbers. Therefore, it makes sense to choose a test case where the input contains three 
numbers, but only two of them are returned.

```
assertArrayEquals(new int[]{1,1}, NumberBond.find(2, new int[]{1,1,1}));
```

The following code passes the current failing test, but it fails the test case where the result should be empty.

```
static class NumberBond {
    static int[] find(int t, int[] s) {
        return new int[]{s[0], s[1]};
    }
}
```

By splitting the flow of execution we can pass the previous failing test.  Performing a length check helps us determine
if number bond(s) exists or not.

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

We now have multiple constants, `return new int[]{}` and `int[]{s[0], s[1]}`. The latter assumes that the first two 
numbers in the sequence are always the result.

#### Add Computation

The current implementation lacks any logic to determine if the number bond is indeed equal to the target number. This
should be the next test case and transformation(s). For the test to pass, we need to add computation that will calculate 
the sum of two numbers. If, and only if, the numbers bonds are equal, then add to the result.

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

The above implementation uses Variable, Assign, Add Computation and Split Flow transformations. The primitive array 
adds complexity, due to the fact that an array must be initialised with a fixed length. In this case, 2. But the code 
still assumes that the first two numbers are either equal to, or not equal to the target number.

#### If -> While transformation

A new test case and transformation(s) are needed. One that will break the assumption that the first two numbers are the 
number bonds.

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

#### Array -> Container transformation

Whilst the implementation is more generalised, it still contains a fundamental flaw. It still assumes that the number
bonds are adjacent to one another. The following test case and transformation(s) will help us break this assumption.

The primitive array lacks basic functionality that their `Object` counterparts provide, such as `contains`, `add`, 
`remove`, etc.

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

`Contains` does not take into account that a number has already been 
used. This means that passing in `[1,2]` will still produce a result of `[1,1]`.

Time for another test:

```
assertArrayEquals(new int[]{}, NumberBond.find(2, new int[]{1,2}));
```

Removing elements from a `List` while it is being iterated over will produce an `IndexOutOfBoundsException`, and 
therefore not suitable. There is a `Collection` type whereby we can safely remove elements, without producing an 
exception.

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

With a small amount of change we were able to solve this easily. Using the `poll` method we can retrieve elements from
the `Queue` without producing an exception. The `while` condition now only focuses on the fact that the `Queue` is not 
empty.

#### Interim commentary

After having made several attempts at this kata, I discovered that by not asking the right question at the right time,
I would end up at a fork in the road. Or choosing a lower transformation(s) than I like. The above codes feels clean, 
simple and elegant.

## Change of requirements

Refer to [REQUIREMENT_CHANGE_1.md](REQUIREMENT_CHANGE_1.md) for the next set of requirements.

#### Variable -> Array

The first and most obvious change is the need to return a series of number bond pairs. They appear in the order they are 
discovered. The current fixed length primitive array will no longer be suitable, as the result(s) will vary depending on 
how many number bonds are discovered. It is quick to assume that a expandable `List` would be more fitting, but is there 
another way? Uncle Bob recommends choosing higher transformations over lower ones. 

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

This change causes two existing tests to fail:

```
assertArrayEquals(new int[]{1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{0, 1}, NumberBond.find(1, new int[]{0, 0, 1}));
```

Updating existing tests to take into account of the requirement change is easy.

```
assertArrayEquals(new int[]{1, 1, 1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{0, 1, 0, 1}, NumberBond.find(1, new int[]{0, 0, 1}));
```

The latter now tests for the condition that number bonds appear in the order they are first discovered.

After reviewing all the tests it was now safe to remove some redundant ones:

```
assertArrayEquals(new int[]{1, 1, 1, 1}, NumberBond.find(2, new int[]{1, 1, 1}));
assertArrayEquals(new int[]{}, NumberBond.find(0, new int[]{1, 1}));
```

#### Interim commentary

Java 8 Streams make the code a little easier to read, but the implementation is not perfect. I'm not entirely satisfied
with my choice of using a higher transformation over a lower one as I believe it has made the code a little more complex 
and harder to read. I wanted to demonstrate that there is always an alternative solution and that the obvious one may 
not always simpler.

#### Alternative solutions

_Using a combination of Variable, Assign, Add Computation and Arrays utils._

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

_Using a combination of Container with Java 8 Streams._

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

## Final change of requirements

Refer to [REQUIREMENT_CHANGE_2.md](REQUIREMENT_CHANGE_2.md) for the next set of requirements.

The first and most obvious change is the need to return a multi-dimensional array of integers. All existing tests will
fail, so for now we will comment them out and write a new one to cover the changes; then fix the failing tests after to 
prove that the code has not changed behaviour. Additionally, there is the unique constraint on the sequence that a 
number cannot be paired with another number more than once, regardless if it makes up a number bond or not.

#### Coping with change

If a change or change of requirement requires significant behaviour or structural modifications, it is therefore likely
due to the wrong question having being asked, or a lower transformation had been chosen whereby a higher one could had 
been simpler.

So far, the changes had been minimal. By actively choosing a higher transformation over a lower one, we were able to
keep changes to a minimum. There was one or two occasions where we needed to backtrack to a simpler test instead of 
pursuing one that would require significant changes.

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

The following tests were identified as being redundant and therefore could be safely removed:

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

I have found it is important to ask the right question, at the right time, to make the best use of the _Transformation 
Priority Premise_. On earlier attempts, I ended up at a fork in the road. The changes required were significant and 
carried more risk of breaking other tests or behaviour. Changes should be kept minimal with the fewest number of
keystrokes.

What I liked about the _Transformation Priority Premise_ is that it forced me to think of a test and transformation(s) 
that is quick to implement, least complex and less risky. At times I could have chosen a lower transformation, but when I did that in previous attempts I ended up getting stuck.

Maintaining a higher transformation over a lower one is achievable, but in one case I found it proved more complicated
than a lower transformation. Perhaps there are trades offs between always choosing a higher transformation over a lower
one, and if the complexity of the higher one is greater than a lower one.