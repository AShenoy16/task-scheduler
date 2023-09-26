package model;

public class Edge {
    private Node src;
    private Node dest;
    private int weight;

    public Edge(Node src, Node dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}
