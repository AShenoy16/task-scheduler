package algorithms.branchandbound;

import model.Node;

/**
 * This ScheduleTask class essentially represents a node with more attributes.
 */
public class ScheduledTask {
    private int startTime;
    private int processorId;
    private Node node;
    private ScheduledTask parent;

    public ScheduledTask(int startTime, int processorId, Node node,ScheduledTask parent){
        this.startTime = startTime;
        this.processorId = processorId;
        this.node = node;
        this.parent = parent;
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
}
