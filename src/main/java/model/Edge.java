package model;

public class Edge {
    private int srcId;
    private int destId;
    private int weight;

    /**
     * This creates an Edge instance
     * @param srcId source node Id
     * @param destId destination node Id
     * @param weight weight of the edge
     */
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
