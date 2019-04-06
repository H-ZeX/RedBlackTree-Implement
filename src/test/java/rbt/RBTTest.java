package rbt;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

public class RBTTest {
    private Random random = new Random();

    private <T> T[] renderData(int size, Class<T> dataClazz, Supplier<T> dataSupplier) {
        @SuppressWarnings("unchecked")
        T[] data = (T[]) Array.newInstance(dataClazz, size);
        for (int i = 0; i < size; i++) {
            data[i] = dataSupplier.get();
        }
        return data;
    }

    private <T> void test(Class<T> dataClazz, Supplier<T> dataSupplier, Comparator<T> dataCmp) {
        final int testCnt = 1024;
        final int maxSize = 1024;
        final int minSize = 10;
        final int roundCnt = 128;

        class Container {
            private T data;
            private long id;

            public Container(T data, long id) {
                this.data = data;
                this.id = id;
            }
        }
        for (int i = 0; i < testCnt; i++) {
            ArrayList<T> delList = new ArrayList<>();
            ArrayList<T> addList = new ArrayList<>();
            T[] data = renderData(random.nextInt(maxSize - minSize) + minSize, dataClazz, dataSupplier);
            long id = 0;
            RBT<T> rbt = new RBT<>(dataCmp);
            HashMap<T, ArrayList<Long>> map = new HashMap<>();
            TreeSet<Container> tree = new TreeSet<>((x, y) -> {
                int t = dataCmp.compare(x.data, y.data);
                return t == 0 ? Long.compare(x.id, y.id) : t;
            });
            for (T x : data) {
                map.put(x, new ArrayList<>());
            }
            try {
                for (int j = 0; j < roundCnt; j++) {
                    for (T x : data) {
                        rbt.insert(x);
                        tree.add(new Container(x, id));
                        ArrayList<Long> list = map.get(x);
                        list.add(id);
                        map.put(x, list);
                        id++;
                        addList.add(x);
                        delList.add(null);
                        if (random.nextInt(10) == 1) {
                            break;
                        }
                    }
                    Iterator<T> it = rbt.iterator();
                    for (Container x : tree) {
                        assert it.hasNext();
                        T t = it.next();
                        assert x.data.equals(t) : x.data + ", " + t;
                    }
                    it = rbt.iterator();
                    while (it.hasNext()) {
                        T x = it.next();
                        if (random.nextBoolean()) {
                            ArrayList<Long> list = map.get(x);
                            long t = list.get(list.size() - 1);
                            list.remove(list.size() - 1);
                            tree.remove(new Container(x, t));
                            it.remove();
                            delList.add(x);
                            addList.add(null);
                        }
                    }
                    it = rbt.iterator();
                    for (Container x : tree) {
                        assert it.hasNext();
                        T t = it.next();
                        assert x.data.equals(t) : x.data + ", " + t;
                    }
                }
            } catch (Throwable throwable) {
                System.out.println("delList: ");
                System.out.println(delList);
                System.out.println("addList: ");
                System.out.println(addList);
                System.out.println("data: ");
                System.out.println(Arrays.toString(data));
                System.out.flush();
                throw throwable;
            }
        }
    }

    @Test
    public void test1() {
        test(Integer.class, () -> random.nextInt(10), Comparator.comparingInt(x -> x));
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
