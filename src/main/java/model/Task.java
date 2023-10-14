package model;

import java.util.Objects;

public class Task {

    private Node node;
    private int startTime;
    private int finishTime;
    private int processor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return startTime == task.startTime && processor == task.processor && Objects.equals(node, task.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node.getId(), startTime, processor);
    }
}
