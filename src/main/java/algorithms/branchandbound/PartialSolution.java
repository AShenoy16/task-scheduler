package algorithms.branchandbound;
import model.Node;

import java.util.*;

/**
 * The PartialSolution class is created everytime for every new schedule.
 */
public class PartialSolution {
    private List<Node> visitedNodes;
    private Map<Node, List<ScheduledTask>> childrenQueue;
    private int[] processorTimes;
    private ScheduledTask scheduledTask;

    /**
     * Partial solution for root level tasks
     * @param scheduledTask the scheduled task of this partial solution
     * @param numProcessors number of processors that can perform this task
     */
    public PartialSolution(ScheduledTask scheduledTask, int numProcessors){
        this.visitedNodes = new ArrayList<>();
        this.childrenQueue = new HashMap<>();
        this.scheduledTask = scheduledTask;
        this.processorTimes = new int[numProcessors];
    }

    /**
     * Partial solution for child tasks
     * @param ParentPartialSolution the parent of this partial solution
     * @param task the task added to the queue
     */
    public PartialSolution(PartialSolution ParentPartialSolution, ScheduledTask task) {
        this.childrenQueue = new HashMap<>();
        this.visitedNodes = new ArrayList<>();
        this.visitedNodes.addAll(ParentPartialSolution.visitedNodes);

        for (Map.Entry<Node, List<ScheduledTask>> dependencyTuple : ParentPartialSolution.childrenQueue.entrySet()) {
            Node node = dependencyTuple.getKey();

            // clone new separate dependency list for this partial solution
            List<ScheduledTask> dependencyList = dependencyTuple.getValue();
            List<ScheduledTask> cloneDependencyList = new ArrayList<>(dependencyList);
            childrenQueue.put(node, cloneDependencyList);
        }

        // assign a copy of processor times for this partial solution
        this.processorTimes = Arrays.copyOf(ParentPartialSolution.processorTimes, ParentPartialSolution.processorTimes.length);

        this.scheduledTask = task;
        this.visitedNodes.add(task.getNode());//add task node as visited in visited array
        this.childrenQueue.remove(task.getNode());//remove node from queue
    }

    public List<Node> getVisitedNodes() {
        return visitedNodes;
    }

    public int[] getProcessorTimes() {
        return processorTimes;
    }

    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }

    public Map<Node, List<ScheduledTask>> getChildrenQueue(){
        return childrenQueue;
    }
}
