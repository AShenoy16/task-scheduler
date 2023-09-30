package algorithms.branchandbound;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchAndBound {
    private int numProcessors;
    private Graph graph;
    private int currentShortestPath;
    private ScheduledTask currentShortestTask;

    public Schedule run(Graph graph, int numProcesses){
        this.graph = graph;
        this.numProcessors = numProcesses;
        this.currentShortestPath = Integer.MAX_VALUE;
        this.currentShortestTask = null;

        // iterate over each entry nodes
        for(Node n : graph.getStartNodes()){
            ScheduledTask task = new ScheduledTask(0,0, n,null);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses);
            partialSolution.getRootQueue().put(n, new ArrayList<>());
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


    private void dfs(PartialSolution partialSolution) {
        ScheduledTask currentTask = partialSolution.getScheduledTask();
        int pathTime = getCurrentLatestTaskTime(currentTask);

        // bound the search of this node
        if (pathTime >= currentShortestPath) {
            return;
        }

        int[] outgoingEdgeWeights = graph.getAdjacencyMatrix()[currentTask.getNode().getId()];
        for (int i = 0; i < outgoingEdgeWeights.length; i++) {// 'i' represents the outgoing edge node
            if (outgoingEdgeWeights[i] != 0) {
                if (partialSolution.getVisitedNodes().contains(graph.getNodes().get(i))) {
                    continue;
                }
                if (partialSolution.getRootQueue().containsKey(graph.getNodes().get(i))) {
                    partialSolution.getRootQueue().get(graph.getNodes().get(i)).add(currentTask);
                } else {
                    ArrayList<ScheduledTask> children = new ArrayList<>();
                    children.add(currentTask);
                    partialSolution.getRootQueue().put(graph.getNodes().get(i), children);
                }
            }
        }

        if (partialSolution.getRootQueue().size() == 0) {
            if (pathTime < currentShortestPath) {
                currentShortestPath = pathTime;
                currentShortestTask = currentTask;
                
                printCurrentPath(currentTask);
            }
        }

        for (Map.Entry<Node, List<ScheduledTask>> childNode : partialSolution.getRootQueue().entrySet()) {
            // 'i' is processors
            for (int i = 0; i < numProcessors; i++) {
                int earliestStartTime = 0;

                Node destNode = childNode.getKey();
                List<ScheduledTask> scheduledTasks = childNode.getValue();

                if (!isFullyVisited(partialSolution, destNode)) {
                    continue;
                }

                for (ScheduledTask scheduledTask : scheduledTasks) {
                    int finishTime = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();

                    if (scheduledTask.getProcessorId() != i) {
                        finishTime += graph.getAdjacencyMatrix()[scheduledTask.getNode().getId()][destNode.getId()];
                    }

                    // return the earliest start time for this task
                    earliestStartTime = Math.max(finishTime, earliestStartTime);
                }
                int possibleStartTime = Math.max(earliestStartTime, partialSolution.getProcessorTimes()[i]);
                ScheduledTask newTask = new ScheduledTask(possibleStartTime, i, destNode, partialSolution.getScheduledTask());
                PartialSolution newPartialSolution = new PartialSolution(partialSolution, newTask);
                newPartialSolution.getProcessorTimes()[i] = possibleStartTime + graph.getNodes().get(destNode.getId()).getVal();
                dfs(newPartialSolution);
            }
        }

    }

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

    private boolean isFullyVisited(PartialSolution partialSolution, Node destNode) {
        for (int row = 0; row < graph.getN(); row++) {
            // 'row' being source node, 'node' being dest, check if source node is fully visited
            if (graph.getAdjacencyMatrix()[row][destNode.getId()] != 0 && !partialSolution.getVisitedNodes().contains(graph.getNodes().get(row))) {
                return false;
            }
        }
        return true;
    }

}
