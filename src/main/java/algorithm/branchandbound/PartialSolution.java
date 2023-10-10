package algorithm.branchandbound;
import model.Node;
import model.Task;

import java.util.*;

/**
 * The PartialSolution class is created everytime for every new schedule.
 */
public class PartialSolution {
    private List<Node> visitedNodes;
    private Map<Node, List<ScheduledTask>> childrenQueue;
    private int[] processorTimes;
    private ScheduledTask scheduledTask;

    private List<Task> allTasks;



    /**
     * Partial solution for the first root level node
     * @param scheduledTask the scheduled task of this partial solution
     * @param numProcessors number of processors that can perform this task
     */
    public PartialSolution(ScheduledTask scheduledTask, int numProcessors){
        this.visitedNodes = new ArrayList<>();
        this.childrenQueue = new HashMap<>();
        this.scheduledTask = scheduledTask;
        this.processorTimes = new int[numProcessors];
        this.allTasks = new ArrayList<>();

        // updates root node
        processorTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();
        this.visitedNodes.add(scheduledTask.getNode());
        // add all scheduled tasks to task
        this.allTasks.add(scheduledTask.toTask());
    }

    /**
     * Partial solution for child tasks
     * @param parentPartialSolution the parent of this partial solution
     * @param task the task added to the queue
     */
    public PartialSolution(PartialSolution parentPartialSolution, ScheduledTask task) {
        this.childrenQueue = new HashMap<>();
        this.visitedNodes = new ArrayList<>();
        this.visitedNodes.addAll(parentPartialSolution.visitedNodes);

        parentPartialSolution.childrenQueue.forEach((node, dependencyList) -> {
            childrenQueue.put(node, new ArrayList<>(dependencyList));
        });

        // assign a copy of processor times for this partial solution
        this.processorTimes = Arrays.copyOf(parentPartialSolution.processorTimes, parentPartialSolution.processorTimes.length);

        this.scheduledTask = task;
        this.visitedNodes.add(task.getNode());//add task node as visited in visited array
        this.childrenQueue.remove(task.getNode());//remove node from queue

        // Add the current task to the list of all tasks
        this.allTasks = new ArrayList<>(parentPartialSolution.allTasks);
        this.allTasks.add(task.toTask());

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


    // hash code if two PartialSolution instances have exactly the same tasks
    // (i.e., all the same nodes, on the same processors with the same start times) in the same order.
    @Override
    public int hashCode() {
        return allTasks.hashCode();
    }
}
