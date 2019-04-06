package rbt;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

public class RBTTest {
    private Random random = new Random();

    /**
     * @return [rbt, dataArray]
     */
    private <T> Object[] render(int size, Class<T> dataClazz, Supplier<T> dataSupplier, Comparator<T> dataCmp) {
        @SuppressWarnings("unchecked")
        T[] data = (T[]) Array.newInstance(dataClazz, size);
        for (int i = 0; i < size; i++) {
            data[i] = dataSupplier.get();
        }
        Arrays.sort(data, dataCmp);

        RBT<T> rbt = new RBT<>(dataCmp);
        for (int i = 0; i < size; i++) {
            rbt.insert(data[i]);
        }
        return new Object[]{rbt, data};
    }

    private <T> void test(Class<T> dataClazz, Supplier<T> dataSupplier, Comparator<T> dataCmp) {
        final int testCnt = 1024;
        final int maxSize = 1024;
        final int minSize = 10;

        for (int i = 0; i < testCnt; i++) {
            Object[] testData = render(random.nextInt(maxSize - minSize) + minSize, dataClazz, dataSupplier, dataCmp);
            @SuppressWarnings("unchecked")
            RBT<T> rbt = (RBT<T>) testData[0];
            @SuppressWarnings("unchecked")
            T[] data = (T[]) testData[1];

            ArrayList<Integer> delInd = new ArrayList<>();
            try {
                int ind = 0;
                for (T x : rbt) {
                    assert data[ind].equals(x) : data[ind] + ", " + x;
                    ind++;
                }
                ind = 0;
                boolean[] del = new boolean[data.length];
                Iterator<T> it = rbt.iterator();
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
                    T next = it.next();
                    assert next.equals(data[ind]) : next + ", " + data[ind];
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

    @Test
    public void test1() {
        test(Integer.class, () -> random.nextInt(), Comparator.comparingInt(x -> x));
        test(Double.class, () -> random.nextDouble(), Comparator.comparingDouble(x -> x));
        test(TestClass.class, () -> new TestClass(random.nextInt()), Comparator.comparingInt(x -> x.n));
    }

    private class TestClass {
        int n;

        TestClass(int n) {
            this.n = n;
        }
    }
}
