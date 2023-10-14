package algorithm.branchandbound;

import model.Node;
import model.Task;

import java.util.Objects;

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

    public Task toTask(){
        return new Task(this.node, this.startTime, this.startTime + this.node.getVal(), this.processorId);
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
    // if two schduledTasks have same startTime, processorId and node Id they're equivalent
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledTask that = (ScheduledTask) o;
        return startTime == that.startTime && processorId == that.processorId && Objects.equals(node.getId(), that.node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, processorId, node.getId());
    }
}
