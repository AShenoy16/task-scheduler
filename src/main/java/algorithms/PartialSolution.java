package algorithms;
import java.util.*;

public class PartialSolution {
    private List<Integer> visitedNodes;
    private Map<Integer, List<ScheduledTask>> rootQueue;

    private int[] processorTimes;

    private ScheduledTask scheduledTask;
    public PartialSolution(ScheduledTask scheduledTask, int numProcessors){
        this.visitedNodes = new ArrayList<>();
        this.rootQueue = new HashMap<>();
        this.scheduledTask = scheduledTask;
        this.processorTimes = new int[numProcessors];

        processorTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + scheduledTask.getTaskTime();

        this.visitedNodes.add(scheduledTask.getNode());
    }
//    private PartialSolution(PartialSolution partialSolution, ScheduledTask task) {
//        this.visitedNodes = new ArrayList<>();
//        this.visitedNodes.addAll(partialSolution.visitedNodes);
//
//        this.rootQueue = new HashMap<>();
//        for (var nodeDependencyPair : partialSolution.rootQueue.entrySet()) {
//            var node = nodeDependencyPair.getKey();
//            var dependencyList = nodeDependencyPair.getValue();
//
//            var clonedList = new ArrayList<>(dependencyList);
//            rootQueue.put(node, clonedList);
//        }
//
//        this.processorTimes = Arrays.copyOf(partialSolution.processorTimes, partialSolution.processorTimes.length);
//        this.scheduledTask = task;
//
//        // Add the current node to visited as an optimisation
//        this.visitedChildren.add(newTask.getNode());
//
//        // Remove the current node from queued children to avoid infinite recursion
//        this.queuedChildren.remove(newTask.getNode());
//    }
    public Map<Integer, List<ScheduledTask>> getRootQueue(){
        return rootQueue;
    }
}
