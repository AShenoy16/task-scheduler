package model;

import algorithm.astar.CalculateCostFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a graph object
 */
public class Graph {
    private final int n;
    private int[][] adjacencyMatrix;
    private int[] nodeWeightings;
    private ArrayList<Node> startNodes = new ArrayList<>();
    private ArrayList<Node> endNodes = new ArrayList<>();
    private Node[] nodes;
    private HashMap<Node, List<Node>> dependencies = new HashMap<>();
    private HashMap<Node, List<Node>> parentNodes = new HashMap<>();


    /**
     * Create a graph instance
     * @param nodes
     * @param edges
     */
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

    /**
     * This method a hashmap of dependencies for a node
     * @param node
     */
    public void createDependencies(Node node){


        List<Node> childrenNodes = getChildrenNodes(node);

        for(Node childrenNode: childrenNodes){
            createDependencies(childrenNode);
        }

        this.parentNodes.putIfAbsent(node, initialiseParentNodes(node));
        List<Node> parentNodes = this.parentNodes.get(node);

        // add parents of specific node
        dependencies.put(node, parentNodes);

        // go through all children of that node and add it's ancestors
        for(Node child: childrenNodes){
            dependencies.get(child).addAll(parentNodes);
        }

    }

    public HashMap<Node, List<Node>> getDependencies() {
        return dependencies;
    }

    public List<Node> getDependenciesByNode(Node node) {
        return dependencies.get(node);
    }

    public List<Node> getParentsByNode(Node node) {
        return parentNodes.get(node);
    }



    /**
     * This method gets the children nodes of a particular node
     *
     * @param node The node
     * @return A list of nodes that is the children of the particular node
     */
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

    /**
     * This method gets the parent nodes of a particular node
     *
     * @param node The node
     * @return A list of nodes that is the parent(s) of the particular node
     */
    public List<Node> initialiseParentNodes(Node node) {
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

    public List<Node> getParentNodes(Node node) {

        return this.parentNodes.get(node);
    }
    /**
     * This method sets all the entry nodes
     */
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

    /**
     * This method sets oll the exit nodes
     */
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
     * @param id The id to create Node with
     * @return The created Node
     */
    private Node createNodeById(int id) {
        return new Node(id, nodeWeightings[id]);
    }


    /**
     * This method orders the nodes
     * @param unorderedNodes A list of unordered nodes
     */
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
