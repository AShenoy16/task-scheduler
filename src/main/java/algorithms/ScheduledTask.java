package algorithms;

import model.Graph;
import model.Node;

/**
 * This ScheduleTask class essentially represents a node with more attributes.
 */
public class ScheduledTask {
    private int startTime;
    private int processorId;
    private Integer node;
    private int taskTime;
    private ScheduledTask parent;

    public ScheduledTask(int startTime, int processorId, Integer node, int taskTime ,ScheduledTask parent){
        this.startTime = startTime;
        this.processorId = processorId;
        this.node = node;
        this.parent = parent;
        this.taskTime = taskTime;
    }
    public int getStartTime(){
        return startTime;
    }
    public int getProcessorId(){
        return processorId;
    }

    public int getNode(){
        return node;
    }

    public int getTaskTime(){
        return taskTime;
    }
}
