package model;

import algorithm.astar.CalculateCostFunction;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
    //Maybe implement hashmap instead as its easier
//    private HashMap<Node, List<Node>> adjacencyMap = new HashMap<>();
    private int[] nodeWeightings;
    private ArrayList<Node> startNodes = new ArrayList<>();
    private ArrayList<Node> endNodes = new ArrayList<>();
    private Node[] nodes;

    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.n = nodes.size();
        adjacencyMatrix = new int[n][n];
        fillNodeWeightings(nodes);
        fillAdjacencyMatrix(edges);
        findStartNodes();
        findEndNodes();
        orderNodes(nodes);
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

    public int[] getNodeWeightings() {
        return nodeWeightings;
    }

    /**
     * Fill nodeWeightings array
     * Nodes are mapped by id to index.
     * E.g. the weight of id 3 node will be stored in index 3 of nodeWeightings
     *
     * @param nodes Nodes from dot file to process
     */
    private void fillNodeWeightings(ArrayList<Node> nodes) {
        nodeWeightings = new int[n];
        for (Node n : nodes) {
            nodeWeightings[n.getId()] = n.getVal();
        }
    }

    /**
     * Create outgoing edges adjacency matrix.
     * Row represents source node, column represents destination node
     *
     * @param edges Edges from dot file to process
     */
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
                Node childNode = createNodeById(j);
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
                Node parentNode = createNodeById(i);
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
                startNodes.add(createNodeById(j));
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
                endNodes.add(createNodeById(i));
            }
        }
    }

    /**
     * Construct a new node object by id.
     * Usable when nodeWeightings is filled.
     *
     * @param id id to create Node with
     * @return created Node
     */
    private Node createNodeById(int id) {
        return new Node(id, nodeWeightings[id]);
    }

    /**
     * This will get all the valid children nodes of a particular schedule
     * @param schedule
     * @param sortedBottomList
     * @return
     */
    public ArrayList<Node> getValidChildrenNodes(Schedule schedule, List<Node> sortedBottomList, CalculateCostFunction calculateCostFunction) {
        ArrayList<Node> childrenNodes = new ArrayList<>();
        ArrayList<Node> allNodes = schedule.getAllNodes();

        boolean flag = true;

        int bottomLevelValue = Integer.MIN_VALUE;

        for (Node node : sortedBottomList) {
            // Check if the node is not in the allNodes
            if (!allNodes.contains(node)) {
                // if not in all nodes, it means it's not in the schedule
                // if it is in all nodes means it is part of schedule
                // we want max bottom lvl value NOT currently in schedule
                // if flag is true we update bottomLevelValue
                if(flag){
                    bottomLevelValue = calculateCostFunction.getBottomLevelMap().get(node);
                }

                flag = false;

                // if there is ever a case where the bottom level value is greater
                // than the current cost function calculated, it means we've reached
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

    private void orderNodes(List<Node> unorderedNodes) {
        this.nodes = new Node[unorderedNodes.size()];
        for (Node node : unorderedNodes) {
            nodes[node.getId()] = node;
        }
    }

    public Node[] getNodes() {
        return nodes;
    }

}
