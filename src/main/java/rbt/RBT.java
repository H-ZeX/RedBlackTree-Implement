package rbt;

import java.util.*;

public class RBT<T> implements Iterable<T> {
    private final Node nil = new Node(null, false);
    private final Comparator<? super T> cmp;
    private Node root = nil;

    // TODO, just for test
    private Random random = new Random();

    public RBT(Comparator<? super T> cmp) {
        this.cmp = cmp;
    }

    public void insert(T element) {
        assert root != null;
        if (root == nil) {
            root = new Node(element, nil, nil, nil, false);
            return;
        }
        Node n = root;
        while (true) {
            if (cmp.compare(element, n.value) <= 0) {
                assert n.left != null;
                if (n.left == nil) {
                    n.left = new Node(element, nil, nil, n);
                    fixAfterInsert(n.left);
                    break;
                } else {
                    n = n.left;
                }
            } else {
                assert n.right != null;
                if (n.right == nil) {
                    n.right = new Node(element, nil, nil, n);
                    fixAfterInsert(n.right);
                    break;
                } else {
                    n = n.right;
                }
            }
        }
        verify(root);
    }

    // TODO, just for test
    public int blackHigh() {
        return verify(root);
    }

    private void fixAfterInsert(Node n) {
        assert n != null;
        assert n != nil;
        assert n != root;

        // Node s = n.parent;
        // if (random.nextBoolean() && s != nil && s.left != nil) {
        //     rightRotate(s);
        // }
        // if (random.nextBoolean() && s != nil && s.right != nil) {
        //     leftRotate(s);
        // }

        while (n.parent.red) {
            // For that n.parent is red,
            // so n.parent.parent is always not nil or null;
            Node gp = n.parent.parent;

            assert !gp.red;
            assert n.red;
            assert n != nil;

            if (n.parent == gp.left) {
                Node uncle = gp.right;
                if (uncle.red) {
                    gp.red = true;
                    uncle.red = false;
                    n.parent.red = false;
                    n = gp;
                } else if (n.parent.right == n) {
                    Node tmp = n.parent;
                    leftRotate(n.parent);
                    assert n.left.red;
                    assert n.left == tmp;
                    n = n.left;
                } else {
                    rightRotate(gp);
                    gp.red = true;
                    n.parent.red = false;
                }
            } else {
                Node uncle = gp.left;
                if (uncle.red) {
                    gp.red = true;
                    uncle.red = false;
                    n.parent.red = false;
                    n = gp;
                } else if (n.parent.left == n) {
                    Node tmp = n.parent;
                    rightRotate(n.parent);
                    assert n.right.red;
                    assert n.right == tmp;
                    n = n.right;
                } else {
                    leftRotate(gp);
                    gp.red = true;
                    n.parent.red = false;
                }
            }
        }
        root.red = false;
    }

    // TODO, just for test
    private int verify(Node n) {
        assert n != null;
        assert !n.red;
        if (n == nil) {
            return 1;
        }
        int lh, rh;
        if (n.left.red) {
            int a = verify(n.left.left);
            int b = verify(n.left.right);
            assert a == b;
            lh = a;
        } else {
            lh = verify(n.left);
        }
        if (n.right.red) {
            int a = verify(n.right.left);
            int b = verify(n.right.right);
            assert a == b;
            rh = a;
        } else {
            rh = verify(n.right);
        }
        assert lh == rh;
        return lh + 1;
    }

    private void rightRotate(Node n) {
        assert n != null;
        assert n != nil;
        assert n.left != null;
        assert n.left != nil;

        Node nl = n.left;
        n.left = nl.right;
        assert nl.right != null;
        if (nl.right != nil) {
            nl.right.parent = n;
        }
        transplant(n, nl);
        // nl.right.parent had changed,
        // so should NOT use transplant here
        nl.right = n;
        n.parent = nl;

    }

    private void leftRotate(Node n) {
        assert n != null;
        assert n != nil;
        assert n.right != null;
        assert n.right != nil;

        Node nr = n.right;
        n.right = nr.left;
        assert nr.left != null;
        if (nr.left != nil) {
            nr.left.parent = n;
        }
        transplant(n, nr);
        nr.left = n;
        n.parent = nr;
    }

    private void transplant(Node x, Node y) {
        assert x != null;
        assert y != null;
        assert x != nil;
        assert y != nil;

        if (x.parent == nil) {
            root = y;
        } else {
            if (x.parent.left == x) {
                x.parent.left = y;
            } else {
                x.parent.right = y;
            }
        }
        y.parent = x.parent;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    public void layout() {
        @SuppressWarnings("unchecked")
        LinkedList<Node>[] lists = (LinkedList<Node>[]) new LinkedList[2];
        lists[0] = new LinkedList<>();
        lists[1] = new LinkedList<>();
        int ind = 0;
        lists[ind].addLast(root);
        boolean allNull;
        do {
            allNull = true;
            while (!lists[ind].isEmpty()) {
                Node n = lists[ind].pop();
                if (n == null) {
                    System.out.print("n ");
                    lists[1 - ind].addLast(null);
                    lists[1 - ind].addLast(null);
                    continue;
                } else {
                    System.out.print(n.value + " ");
                }
                assert n.left != null;
                if (n.left != nil) {
                    allNull = false;
                    lists[1 - ind].addLast(n.left);
                } else {
                    lists[1 - ind].addLast(null);
                }
                assert n.right != null;
                if (n.right != nil) {
                    allNull = false;
                    lists[1 - ind].addLast(n.right);
                } else {
                    lists[1 - ind].addLast(null);
                }
            }
            System.out.println();
            ind = 1 - ind;
        } while (!allNull);
    }

    private class MyIterator implements Iterator<T> {
        private Node next = root;
        private Node cur = nil;

        MyIterator() {
            while (next.left != nil) {
                next = next.left;
            }
        }

        @Override
        public boolean hasNext() {
            return next != nil;
        }

        @Override
        public T next() {
            if (next == nil) {
                throw new NoSuchElementException();
            }
            Node current = next;
            cur = current;
            if (next.right != nil) {
                next = next.right;
                while (next.left != nil) {
                    next = next.left;
                }
            } else {
                while (next.parent != nil && next.parent.right == next) {
                    next = next.parent;
                }
                next = next.parent;
            }
            return current.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class Node {
        T value;
        Node left, right, parent;
        boolean red = true;

        public Node(T value, Node left, Node right, Node parent, boolean red) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
            this.red = red;
        }

        public Node(T value, Node left, Node right, Node parent) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }

        public Node(T value, boolean red) {
            this.value = value;
            this.red = red;
        }

        public Node(T value) {
            this.value = value;
        }
    }
}
