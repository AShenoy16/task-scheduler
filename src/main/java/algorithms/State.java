package algorithms;

import model.Graph;

public class State {
    private int numProcessors;
    private Graph graph;
    private int currrentShortestPath;
    private ScheduledTask currentShortestTask;
    public State(int numProcessors, Graph graph){
        this.numProcessors = numProcessors;
        this.graph = graph;
        this.currrentShortestPath = Integer.MAX_VALUE;
    }
}
