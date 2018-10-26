package kata.numberbond;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class NumberBondUTest {

    @Test
    void findNumberBond() {
        assertArrayEquals(new int[][]{}, NumberBond.find(0, new int[]{}));
        assertArrayEquals(new int[][]{{1, 1}}, NumberBond.find(2, new int[]{1, 2, 1}));
        assertArrayEquals(new int[][]{}, NumberBond.find(2, new int[]{1, 2}));
        assertArrayEquals(new int[][]{{3, 1}, {4, 0}}, NumberBond.find(4, new int[]{3, 4, 1, 0, 4}));
    }

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
}