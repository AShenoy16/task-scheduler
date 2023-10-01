package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Task> tasks;
    private int numProcessors;
    private int cost;

    /**
     * This creates a new schedule given a list of tasks
     *
     * @param tasks A list of tasks to add to a schedule
     * @param numProcessors The number of processors
     */
    public Schedule(List<Task> tasks, int numProcessors) {
        this.numProcessors = numProcessors;
        this.tasks = tasks;
    }

    /**
     * This creates a new initial schedule with only one task (entry node)
     *
     * @param intialTask The initial task to add
     * @param numProcessors The number of processors
     */
    public Schedule(Task intialTask, int numProcessors){
        this.tasks = new ArrayList<>(List.of(intialTask));
        this.numProcessors = numProcessors;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * This method gets all of the nodes in the schedule
     *
     * @return A list of nodes in the schedule
     */
    public ArrayList<Node> getAllNodes(){
        ArrayList<Node> allNodes = new ArrayList<>();
        for(Task task: tasks){
            allNodes.add(task.getNode());
        }

        return allNodes;
    }

    public int getCost() {
        return cost;
    }

    /**
     * This method checks if the schedule is complete given the input graph
     *
     * @param graph The input graph
     * @return True if the schedule is complete, False if not
     */
    public boolean isCompleteSchedule(Graph graph){
        return(tasks.size() == graph.getAdjacencyMatrix().length);
    }



}
