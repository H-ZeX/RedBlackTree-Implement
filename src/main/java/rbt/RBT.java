package rbt;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class RBT<T> implements Iterable<T> {
    private final Node nil = new Node(null, false);
    private final Comparator<? super T> cmp;
    private Node root = nil;

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

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    private void remove(Node z) {
        assert z != nil;
        Node x;
        boolean yoc;
        if (z.right == nil) {
            transplant(z, z.left);
            x = z.left;
            yoc = z.red;
        } else {
            Node sr = successor(z);
            assert sr != nil;
            x = sr.right;
            yoc = sr.red;
            if (sr.parent != z) {
                transplant(sr, x);
                sr.right = z.right;
                assert z.right != nil;
                z.right.parent = sr;
            } else {
                x.parent = sr;
            }
            sr.left = z.left;
            if (z.left != nil) {
                z.left.parent = sr;
            }
            sr.red = z.red;
            transplant(z, sr);
        }
        if (!yoc) {
            fixAfterRemove(x);
        }
    }

    private void fixAfterRemove(Node x) {
        while (x != root && !x.red) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                // x has double black,
                // so its brother should not be nil,
                // even if x is nil
                assert w != nil;
                if (w.red) {
                    assert !x.parent.red;
                    x.parent.red = true;
                    w.red = false;
                    leftRotate(x.parent);
                } else {
                    if (!w.left.red && !w.right.red) {
                        w.red = true;
                        x = x.parent;
                    } else if (w.left.red && !w.right.red) {
                        w.red = true;
                        w.left.red = false;
                        rightRotate(w);
                    } else {
                        w.red = x.parent.red;
                        w.right.red = false;
                        x.parent.red = false;
                        leftRotate(x.parent);
                        x = root;
                    }
                }
            } else {
                Node w = x.parent.left;
                assert w != nil;
                if (w.red) {
                    assert !x.parent.red;
                    x.parent.red = true;
                    w.red = false;
                    rightRotate(x.parent);
                } else {
                    if (!w.left.red && !w.right.red) {
                        w.red = true;
                        x = x.parent;
                    } else if (!w.left.red && w.right.red) {
                        w.red = true;
                        w.right.red = false;
                        leftRotate(w);
                    } else {
                        w.red = x.parent.red;
                        w.left.red = false;
                        x.parent.red = false;
                        rightRotate(x.parent);
                        x = root;
                    }
                }
            }
        }
        x.red = false;
    }

    /**
     * @return nil if no successor
     */
    private Node successor(Node node) {
        assert node != null;
        assert node != nil;
        assert node.right != null;
        if (node.right == nil) {
            Node p = node.parent;
            while (p != nil && p.right == node) {
                p = p.parent;
                node = node.parent;
            }
            return p;
        } else {
            Node n = node.right;
            while (n.left != nil) {
                n = n.left;
            }
            return n;
        }
    }

    private void fixAfterInsert(Node n) {
        assert n != null;
        assert n != nil;
        assert n != root;

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

    private void transplant(Node n1, Node n2) {
        assert n1 != null;
        assert n2 != null;
        assert n1 != nil;

        if (n1.parent == nil) {
            root = n2;
        } else {
            if (n1.parent.left == n1) {
                n1.parent.left = n2;
            } else {
                n1.parent.right = n2;
            }
        }
        n2.parent = n1.parent;
    }

    /**
     * @return the black high
     */
    public int verify() {
        return verify(root);
    }

    // TODO, just for test
    /**
     * @return return the black high
     */
    private int verify(Node n) {
        assert n != null;
        assert !n.red;

        assert !nil.red;
        assert nil.left == null;
        assert nil.right == null;

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
        assert lh == rh : lh + " " + rh;
        return lh + 1;
    }

    // TODO, just for test
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
        private Node next = nil;
        private Node cur = nil;

        MyIterator() {
            Node n = root;
            while (n != nil) {
                next = n;
                n = n.left;
            }
        }

        @Override
        public boolean hasNext() {
            assert next != null;
            return next != nil;
        }

        @Override
        public T next() {
            if (next == nil) {
                throw new NoSuchElementException();
            }
            cur = next;
            next = next(next);
            return cur.value;
        }

        /**
         * @return nil if no next
         */
        private Node next(Node n) {
            assert n != nil;
            if (n.right != nil) {
                n = n.right;
                while (n.left != nil) {
                    n = n.left;
                }
            } else {
                while (n.parent != nil && n.parent.right == n) {
                    n = n.parent;
                }
                n = n.parent;
            }
            return n;
        }

        @Override
        public void remove() {
            if (cur == nil) {
                throw new IllegalStateException();
            }
            RBT.this.remove(cur);
            cur = nil;
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
