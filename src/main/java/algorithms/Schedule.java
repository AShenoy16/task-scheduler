package algorithms;

import java.util.List;

public class Schedule {
    private int numProcesses;
    private List<ScheduledTask> scheduledTaskList;

    public Schedule(int numProcesses, List<ScheduledTask> scheduledTaskList){
        this.numProcesses = numProcesses;
        this.scheduledTaskList = scheduledTaskList;
    }
}
