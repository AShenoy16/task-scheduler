package algorithm.branchandbound;

import controller.VisualisationController;
import algorithm.astar.CalculateCostFunction;
import model.Graph;
import model.Node;

import java.util.*;

/**
 * The branch and bound class is needed to run the dfs branch and bound algorithm.
 */
public class BranchAndBound extends BranchAndBoundAlgorithm{
    private int numProcessors;
    private Graph graph;
    private int currentShortestPath;
    private ScheduledTask currentShortestTask;
    private VisualisationController controller;
    private ScheduledTask currentDFSTask;
    private boolean isFinished = false;
    private CalculateCostFunction calculateCostFunction;
    private HashMap<Node, Integer> bottomLevels;
    private final HashSet<Integer> visitedSchedules = new HashSet<>();

    /**
     * This run method will initialise the necessary variables for the dfs branch and bound recursive method. It will
     * be called first before the dfs method
     * @param graph - input graph by the user
     * @param numProcesses - number of processors specified by the user
     * @return an optimal schedule found by the branch and bound algorithm
     */
    @Override
    public Schedule run(Graph graph, int numProcesses){
        this.graph = graph;
        this.numProcessors = numProcesses;
        this.currentShortestPath = Integer.MAX_VALUE;
        this.currentShortestTask = null;

        calculateCostFunction = new CalculateCostFunction(graph);

        for(Node entryNode: graph.getStartNodes()){
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);
        }
        bottomLevels = calculateCostFunction.getBottomLevelMap();

        // iterate over each entry nodes
        for(Node n : graph.getStartNodes()){
            Map<Node, List<ScheduledTask>> childrenQueue = new HashMap<>();

            // add multiple entry nodes to queue or algorithm will not be able to visit children
            for (Node m : graph.getStartNodes()) {
                if (n != m) {
                    childrenQueue.put(m, new ArrayList<>());
                }
            }
            // cost will be bottom level of the entry node
            ScheduledTask task = new ScheduledTask(0,0, n,null, 1);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses, bottomLevels.get(n));
            partialSolution.getChildrenQueue().putAll(childrenQueue);

            dfs(partialSolution); // start recursive dfs branch and bound
        }

        // returns a schedule of the shortest path found
        List<ScheduledTask> scheduledTasksList = new ArrayList<>();
        ScheduledTask shortestPathTask = currentShortestTask;

        while (shortestPathTask != null) {
            scheduledTasksList.add(shortestPathTask);
            shortestPathTask = shortestPathTask.getParent();
        }

        isFinished = true;
        currentDFSTask = currentShortestTask;

        Schedule schedule = new Schedule(numProcesses, scheduledTasksList);
        schedule.setShortestPath(currentShortestPath);
        return schedule;
    }

    @Override
    public Schedule run(Graph graph, int numProcessors, int i) {
        throw new RuntimeException("Incorrect run method for this bnb class");
    }

    @Override
    public void setController(VisualisationController controller){
        this.controller = controller;
    }

    @Override
    public ScheduledTask getCurrentDFSTask() {
        return currentDFSTask;
    }

    @Override
    public boolean getIsFinished() {
        return isFinished;
    }

    @Override
    public int getShortestPathText(){
        return currentShortestPath;
    }

    /**
     * recursive dfs branch and bound algorithm, that will recursively iterate for each new partial solution
     * @param partialSolution the partial solution of this dfs iteration
     */
    private void dfs(PartialSolution partialSolution) {
        ScheduledTask currentTask = partialSolution.getScheduledTask();
        currentDFSTask = currentTask;
        controller.setStarted();
        int pathTime = getCurrentLatestTaskTime(currentTask);

        // bound the search of this node
        if (pathTime >= currentShortestPath) {
            return;
        }

        //make sure tasks are scheduled by bottom level
        // remove any schedules we have already visited
        if(visitedSchedules.contains(partialSolution.hashCode())){
            return;
        }
        visitedSchedules.add(partialSolution.hashCode());

        // add children of current node to queue of partial solution
        int[] outgoingEdgeWeights = graph.getAdjacencyMatrix()[currentTask.getNode().getId()];
        for (int i = 0; i < outgoingEdgeWeights.length; i++) {// 'i' represents the outgoing edge node
            if (outgoingEdgeWeights[i] != 0) {
                // if node already visited then continue with for loop
                if (partialSolution.getVisitedNodes().contains(graph.getNodes()[i])) {
                    continue;
                }
                // adds current task as dependency of dest node
                if (partialSolution.getChildrenQueue().containsKey(graph.getNodes()[i])) {
                    partialSolution.getChildrenQueue().get(graph.getNodes()[i]).add(currentTask);
                } else {
                    List<ScheduledTask> children = new ArrayList<>();
                    children.add(currentTask);
                    partialSolution.getChildrenQueue().put(graph.getNodes()[i], children);
                }
            }
        }

        // update current shortest path and task if queue is empty and is shorter
        if (partialSolution.getChildrenQueue().size() == 0 && pathTime < currentShortestPath) {
            currentShortestPath = pathTime;
            currentShortestTask = currentTask;

            // update visualisation
            controller.queuePartialSolution(partialSolution);
        }

        // branch and bound algorithm for queued children
        partialSolution.getChildrenQueue().forEach((destNode, dependencyList) -> {
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

                // if tasks bottom level + the earliest start time can't beat the fastest time
                // just skip
                if(earliestStartTime + bottomLevels.get(destNode) >= currentShortestPath){
                    continue;
                }

                // create new partial solution with new task for this child and add it to dfs branch and bound recursion
                ScheduledTask newTask = new ScheduledTask(possibleStartTime, i, destNode, partialSolution.getScheduledTask(), partialSolution.getScheduledTask().getTaskLength() + 1);
                PartialSolution newPartialSolution = new PartialSolution(partialSolution, newTask, calculateCostFunction);
                newPartialSolution.getProcessorTimes()[i] = possibleStartTime + graph.getNodes()[destNode.getId()].getVal();

                // Set cost
                calculateCostFunction.setPartialSolutionCost(newPartialSolution);

                // skip if new cost can't beat the fastest time
                if(newPartialSolution.getCost() >= currentShortestPath){
                    continue;
                }

                // check if newPartial Solution is valid
                if(!newPartialSolution.isValid(graph)){
                    continue;
                }

                dfs(newPartialSolution);
            }
        });
    }


    /**
     * This helper function will return the latest task time from the partial solution of this current task, as current
     * task may not truly represent the latest task time.
     * @param currentTask - the current task of the dfs recursion
     * @return - the total path length of the partial solution of the current task
     */
    private int getCurrentLatestTaskTime(ScheduledTask currentTask) {
        int pathTime = 0;
        while (currentTask != null) {
            if (currentTask.getStartTime() + currentTask.getNode().getVal() > pathTime) {
                pathTime = currentTask.getStartTime() + currentTask.getNode().getVal();
            }
            currentTask = currentTask.getParent();
        }
        return pathTime;
    }

    /**
     * This helper function checks whether the destination node has all dependencies visited already before it can be
     * visited
     * @param partialSolution - the current partial solution in the dfs recursion
     * @param destNode - the destination node to check if it can be visited
     * @return true if dependencies of destNode is full visited, false if not
     */
    private boolean isFullyVisited(PartialSolution partialSolution, Node destNode) {
        for (int row = 0; row < graph.getNumberOfNodes(); row++) {
            // 'row' being source node, 'destNode' being dest, check if source node is fully visited
            if (graph.getAdjacencyMatrix()[row][destNode.getId()] != 0 && !partialSolution.getVisitedNodes().contains(graph.getNodes()[row])) {
                return false;
            }
        }
        return true;
    }
}
