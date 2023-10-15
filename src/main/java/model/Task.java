package model;

import java.util.Objects;

/**
 * Class represents a Task object
 */
public class Task {

    private Node node;
    private int startTime;
    private int finishTime;
    private int processor;

    /**
     * Creates a task instance
     * @param node
     * @param startTime
     * @param finishTime
     * @param processor
     */
    public Task(Node node, int startTime, int finishTime, int processor){
        this.node = node;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.processor = processor;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getProcessor() {
        return processor;
    }

    public Node getNode() {
        return node;
    }

    /**
     * Two nodes are equal if they have the same start time
     * processor and Node id
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return startTime == task.startTime && processor == task.processor && Objects.equals(node.getId(), task.node.getId());
    }

    /**
     * Hashcode of the tasks
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(node.getId(), startTime, processor);
    }
}
