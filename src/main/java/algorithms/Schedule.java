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

    public int getNumProcesses() {
        return numProcesses;
    }

    public void setNumProcesses(int numProcesses) {
        this.numProcesses = numProcesses;
    }

    public List<ScheduledTask> getScheduledTaskList() {
        return scheduledTaskList;
    }

    public void setScheduledTaskList(List<ScheduledTask> scheduledTaskList) {
        this.scheduledTaskList = scheduledTaskList;
    }

    public int getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(int shortestPath) {
        this.shortestPath = shortestPath;
    }

}
