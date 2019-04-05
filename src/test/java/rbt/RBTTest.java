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
    private Object[] render(int maxSize, int minSize) {
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

    @Test
    public void test1() {
        final int testCnt = 1024;
        final int maxSize = 1024;
        final int minSize = 10;
        for (int i = 0; i < testCnt; i++) {
            Object[] testData = render(maxSize, minSize);
            @SuppressWarnings("unchecked")
            RBT<Double> rbt = (RBT<Double>) testData[0];
            double[] data = (double[]) testData[1];
            int ind = 0;
            for (double x : rbt) {
                assert x == data[ind] : Arrays.toString(data) + "";
                ind++;
            }
            System.out.println("success: " + i);
        }
    }
}
