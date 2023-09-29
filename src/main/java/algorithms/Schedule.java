package algorithms;

import java.util.List;

/**
 * The schedule class keeps track of all tasks with a scheduled task list. Used at the end of algorithm
 */
public class Schedule {
    private int numProcesses;
    private List<ScheduledTask> scheduledTaskList;
    private int shortestPath;

    public Schedule(int numProcesses, List<ScheduledTask> scheduledTaskList){
        this.numProcesses = numProcesses;
        this.scheduledTaskList = scheduledTaskList;
    }
}
