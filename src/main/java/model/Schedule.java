package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private ArrayList<Task> tasks;
    private int numProcessors;


    public Schedule(ArrayList<Task> tasks, int numProcessors) {
        this.numProcessors = numProcessors;
        this.tasks = tasks;
    }

    public ArrayList<Task> getTask() {
        return tasks;
    }


}
