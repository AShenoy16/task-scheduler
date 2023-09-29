package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private List<Task> tasks;
    private int numProcessors;

    private int cost;
    private Task task;


    // need to create new instances each time to represent each new partial solution

    // this
    public Schedule(List<Task> tasks, int numProcessors) {
        this.numProcessors = numProcessors;
        this.tasks = tasks;
        //TODO maybe optimise
        this.cost = Integer.MAX_VALUE;
    }

    // first entry node added
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
    // create a new schedule
    // create first schedule into
    // intial state = empty = nothing inside in


    // get all the nodes in a current schedule
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

    public boolean isCompleteSchedule(Graph graph){

        return(tasks.size() == graph.getAdjacencyMatrix().length);
    }



}
