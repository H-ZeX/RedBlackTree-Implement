package rbt;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

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
    }

    private void fixAfterInsert(Node n) {
        // if (!n.parent.red) {
        //     return;
        // }
        // // for that n.parent is red, so n.parent.parent is always not null;
        // if (n.parent == n.parent.parent.left) {
        //
        // }
        Node s = n.parent;
        if (random.nextBoolean() && s.right != nil) {
            leftRotate(s);
        }
        // if (random.nextBoolean() && s.left != nil) {
        //     rightRotate(s);
        // }
    }

    private Node rightRotate(Node n) {
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
        assert nl.right != null;
        if (nl.right != nil) {
            transplant(nl.right, n);
        } else {
            nl.right = n;
            n.parent = nl;
        }
        return nl;
    }

    private Node leftRotate(Node n) {
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
        assert nr.left != null;
        if (nr.left != nil) {
            transplant(nr.left, n);
        } else {
            nr.left = n;
            n.parent = nr;
        }
        return nr;
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

    private class MyIterator implements Iterator<T> {
        private Node next = root;
        private Node cur = null;

        MyIterator() {
            while (next.left != null) {
                next = next.left;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            Node current = next;
            cur = current;
            if (next.right != null) {
                next = next.right;
                while (next.left != null) {
                    next = next.left;
                }
            } else {
                while (next.parent != null && next.parent.right == next) {
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
