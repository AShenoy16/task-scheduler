package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private ArrayList<Task> tasks;
    private int numProcessors;

    private int cost;
    private Task task;

    // need to create new instances each time to represent each new partial solution

    // this
    public Schedule(ArrayList<Task> tasks, int numProcessors) {
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    // create a new schedule
    // create first schedule into
    // intial state = empty = nothing inside in




}
