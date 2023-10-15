package algorithm.astar;

import algorithm.branchandbound.PartialSolution;
import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.*;

import static java.lang.Math.max;


/**
 * This class is used to calculate the cost function for our astar algorithm
 */
public class CalculateCostFunction {

    private Graph graph;
    private HashMap<Node,Integer> bottomLevelMap = new HashMap<>();

    public HashMap<Node,Integer> getBottomLevelMap(){
        return bottomLevelMap;
    }

    /**
     * Default constructor
     */
    public CalculateCostFunction() {}

    public CalculateCostFunction(Graph graph) {
        this.graph = graph;
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
            // heuristic = max(start time of scheduled tasks plus their bottom level)
            cost = Math.max(cost, startTime + bottomLevelMap.get(task.getNode()));

        }

        // this calculates the idle time for a scheduler
        int sumOfNodeWeights = 0;
        int trailTimes = 0;
        int idleTimeHeuristic = 0;

        //idle time
        for (int i = 1; i <= currentSchedule.getNumProcessors(); i++) {
            int maxFinishTime = 0;
            for(Task task : currentSchedule.getTasks()){
                if(task.getProcessor() != i){
                    continue;
                }
                // get the maximum finish time for a processor
                maxFinishTime = Math.max(task.getFinishTime(), maxFinishTime);
                sumOfNodeWeights += task.getNode().getVal();
            }
            // ending trailtimes for a specific processor
            trailTimes += currentSchedule.getFinishTime() - maxFinishTime;
        }

        // idle time from equation
        idleTimeHeuristic = (currentSchedule.getGapTimes() + trailTimes + sumOfNodeWeights)/currentSchedule.getNumProcessors();

        //set cost value to be max of bottom level and heuristic

        currentSchedule.setCost(Math.max(cost, idleTimeHeuristic));
    }

    /**
     * This set the cost value for a partial solution via bottom levels
     * @param partialSolution to set cost value for
     */
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
     * Method gets the nodes with the highest bottom level which are the entry nodes
     * @return list of nodes with the higest bottom level
     */
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
