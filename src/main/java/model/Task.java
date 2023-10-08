package model;

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
}
