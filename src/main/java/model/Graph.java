package model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
    private ArrayList<Node> startNodes = new ArrayList<>();
    private ArrayList<Node> endNodes = new ArrayList<>();
    private ArrayList<Node> nodes;
    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.n = nodes.size();
        this.nodes = nodes;
        adjacencyMatrix = new int[n][n];
        fillAdjacencyMatrix(edges);
        findStartNodes(nodes);
        findEndNodes(nodes);
    }

    public int getN() {
        return n;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public ArrayList<Node> getStartNodes() {
        return startNodes;
    }

    public ArrayList<Node> getEndNodes() {
        return endNodes;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    private void fillAdjacencyMatrix(ArrayList<Edge> edges) {
        for (Edge e : edges) {
            adjacencyMatrix[e.getSrcId()][e.getDestId()] = e.getWeight();
        }
    }

    private void findStartNodes(List<Node> nodes) {
        for (int j = 0;  j < n; j++) {
            int count = 0;
            for (int i = 0; i < n; i++) {
                if (adjacencyMatrix[i][j] == 0) {
                    count++;
                }
            }

            if (count == n) {
                startNodes.add(nodes.get(j));
            }
        }
    }
    private void findEndNodes(List<Node> nodes) {
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] == 0) {
                    count++;
                }
            }

            if (count == n) {
                endNodes.add(nodes.get(i));
            }
        }
    }
}
