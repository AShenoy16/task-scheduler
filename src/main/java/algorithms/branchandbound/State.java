package algorithms.branchandbound;

import model.Graph;

/**
 * The state class keeps track of current shortest path
 */
public class State {
    private int numProcessors;
    private Graph graph;
    private int currentShortestPath;
    private ScheduledTask currentShortestTask;

    public State(int numProcessors, Graph graph){
        this.numProcessors = numProcessors;
        this.graph = graph;
        this.currentShortestPath = Integer.MAX_VALUE;
    }

    public int getNumProcessors() {
        return numProcessors;
    }

    public Graph getGraph() {
        return graph;
    }

    public ScheduledTask getCurrentShortestTask() {
        return currentShortestTask;
    }

    public int getCurrentShortestPath() {
        return currentShortestPath;
    }

    public void setCurrentShortestPath(int currrentShortestPath) {
        this.currentShortestPath = currrentShortestPath;
    }

    public void setCurrentShortestTask(ScheduledTask currentShortestTask) {
        this.currentShortestTask = currentShortestTask;
    }

}
