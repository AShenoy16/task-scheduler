package model;

import java.util.Objects;

public class Node {
    private int id;
    private int val;
    public Node(int id, int val) {
        this.id = id;
        this.val = val;
    }

    public int getId() {
        return id;
    }

    public int getVal() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && val == node.val;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, val);
    }
}
