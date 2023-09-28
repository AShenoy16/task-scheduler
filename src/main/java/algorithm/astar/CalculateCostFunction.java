package algorithm.astar;

import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.Math.max;

public class CalculateCostFunction {
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

    public int getScheduleCost(Schedule currentSchedule){

        // loop through all the tasks in a given partial solution
        // can either pass this or a schdule
        int cost = 0;

        ArrayList<Task> tasks = currentSchedule.getTask();


        for (Task task: tasks){


            int startTime = task.getStartTime();

            // calculate lower bound
            // heuristic = max(start time of scheduled tasks plus their bottom level)
            cost = Math.max(cost, startTime + bottomLevelMap.get(task.getNode()));

        }

        return cost;

    }


    public void setCostFunction(){}


}
