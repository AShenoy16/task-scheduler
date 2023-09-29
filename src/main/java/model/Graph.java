package model;

import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
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
    private ArrayList<Node> getChildrenNodes(Node node) {
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

    private ArrayList<Node> getParentNodes(Node node) {
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


//    public ArrayList<Node> getValidChildrenNodes(Schedule schedule) {
//        ArrayList<Node> childrenNodes = new ArrayList<>();
//
//        ArrayList<Task> tasks = schedule.getTasks();
//        HashSet<Node> taskNodes = new HashSet<>();
//
//        // Create a set of nodes from the tasks for efficient lookup
//        for (Task task : tasks) {
//            Node node = task.getNode();
//            taskNodes.add(node);
//        }
//
//        for (Task task : tasks) {
//            Node node = task.getNode();
//            ArrayList<Node> children = getChildrenNodes(node);
//
//            for (Node child : children) {
//                ArrayList<Node> parents = getParentNodes(child);
//
//                boolean allParentsInTasks = true;
//
//                for (Node parent : parents) {
//                    // Check if all parents are in the current tasks using the taskNodes set
//                    if (!taskNodes.contains(parent)) {
//                        allParentsInTasks = false;
//                        break; // No need to continue checking parents if one is not in tasks
//                    }
//                }
//
//                if (allParentsInTasks) {
//                    childrenNodes.add(child);
//                }
//            }
//        }
//
//        return childrenNodes;
//    }




}
