package algorithm.branchandbound;

import model.Node;

/**
 * This ScheduleTask class essentially represents a node with more attributes.
 */
public class ScheduledTask {
    private int startTime;
    private int processorId;
    private Node node;
    private ScheduledTask parent;
    private int taskLength;

    public ScheduledTask(int startTime, int processorId, Node node,ScheduledTask parent, int taskLength){
        this.startTime = startTime;
        this.processorId = processorId;
        this.node = node;
        this.parent = parent;
        this.taskLength = taskLength;
    }

    public int getStartTime(){
        return startTime;
    }
    public int getProcessorId(){
        return processorId;
    }

    public Node getNode(){
        return node;
    }

    public ScheduledTask getParent() {
        return parent;
    }

    public int getTaskLength() {
        return taskLength;
    }
}
