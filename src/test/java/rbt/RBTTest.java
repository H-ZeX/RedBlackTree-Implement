package rbt;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class RBTTest {
    private Random random = new Random();

    /**
     * @return [dataArray, rbt]
     */
    private Object[] renderDouble(int maxSize, int minSize) {
        int size = random.nextInt(maxSize - minSize) + minSize;
        double[] data = new double[size];
        RBT<Double> rbt = new RBT<>(Comparator.comparingDouble(x -> x));
        for (int i = 0; i < size; i++) {
            System.out.println("insert: " + i);
            data[i] = random.nextDouble();
            rbt.insert(data[i]);
        }
        Arrays.sort(data);
        return new Object[]{rbt, data};
    }

    private Object[] renderInt(int maxSize, int minSize) {
        int size = random.nextInt(maxSize - minSize) + minSize;
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt();
        }
        // System.out.println(Arrays.toString(data));
        // System.out.flush();

        Arrays.sort(data);
        RBT<Integer> rbt = new RBT<>(Comparator.comparingInt(x -> x));
        for (int i = 0; i < size; i++) {
            rbt.insert(data[i]);
        }
        return new Object[]{rbt, data};
    }

    @Test
    public void test1() {
        final int testCnt = 1024;
        final int maxSize = 10240;
        final int minSize = 10;
        for (int i = 0; i < testCnt; i++) {
            Object[] testData = renderInt(maxSize, minSize);
            @SuppressWarnings("unchecked")
            RBT<Integer> rbt = (RBT<Integer>) testData[0];
            // rbt.layout();
            // System.exit(0);

            int[] data = (int[]) testData[1];
            int ind = 0;
            for (int x : rbt) {
                assert x == data[ind] : x + "\t" + data[ind] + "\n" + Arrays.toString(data) + "";
                ind++;
            }
            System.out.println("success: " + i + ", high: " + rbt.blackHigh());
        }
    }
}
