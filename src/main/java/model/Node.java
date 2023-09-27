package model;

import java.util.ArrayList;

public class Node {
    private int val;
    private ArrayList<Edge> edges;
    char label;
    public Node(char label, int val) {
        this.label = label;
        this.val = val;
    }

}
