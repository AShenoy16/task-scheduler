package algorithms;

import model.Graph;
import model.Node;

public class ScheduledTask {
    private int startTime;
    private int processorId;
    private Integer node;
    private ScheduledTask parent;

    private Graph graph;
    public ScheduledTask(int startTime, int processorId, Integer node, ScheduledTask parent, Graph graph){
        this.startTime = startTime;
        this.processorId = processorId;
        this.node = node;
        this.parent = parent;
        this.graph = graph;
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
        return graph.getNodeWeightings().get(node);
    }
}
