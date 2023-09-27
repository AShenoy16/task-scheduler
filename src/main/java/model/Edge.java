package model;

public class Edge {
    private int srcId;
    private int destId;
    private int weight;

    public Edge(int srcId, int destId, int weight) {
        this.srcId = srcId;
        this.destId = destId;
        this.weight = weight;
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDestId() {
        return destId;
    }

    public int getWeight() {
        return weight;
    }
}
