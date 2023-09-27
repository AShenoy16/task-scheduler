package model;

import java.util.ArrayList;

public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
    private ArrayList<Integer> nodeWeightings;
    private ArrayList<Integer> startNodes = new ArrayList<>();
    private ArrayList<Integer> endNodes = new ArrayList<>();
    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.n = nodes.size();
        adjacencyMatrix = new int[n][n];
        mapNodeLabels(nodes);
        fillAdjacencyMatrix(edges);
        findStartNodes();
        findEndNodes();
    }

    public int getN() {
        return n;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public ArrayList<Integer> getStartNodes() {
        return startNodes;
    }

    public ArrayList<Integer> getEndNodes() {
        return endNodes;
    }

    public ArrayList<Integer> getNodeWeightings() {
        return nodeWeightings;
    }

    private void mapNodeLabels(ArrayList<Node> nodes) {
        nodeWeightings = new ArrayList<>(n);
        for (Node n : nodes) {
            nodeWeightings.add(n.getId(), n.getVal());
        }
    }
    private void fillAdjacencyMatrix(ArrayList<Edge> edges) {
        for (Edge e : edges) {
            adjacencyMatrix[e.getSrcId()][e.getDestId()] = e.getWeight();
        }
    }

    //TODO Implement getChildrenNodes method
    public ArrayList<Node> getChildrenNodes(Node node) {
        ArrayList<Node> childrenNodes = new ArrayList<>();
        return childrenNodes;
    }

        private void findStartNodes() {
        for (int j = 0;  j < n; j++) {
            int count = 0;
            for (int i = 0; i < n; i++) {
                if (adjacencyMatrix[i][j] == 0) {
                    count++;
                }
            }

            if (count == n) {
                startNodes.add(j);
            }
        }
    }
    private void findEndNodes() {
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] == 0) {
                    count++;
                }
            }

            if (count == n) {
                endNodes.add(i);
            }
        }
    }
}
