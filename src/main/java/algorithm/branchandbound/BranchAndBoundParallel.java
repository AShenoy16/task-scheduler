package algorithm.branchandbound;

import algorithm.astar.CalculateCostFunction;
import controller.VisualisationController;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The branch and bound class is needed to run the dfs branch and bound algorithm.
 */
public class BranchAndBoundParallel extends BranchAndBoundAlgorithm{
    private int numProcessors;
    private Graph graph;
    private int currentShortestPath;
    private ScheduledTask currentShortestTask;
    private CalculateCostFunction calculateCostFunction;
    private HashMap<Node, Integer> bottomLevels;
    private int[] parallelThreadTimes;
    private static ForkJoinPool pool;
    private VisualisationController controller;
    private ScheduledTask currentDFSTask;
    private boolean isFinished = false;

    /**
     * This run method will initialise the necessary variables for the dfs branch and bound recursive method. It will
     * be called first before the dfs method
     * @param graph - input graph by the user
     * @param numProcesses - number of processors specified by the user
     * @return an optimal schedule found by the branch and bound algorithm
     */
    public Schedule run(Graph graph, int numProcesses, int numCores){
        this.graph = graph;
        this.numProcessors = numProcesses;
        this.currentShortestPath = Integer.MAX_VALUE;
        this.currentShortestTask = null;
        this.parallelThreadTimes = new int[graph.getStartNodes().size()];
        pool = new ForkJoinPool(numCores);

        calculateCostFunction = new CalculateCostFunction(graph);

        for(Node entryNode: graph.getStartNodes()){
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);
        }

        bottomLevels = calculateCostFunction.getBottomLevelMap();

        AtomicInteger i = new AtomicInteger();
        graph.getStartNodes().forEach(startNode -> {
            Map<Node, List<ScheduledTask>> childrenQueue = graph.getStartNodes().stream()
                    .filter(node -> !node.equals(startNode))
                    .collect(Collectors.toMap(node -> node, node -> new ArrayList<>()));

            ScheduledTask task = new ScheduledTask(0, 0, startNode, null, 1);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses, bottomLevels.get(startNode), calculateCostFunction);
            partialSolution.getChildrenQueue().putAll(childrenQueue);

            dfs dfs = new dfs(partialSolution, i);
            pool.invoke(dfs);
            i.getAndIncrement();
        });

        List<ScheduledTask> scheduledTasksList = new ArrayList<>();
        ScheduledTask shortestPathTask = currentShortestTask;

        while (shortestPathTask != null) {
            scheduledTasksList.add(shortestPathTask);
            shortestPathTask = shortestPathTask.getParent();
        }

        // finalise visualiser with best shortest task
        currentDFSTask = currentShortestTask;
        isFinished = true;

        Schedule schedule = new Schedule(numProcesses, scheduledTasksList);
        schedule.setShortestPath(currentShortestPath);

        return schedule;
    }

    /**
     * Represents a unit of work to be processed in parallel. Take partial solution as workload to execute.
     */
    private class dfs extends RecursiveAction {

        private AtomicInteger id;
        public PartialSolution partialSolution;

        dfs(PartialSolution partialSolution, AtomicInteger id) {
            this.partialSolution = partialSolution;
            this.id = id;
        }

        /**
         * Method invoked on fork and invokeAll() calls. Processes partial solution and children node recursion in parallel.
         */
        @Override
        protected void compute() {
            ScheduledTask currentTask = partialSolution.getScheduledTask();
            currentDFSTask = currentTask;
            int pathTime = getCurrentLatestTaskTime(currentTask);

            // bound the search of this node
            if (pathTime >= currentShortestPath) {
                return;
            }

            // add children of current node to queue of partial solution
            int[] outgoingEdgeWeights = graph.getAdjacencyMatrix()[currentTask.getNode().getId()];

            for (int i = 0; i < outgoingEdgeWeights.length; i++) {
                // 'i' represents the outgoing edge node
                if (outgoingEdgeWeights[i] != 0 && !partialSolution.getVisitedNodes().contains(graph.getNodes()[i])) {
                    // adds current task as dependency of dest node
                    partialSolution.getChildrenQueue().computeIfAbsent(graph.getNodes()[i], k -> new ArrayList<>()).add(currentTask);
                }
            }

            // update current shortest path and task if queue is empty and is shorter
            if (partialSolution.getChildrenQueue().size() == 0 && pathTime < currentShortestPath) {
                currentShortestPath = pathTime;
                currentShortestTask = currentTask;

                // print path on console
                printCurrentPath(currentTask);

                // update thread times
                parallelThreadTimes[this.id.get()] = currentShortestPath;
            }

            // branch and bound algorithm for queued children
            partialSolution.getChildrenQueue().forEach((destNode, dependencyList) -> {
                List<dfs> taskList = new ArrayList<>();
                for (int i = 0; i < numProcessors; i++) {// 'i' is processors to consider for each queued childNode
                    int earliestStartTime = 0;

                    // ensure all previous tasks of destNode is visited
                    if (!isFullyVisited(partialSolution, destNode)) {
                        continue;
                    }

                    // iterates over each dependency of destNode, and gets the time it can start at earliest
                    for (ScheduledTask dependency : dependencyList) {
                        int finishTime = dependency.getStartTime() + dependency.getNode().getVal();

                        // if dependency is not on the same processor, account for communication delay
                        if (dependency.getProcessorId() != i) {
                            int communicationDelay = graph.getAdjacencyMatrix()[dependency.getNode().getId()][destNode.getId()];
                            finishTime += communicationDelay;
                        }

                        // return the earliest start time for this task
                        earliestStartTime = Math.max(finishTime, earliestStartTime);
                    }
                    // check if processor is free after earliestStartTime
                    int possibleStartTime = Math.max(earliestStartTime, partialSolution.getProcessorTimes()[i]);

                    if(earliestStartTime + bottomLevels.get(destNode) >= currentShortestPath){
                        continue;
                    }
                    // create new partial solution with new task for this child and add it to dfs branch and bound recursion
                    ScheduledTask newTask = new ScheduledTask(possibleStartTime, i, destNode, partialSolution.getScheduledTask(), partialSolution.getScheduledTask().getTaskLength()+1);
                    PartialSolution newPartialSolution = new PartialSolution(partialSolution, newTask, calculateCostFunction);
                    newPartialSolution.getProcessorTimes()[i] = possibleStartTime + graph.getNodes()[destNode.getId()].getVal();

                    calculateCostFunction.setPartialSolutionCost(newPartialSolution);

                    if(newPartialSolution.getCost() >= currentShortestPath){
                        continue;
                    }

                    // check if newPartial Solution is valid
                    if(!newPartialSolution.isValid(graph)){
                        continue;
                    }

                    taskList.add(new dfs(newPartialSolution, this.id));
                }

                // Fork subtasks (execute in parallel). InvokeAll will return will all tasks are completed.
                invokeAll(taskList);
            });
        }
    }

    /**
     * This helper function will return the latest task time from the partial solution of this current task, as current
     * task may not truly represent the latest task time.
     * @param currentTask - the current task of the dfs recursion
     * @return - the total path length of the partial solution of the current task
     */
    private int getCurrentLatestTaskTime(ScheduledTask currentTask) {
        int pathTime = 0;
        int finishTime;
        while (currentTask != null) {
            finishTime = currentTask.getStartTime() + currentTask.getNode().getVal();
            if (finishTime > pathTime) {
                pathTime = finishTime;
            }
            currentTask = currentTask.getParent();
        }
        return pathTime;
    }

    /**
     * Prints out the schedule whenever a new shorter complete schedule is found.
     * It displays start time, end time, node ID, and processor ID of each task in the schedule
     * @param task - the task of the new schedule
     */
    private void printCurrentPath(ScheduledTask task) {
        int pathLength = 0;
        System.out.println("New Shortest Task: " + task.getNode().getId());

        System.out.println("Start | Finish | Node ID | Processor ID");
        while (task != null) {
            System.out.println(task.getStartTime() + " | " + (task.getStartTime()+task.getNode().getVal()) + " | " + task.getNode().getId() + " | " + task.getProcessorId());
            pathLength = Math.max(pathLength, task.getStartTime() + task.getNode().getVal());
            task = task.getParent();
        }
        System.out.println("New Shortest Path: " + pathLength + "\n");
    }

    /**
     * This helper function checks whether the destination node has all dependencies visited already before it can be
     * visited
     * @param partialSolution - the current partial solution in the dfs recursion
     * @param destNode - the destination node to check if it can be visited
     * @return true if dependencies of destNode is full visited, false if not
     */
    private boolean isFullyVisited(PartialSolution partialSolution, Node destNode) {
        for (int row = 0; row < graph.getN(); row++) {
            // 'row' being source node, 'destNode' being dest, check if source node is fully visited
            if (graph.getAdjacencyMatrix()[row][destNode.getId()] != 0 && !partialSolution.getVisitedNodes().contains(graph.getNodes()[row])) {
                return false;
            }
        }
        return true;
    }

    public void setController(VisualisationController controller) {
        this.controller = controller;
    }

    public ScheduledTask getCurrentDFSTask() {
        return currentDFSTask;
    }

    @Override
    public Schedule run(Graph graph, int numProcessors) {
        return null;
    }

    @Override
    public int getShortestPathText() {
        return currentShortestPath;
    }

    @Override
    public boolean getIsFinished() {
        return isFinished;
    }

    public int[] getParallelThreadTimes() {
        return parallelThreadTimes;
    }

}
