package model;

import algorithm.astar.CalculateCostFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
    //Maybe implement hashmap instead as its easier
//    private HashMap<Node, List<Node>> adjacencyMap = new HashMap<>();
    private ArrayList<Integer> nodeWeightings;
    private ArrayList<Node> startNodes = new ArrayList<>();
    private ArrayList<Node> endNodes = new ArrayList<>();
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

    public ArrayList<Node> getStartNodes() {
        return startNodes;
    }

    public ArrayList<Node> getEndNodes() {
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

    // this is given a schedule and sees what child nodes are valid
    //TODO Implement getChildrenNodes method
    public ArrayList<Node> getChildrenNodes(Node node) {
        ArrayList<Node> childNodes = new ArrayList<>();

        int row = node.getId();

        for (int j = 0; j < n; j++) {
            if (adjacencyMatrix[row][j] > 0) {
                // get the node with correct value
                Node childNode = new Node(j);
                childNodes.add(childNode);
            }
        }

        return childNodes;
    }

    public ArrayList<Node> getParentNodes(Node node) {
        ArrayList<Node> parentNodes = new ArrayList<>();

        int col = node.getId();

        for (int i = 0; i < n; i++) {
            if (adjacencyMatrix[i][col] > 0) {
                //get node with correct getter
                Node parentNode = new Node(i);
                parentNodes.add(parentNode);
            }
        }

        return parentNodes;
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

    /**
     * This will get all the valid children nodes of a particular schedule
     * @param schedule
     * @param sortedBottomList
     * @return
     */
    public ArrayList<Node> getValidChildrenNodes(Schedule schedule, List<Node> sortedBottomList) {
        ArrayList<Node> childrenNodes = new ArrayList<>();
        ArrayList<Node> allNodes = schedule.getAllNodes();
        CalculateCostFunction calculateCostFunction = new CalculateCostFunction();

        boolean flag = true;

        int bottomLevelValue = Integer.MIN_VALUE;

        for (Node node : sortedBottomList) {
            // Check if the node is not in the allNodes
            if (!allNodes.contains(node)) {
                // if not in all nodes, it means it's not in the schedule
                // if it is in all nodes means it is part of scheudle
                // we want max bottom lvl value NOT currently in scheulde
                // if flag is true we update bottomLevelValue
                if(flag){
                    bottomLevelValue = calculateCostFunction.bottomLevelofNode(node);
                }

                flag = false;

                // if there is ever a case where the bottom level value is greater
                // than the current cost function calcualted, it means we've reached
                // a node with a smaller bottom level value, which must be done
                // afterwards, so we break
                if(bottomLevelValue > calculateCostFunction.bottomLevelofNode(node)){
                    break;
                }

                // if bottom level value == then we have something with the same bottom level
                childrenNodes.add(node);

            }
        }

        // return children nodes
        return childrenNodes;
    }





}
