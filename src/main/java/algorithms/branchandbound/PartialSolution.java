package algorithms.branchandbound;
import model.Node;

import java.util.*;

/**
 * The PartialSolution class is created everytime for every new schedule.
 */
public class PartialSolution {
    private List<Node> visitedNodes;
    private Map<Node, List<ScheduledTask>> rootQueue;
    private int[] processorTimes;

    private ScheduledTask scheduledTask;

    public PartialSolution(ScheduledTask scheduledTask, int numProcessors){
        this.visitedNodes = new ArrayList<>();
        this.rootQueue = new HashMap<>();
        this.scheduledTask = scheduledTask;
        this.processorTimes = new int[numProcessors];

//        processorTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();

        this.visitedNodes.add(scheduledTask.getNode());
    }

    public PartialSolution(PartialSolution ParentPartialSolution, ScheduledTask task) {
        this.visitedNodes = new ArrayList<>();
        this.visitedNodes.addAll(ParentPartialSolution.visitedNodes);

        this.rootQueue = new HashMap<>();
        for (Map.Entry<Node, List<ScheduledTask>> nodeDependencyPair : ParentPartialSolution.rootQueue.entrySet()) {
            Node node = nodeDependencyPair.getKey();
            List<ScheduledTask> dependencyList = nodeDependencyPair.getValue();

            List<ScheduledTask> clonedList = new ArrayList<>(dependencyList);
            rootQueue.put(node, clonedList);
        }

        this.processorTimes = Arrays.copyOf(ParentPartialSolution.processorTimes, ParentPartialSolution.processorTimes.length);
        this.scheduledTask = task;

        this.visitedNodes.add(task.getNode());

        this.rootQueue.remove(task.getNode());
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

    public Map<Node, List<ScheduledTask>> getRootQueue(){
        return rootQueue;
    }
}
