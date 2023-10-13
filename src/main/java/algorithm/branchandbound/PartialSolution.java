package algorithm.branchandbound;
import algorithm.astar.CalculateCostFunction;
import model.Graph;
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

    private int cost;

    private List<Task> allTasks;



    /**
     * Partial solution for the first root level node
     * @param scheduledTask the scheduled task of this partial solution
     * @param numProcessors number of processors that can perform this task
     */
    public PartialSolution(ScheduledTask scheduledTask, int numProcessors, int cost){
        this.visitedNodes = new ArrayList<>();
        this.childrenQueue = new HashMap<>();
        this.scheduledTask = scheduledTask;
        this.processorTimes = new int[numProcessors];
        this.allTasks = new ArrayList<>();
        this.cost = cost;

        // updates root node
        processorTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();
        this.visitedNodes.add(scheduledTask.getNode());
        // add all scheduled tasks to task
        this.allTasks.add(scheduledTask.toTask());
    }

//    public PartialSolution(ScheduledTask scheduledTask, int numProcessors){
//        this.visitedNodes = new ArrayList<>();
//        this.childrenQueue = new HashMap<>();
//        this.scheduledTask = scheduledTask;
//        this.processorTimes = new int[numProcessors];
//        this.allTasks = new ArrayList<>();
//
//        // updates root node
//        processorTimes[scheduledTask.getProcessorId()] = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();
//        this.visitedNodes.add(scheduledTask.getNode());
//        // add all scheduled tasks to task
//        this.allTasks.add(scheduledTask.toTask());
//    }

    /**
     * Partial solution for child tasks
     * @param parentPartialSolution the parent of this partial solution
     * @param task the task added to the queue
     */
    public PartialSolution(PartialSolution parentPartialSolution, ScheduledTask task, Graph graph) {
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

        int bottomLevelOfTask = CalculateCostFunction.getInstance(graph).bottomLevelofNode(scheduledTask.getNode());
        this.cost = parentPartialSolution.getCost() + bottomLevelOfTask;


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

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    // checks if partial solution satisfies dependencies
    public boolean isValidScheduleSatisfyDependencies(Graph graph){

        for(Task task: allTasks){

            Node node = task.getNode();
            List<Node> dependencies = graph.getDependenciesByNode(node);

            for(Node dependency: dependencies){
                // if parent on the same processor as child
                // make sure dependency finish time > task start time
                // no communication cost

                Task task1 = getTaskByNode(dependency);

                if(task1 == null){
                    return false;
                }

                if(task1.getProcessor() == task.getProcessor()){

                    // dependency not yet finished but child already started
                    if (task1.getFinishTime() > task.getStartTime()){
                        return false;
                    }

                }else{
                    // Parent, child on different processors, need to account for
                    // communication cost

                    //get the edge weight from parent to child from the graph

                    int edgeWeight = graph.getAdjacencyMatrix()[task1.getNode().getId()][task.getNode().getId()];

                    if(task.getStartTime() < task1.getFinishTime() + edgeWeight){
                        return false;
                    }
                }
            }
        }

        return true;

    }


    private Task getTaskByNode(Node node){
        for(Task task: allTasks){
            if(task.getNode().getId() == node.getId()){
                return task;
            }
        }
        return null;
    }

    private List<Task> getTaskByProcessor(int processorId){

        ArrayList<Task> processorTask = new ArrayList<>();

        for(Task task: allTasks){
            if(task.getProcessor() == processorId){
                processorTask.add(task);
            }

        }

        return processorTask;
    }


    public boolean isValidScheduleNoOverlap(){

        for( int i = 0; i < processorTimes.length; i++){
            // loop through all the processors
            // get all the tasks on that processor
            List<Task> sortedTasks = getTaskByProcessor(i);

            for(int j = 0; j < sortedTasks.size() - 1; j++){
                Task currentTask = sortedTasks.get(j);
                Task nextTask = sortedTasks.get(j + 1);

                //make sure there's no overlap
                if (!(currentTask.getStartTime() < currentTask.getFinishTime() &&
                        currentTask.getFinishTime() <= nextTask.getStartTime() &&
                        nextTask.getStartTime() < nextTask.getFinishTime())) {
                    return false;
                }
            }

        }

        return true;

    }

    public int getCost() {
        return cost;
    }

    public boolean isValid(Graph graph){
        return (isValidScheduleNoOverlap() && isValidScheduleSatisfyDependencies(graph));
    }




    // hash code if two PartialSolution instances have exactly the same tasks
    // (i.e., all the same nodes, on the same processors with the same start times) in the same order.
    @Override
    public int hashCode() {
        return allTasks.hashCode();
    }
}
