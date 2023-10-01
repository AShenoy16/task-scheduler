package algorithm.branchandbound;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The branch and bound class is needed to run the dfs branch and bound algorithm.
 */
public class BranchAndBound {
    private int numProcessors;
    private Graph graph;
    private int currentShortestPath;
    private ScheduledTask currentShortestTask;

    /**
     * This run method will initialise the necessary variables for the dfs branch and bound recursive method. It will
     * be called first before the dfs method
     * @param graph - input graph by the user
     * @param numProcesses - number of processors specified by the user
     * @return an optimal schedule found by the branch and bound algorithm
     */
    public Schedule run(Graph graph, int numProcesses){
        this.graph = graph;
        this.numProcessors = numProcesses;
        this.currentShortestPath = Integer.MAX_VALUE;
        this.currentShortestTask = null;

        // iterate over each entry nodes
        for(Node n : graph.getStartNodes()){
            Map<Node, List<ScheduledTask>> childrenQueue = new HashMap<>();
            // add multiple entry nodes to queue or algorithm will not be able to visit children
            for (Node m : graph.getStartNodes()) {
                if (n != m) {
                    childrenQueue.put(m, new ArrayList<>());
                }
            }
            ScheduledTask task = new ScheduledTask(0,0, n,null);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses);
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
        Schedule schedule = new Schedule(numProcesses, scheduledTasksList);
        schedule.setShortestPath(currentShortestPath);
        return schedule;
    }


    /**
     * recursive dfs branch and bound algorithm, that will recursively iterate for each new partial solution
     * @param partialSolution the partial solution of this dfs iteration
     */
    private void dfs(PartialSolution partialSolution) {
        ScheduledTask currentTask = partialSolution.getScheduledTask();
        int pathTime = getCurrentLatestTaskTime(currentTask);

        // bound the search of this node
        if (pathTime >= currentShortestPath) {
            return;
        }

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

            // print path on console
            printCurrentPath(currentTask);

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

                // create new partial solution with new task for this child and add it to dfs branch and bound recursion
                ScheduledTask newTask = new ScheduledTask(possibleStartTime, i, destNode, partialSolution.getScheduledTask());
                PartialSolution newPartialSolution = new PartialSolution(partialSolution, newTask);
                newPartialSolution.getProcessorTimes()[i] = possibleStartTime + graph.getNodes()[destNode.getId()].getVal();
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

}
