package rbt;

import org.junit.Test;

import java.util.*;

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
        final int maxSize = 1024;
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

    @Test
    public void test2() {
        final int testCnt = 1024;
        final int maxSize = 1024;
        final int minSize = 10;
        for (int i = 0; i < testCnt; i++) {
            ArrayList<Integer> delInd = new ArrayList<>();
            int[] data = null;
            try {
                Object[] testData = renderInt(maxSize, minSize);
                @SuppressWarnings("unchecked")
                RBT<Integer> rbt = (RBT<Integer>) testData[0];
                data = (int[]) testData[1];
                boolean[] del = new boolean[data.length];
                Iterator<Integer> it = rbt.iterator();
                int ind = 0;

                while (it.hasNext()) {
                    it.next();
                    if (random.nextBoolean()) {
                        delInd.add(ind);
                        del[ind] = true;
                        it.remove();
                    }
                    ind++;
                }

                it = rbt.iterator();
                ind = 0;
                while (it.hasNext()) {
                    while (ind < del.length && del[ind]) {
                        ind++;
                    }
                    assert ind < del.length : ind + ", " + data.length;
                    int next = it.next();
                    assert next == data[ind] : next + ", " + data[ind];
                    ind++;
                }
            } catch (Throwable throwable) {
                System.out.println("delIndex: ");
                System.out.println(delInd);
                System.out.println("data: ");
                System.out.println(Arrays.toString(data));
                throw throwable;
            }
        }
    }
}
