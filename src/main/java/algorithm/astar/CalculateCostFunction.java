package algorithm.astar;

import algorithm.branchandbound.PartialSolution;
import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.*;

import static java.lang.Math.max;

public class CalculateCostFunction {
    // possibly make this class a singleton - reasoning - because we will need to use the getScheduleCost method
    // Maybe make method into a static method?
    private Graph graph;
    private HashMap<Node,Integer> bottomLevelMap = new HashMap<>();

    public HashMap<Node,Integer> getBottomLevelMap(){
        return bottomLevelMap;
    }

    public CalculateCostFunction() {}

    public CalculateCostFunction(Graph graph) {
        this.graph = graph;
    }

    private static CalculateCostFunction instance;

    public static CalculateCostFunction getInstance(Graph graph) {
        if (instance == null) {
            instance = new CalculateCostFunction(graph);
        }
        return instance;
    }

    /**
     * This method fills the bottomLevelMap hashmap with nodes and their bottom level values.
     * It will take in a node and set the bottom level values for all descendant nodes including itself.
     *
     * @param node The node to set the bottom level value
     * @return The bottom level value
     */
    public int setBottomLevelMap(Node node){
        for (Node child : graph.getChildrenNodes(node)) {
            // Recursive call to start from exit nodes
            setBottomLevelMap(child);
        }

        int maxBottomLevelValue = 0;
        for(Node childNode : graph.getChildrenNodes(node)){
            if(bottomLevelMap.containsKey(childNode)){
                maxBottomLevelValue = Math.max(maxBottomLevelValue, bottomLevelMap.get(childNode));
            }
        }

        // Calculate bottom level value
        int bottomLevel = maxBottomLevelValue + node.getVal();

        // Save bottom level value to map
        bottomLevelMap.put(node,bottomLevel);

        return bottomLevel;
    }

    /**
     * This method sets the cost value for a given schedule
     * @param currentSchedule The schedule
     */
    public void setScheduleCost(Schedule currentSchedule){
        int cost = 0;
        List<Task> tasks = currentSchedule.getTasks();

        for (Task task: tasks){
            int startTime = task.getStartTime();
            // calculate lower bound
            // heuristic = max(start time of scheduled tasks plus their bottom level)
            cost = Math.max(cost, startTime + bottomLevelMap.get(task.getNode()));

        }
        int sumOfNodeWeights = 0;
        int trailTimes = 0;
        int idleTimeHeuristic = 0;
        for (int i = 0; i < graph.getNodeWeightings().length; i++) {
            sumOfNodeWeights += graph.getNodeWeightings()[i];
        }
        for (int i = 1; i <= currentSchedule.getNumProcessors(); i++) {
            int maxFinishTime = 0;
            for(Task task : currentSchedule.getTasks()){
                if(task.getProcessor() != i){
                    continue;
                }
                // get the maximum finish time for a processor
                maxFinishTime = Math.max(task.getFinishTime(), maxFinishTime);
            }
            // ending trailtimes for a specific processor
            // will be zero for the processor with the latest scheduled task
            trailTimes += currentSchedule.getFinishTime() - maxFinishTime;
        }

        // idle time from equation
        idleTimeHeuristic = (currentSchedule.getGapTimes() + trailTimes + sumOfNodeWeights)/currentSchedule.getNumProcessors();
        currentSchedule.setCost(Math.max(cost, idleTimeHeuristic));
    }

    public void setPartialSolutionCost(PartialSolution partialSolution){
        int cost = 0;
        List<Task> tasks = partialSolution.getAllTasks();

        for (Task task: tasks){
            int startTime = task.getStartTime();
            // calculate lower bound
            // heuristic = max(start time of scheduled tasks plus their bottom level)
            cost = Math.max(cost, startTime + bottomLevelMap.get(task.getNode()));

        }
        partialSolution.setCost(cost);
    }

    /**
     * This method returns the nodes ordered in descending order by their bottom level
     * as a list of nodes, needed for the start of Astar
     *
     * @return The list of nodes sorted in reverse order by bottom level value
     */
    public List<Node> getSortedBottomLevel() {
        List<Map.Entry<Node, Integer>> bottomLevelList = new ArrayList<>(bottomLevelMap.entrySet());

        // Reversed order sort
        bottomLevelList.sort(Map.Entry.<Node, Integer>comparingByValue().reversed());

        ArrayList<Node> sinkNodes = graph.getEndNodes();
        List<Node> sortedSinkNodes = new ArrayList<>();

        List<Node> bottomLevelOrder = new ArrayList<>();

        // Separate exit and non exit nodes
        for (Map.Entry<Node, Integer> entry : bottomLevelList) {
            Node currentNode = entry.getKey();
            if(sinkNodes.contains(currentNode)){
                sortedSinkNodes.add(currentNode);
                // Need to make sure if it's an exit node the one with the least number of parents is first
            }else{
                // Non exit node
                bottomLevelOrder.add(currentNode);
            }
        }

        // Sort exit nodes by the number of parents in ascending order
        sortedSinkNodes.sort(Comparator.comparingInt(this::countParents));

        // Add sorted sink nodes to the end of the order
        bottomLevelOrder.addAll(sortedSinkNodes);

        return bottomLevelOrder;

    }

    /**
     * This method counts the number of parents of a particular node
     * @param node The node
     * @return The number of parents
     */
    private int countParents(Node node){
        return graph.getParentNodes(node).size();
    }

    public List<Node> getHighestBottomLevelNodes(){
        // Find the highest value in the HashMap, this value should belong to an entry node
        int maxValue = Integer.MIN_VALUE;
        for (Integer value : bottomLevelMap.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Collect nodes associated with the highest value
        List<Node> nodesWithMaxValue = new ArrayList<>();
        for (Map.Entry<Node, Integer> entry : bottomLevelMap.entrySet()) {
            if (entry.getValue() == maxValue) {
                nodesWithMaxValue.add(entry.getKey());
            }
        }

        return nodesWithMaxValue;
    }

    /**
     * This method gets the bottom level value of a particular node
     * @param node The node
     * @return The bottom level value
     */
    public int bottomLevelofNode(Node node){
        return bottomLevelMap.get(node);
    }
}
