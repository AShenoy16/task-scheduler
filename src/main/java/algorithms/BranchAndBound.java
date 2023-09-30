package algorithms;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchAndBound {
    private int shortestPath;

    public Schedule run(Graph graph, int numProcesses){
        // initialise new state to keep track of current shortest path
        State state = new State(numProcesses, graph);
        Map<Node, List<ScheduledTask>> rootQueue = new HashMap<>();

        for(Node n : graph.getStartNodes()){
            rootQueue.put(n, new ArrayList<>());
            ScheduledTask task = new ScheduledTask(0,0, n,null);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses);
            partialSolution.getRootQueue().putAll(rootQueue);
            dfs(state, partialSolution);
        }
        List<ScheduledTask> scheduledTasksList = new ArrayList<>();
        ScheduledTask shortestPathTask = state.getCurrentShortestTask();

        while (shortestPathTask != null) {
            scheduledTasksList.add(shortestPathTask);
            shortestPathTask = shortestPathTask.getParent();
        }
        Schedule schedule = new Schedule(numProcesses, scheduledTasksList);
        schedule.setShortestPath(shortestPath);
        return schedule;
    }


    private void dfs(State state, PartialSolution partialSolution) {
        ScheduledTask currentTask = partialSolution.getScheduledTask();
        int pathTime = currentTask.getStartTime() + currentTask.getNode().getVal();

        if (pathTime >= state.getCurrentShortestPath()) {
            return;
        }

        int[] outgoingEdgeWeights = state.getGraph().getAdjacencyMatrix()[currentTask.getNode().getId()];
        for (int i = 0; i < outgoingEdgeWeights.length; i++) {// 'i' represents the outgoing edge node
            if (outgoingEdgeWeights[i] != 0) {
                if (partialSolution.getVisitedNodes().contains(state.getGraph().getNodes().get(i))) {
                    continue;
                } else if (partialSolution.getRootQueue().containsKey(state.getGraph().getNodes().get(i))) {
                    partialSolution.getRootQueue().get(state.getGraph().getNodes().get(i)).add(currentTask);
                } else {
                    ArrayList<ScheduledTask> children = new ArrayList<>();
                    children.add(currentTask);
                    partialSolution.getRootQueue().put(state.getGraph().getNodes().get(i), children);
                }
            }
        }

        if (partialSolution.getRootQueue().size() == 0) {
            if (pathTime < state.getCurrentShortestPath()) {
                state.setCurrentShortestPath(pathTime);
                state.setCurrentShortestTask(currentTask);
                
                printCurrentPath(currentTask);
            }
        }

        for (Map.Entry<Node, List<ScheduledTask>> childNode : partialSolution.getRootQueue().entrySet()) {
            // 'i' is processors
            for (int i = 0; i < state.getNumProcessors(); i++) {
                int earliestStartTime = 0;

                Node destNode = childNode.getKey();
                List<ScheduledTask> scheduledTasks = childNode.getValue();

                if (!isFullyVisited(state, partialSolution, destNode)) {
                    continue;
                }

                for (ScheduledTask scheduledTask : scheduledTasks) {
                    int finishTime = scheduledTask.getStartTime() + scheduledTask.getNode().getVal();

                    if (scheduledTask.getProcessorId() != i) {
                        finishTime += state.getGraph().getAdjacencyMatrix()[scheduledTask.getNode().getId()][destNode.getId()];
                    }

                    // return the earliest start time for this task
                    earliestStartTime = Math.max(finishTime, earliestStartTime);
                }
                int possibleStartTime = Math.max(earliestStartTime, partialSolution.getProcessorTimes()[i]);
                ScheduledTask newTask = new ScheduledTask(possibleStartTime, i, destNode, partialSolution.getScheduledTask());
                PartialSolution newPartialSolution = new PartialSolution(partialSolution, newTask);
                newPartialSolution.getProcessorTimes()[i] = possibleStartTime + state.getGraph().getNodes().get(destNode.getId()).getVal();
                dfs(state, newPartialSolution);
            }
        }

    }

    private void printCurrentPath(ScheduledTask task) {
        int pathLength = 0;

        System.out.println("Start | Finish | Node ID | Processor ID");
        while (task.getParent() != null) {
            System.out.println(task.getStartTime() + " | " + (task.getStartTime()+task.getNode().getVal()) + " | " + task.getNode().getId() + " | " + task.getProcessorId());
            pathLength = Math.max(pathLength, task.getStartTime() + task.getNode().getVal());
            task = task.getParent();
        }
        shortestPath = pathLength;
        System.out.println("New Shortest Path: " + pathLength);
    }

    private boolean isFullyVisited(State state, PartialSolution partialSolution, Node destNode) {
        for (int row = 0; row < state.getGraph().getN(); row++) {
            // 'row' being source node, 'node' being dest, check if source node is fully visited
            if (state.getGraph().getAdjacencyMatrix()[row][destNode.getId()] != 0 && !partialSolution.getVisitedNodes().contains(state.getGraph().getNodes().get(row))) {
                return false;
            }
        }
        return true;
    }

}
