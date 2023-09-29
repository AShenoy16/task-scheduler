package algorithm.astar;

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
    public HashMap<Node,Integer> bottomLevelMap = new HashMap<>();

    public HashMap<Node,Integer> getBottomLevelMap(){
        return bottomLevelMap;
    }
    public int setBottomLevelMap(Node node){
        for (Node child : graph.getChildrenNodes(node)) {
            // Recursive call to start from exit nodes
            setBottomLevelMap(child);
        }

        // Get maximum bottom level value out of children nodes
        int maxChildBottomLevel = graph.getChildrenNodes(node).stream()
                .mapToInt(Node::getVal)
                .max()
                .orElse(0);

        // Calculate bottom level value
        int bottomLevel = maxChildBottomLevel + node.getVal();

        // Save bottom level value to map
        bottomLevelMap.put(node,bottomLevel);

        return bottomLevel;
    }

    // this returns cost value for a partial solution, so need to pass in a schedule or tasks
    // in a schedule
    // this will then be added to a priority queue that we run for a*
    // this determines wheter we explore the partial solution further or not

    public void setScheduleCost(Schedule currentSchedule){

        // loop through all the tasks in a given partial solution
        // can either pass this or a schdule
        int cost = 0;

        ArrayList<Task> tasks = currentSchedule.getTasks();


        for (Task task: tasks){

            int startTime = task.getStartTime();

            // calculate lower bound
            // heuristic = max(start time of scheduled tasks plus their bottom level)
            cost = Math.max(cost, startTime + bottomLevelMap.get(task.getNode()));

        }

        currentSchedule.setCost(cost);

    }

    /**
     * This method returns the nodes ordered in descending order by their bottom level
     * as a list of nodes, needed for the start of a*
     * @return
     */
    public List<Node> getBottomLevel() {

        List<Map.Entry<Node, Integer>> bottomLevelList = new ArrayList<>(bottomLevelMap.entrySet());

//        bottomLevelList.sort(Comparator.comparing(Map.Entry::getValue));
        // above should work if this doesn't
        // reversed order sort
        bottomLevelList.sort(Map.Entry.<Node, Integer>comparingByValue().reversed());


        List<Node> bottomLevelOrder = new ArrayList<>();

        for (Map.Entry<Node, Integer> entry : bottomLevelList) {
            bottomLevelOrder.add(entry.getKey());
        }


        return bottomLevelOrder;

    }

    public List<Node> getHighestBottomLevelNodes(){
        // Find the highest value in the HashMap, this valeu should belong to an entry node
        int maxValue = Integer.MIN_VALUE;
        for (Integer value : bottomLevelMap.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Collect keys associated with the highest value
        List<Node> nodesWithMaxValue = new ArrayList<>();
        for (Map.Entry<Node, Integer> entry : bottomLevelMap.entrySet()) {
            if (entry.getValue() == maxValue) {
                nodesWithMaxValue.add(entry.getKey());
            }
        }

        return nodesWithMaxValue;

    }
}
