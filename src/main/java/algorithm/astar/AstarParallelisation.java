package algorithm.astar;

import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AstarParallelisation {

    private PriorityQueue<Schedule> open = new PriorityQueue<>(new CostFunctionComparator());
    private CalculateCostFunction calculateCostFunction;

    public Schedule run(Graph graph, int numProcessors) {
        long startTimeNano = System.nanoTime();
        long finishTimeNano;

        calculateCostFunction = new CalculateCostFunction(graph);
        for (Node entryNode : graph.getStartNodes()) {
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);
        }

        List<Node> validEntryNodes = calculateCostFunction.getHighestBottomLevelNodes();
        if (validEntryNodes.isEmpty()) {
            System.out.println("Why are entry nodes empty????");
            return null;
        }

        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes, numProcessors);
        open.addAll(initialSchedules);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        while (open.size() != 0) {
            Schedule partialSchedule = open.poll();

            if (partialSchedule.isCompleteSchedule(graph)) {
                if (partialSchedule.isValidScheduleNoOverlap() && partialSchedule.isValidScheduleSatisfyDependencies(graph)) {
                    finishTimeNano = System.nanoTime();
                    System.out.println("Elapsed Time (Nanoseconds): " + (finishTimeNano - startTimeNano) + " ns");
                    open.clear();
                    return partialSchedule;
                }
            }

            List<Schedule> newSchedules = createPartialSchedulesParallel(
                    partialSchedule.getFreeNodes(graph), numProcessors, partialSchedule, graph);

            open.addAll(newSchedules);
        }

        executorService.shutdown(); // Shut down the thread pool
        return null;
    }

    public List<Schedule> createInitialSchedules(List<Node> entryNodes, int numProcessors) {
        List<Schedule> newSchedules = new ArrayList<>();
        for (Node entryNode : entryNodes) {
            Task task = new Task(entryNode, 0, entryNode.getVal(), 1);
            Schedule newlyMadeSchedule = new Schedule(task, numProcessors);
            calculateCostFunction.setScheduleCost(newlyMadeSchedule);
            newSchedules.add(newlyMadeSchedule);
        }
        return newSchedules;
    }

    public List<Schedule> createPartialSchedulesParallel(
            List<Node> validNodes, int numOfProcessors, Schedule schedule, Graph graph) {

        // concurrently handle valid node partial schedule creation
        // map each validNode to a list of partial schedules created by createPartialSchedules
        // return the list
        return validNodes.parallelStream()
                .flatMap(validNode -> createPartialSchedules(validNode, numOfProcessors, schedule, graph).stream())
                .collect(Collectors.toList());
    }


    public List<Schedule> createPartialSchedules(Node validNode, int numOfProcessors, Schedule schedule, Graph graph) {

        List<Schedule> newSchedules = new ArrayList<>();
        List<Node> parentNodes = graph.getParentNodes(validNode);
        List<Task> newTasks;

        int earliestStartTimeForProcessor;
        int latestParentStartTime;
        int earliestTimeTaskCanStart;

        // Add Task for each processor
        for(int processorID = 1 ; processorID <= numOfProcessors; processorID++){
            earliestStartTimeForProcessor = 0;
            latestParentStartTime = 0;

            // Get all existing task in schedule to get latest starting time
            for(Task task : schedule.getTasks()){
                // This will get the latest finish time of any task for a particular processor (processorID)
                if(task.getProcessor() == processorID && task.getFinishTime() > earliestStartTimeForProcessor){
                    earliestStartTimeForProcessor = task.getFinishTime();
                }

                // This checks if the task is a parent task
                if(parentNodes.contains(task.getNode())){
                    int edgeWeight = graph.getAdjacencyMatrix()[task.getNode().getId()][validNode.getId()];

                    // if the parent task processor is the same as the current processor we are in, then there will be no edge weight value added
                    if(task.getProcessor() == processorID && task.getFinishTime() > latestParentStartTime){
                        latestParentStartTime = task.getFinishTime();
                    } else if (task.getFinishTime() + edgeWeight > latestParentStartTime) {
                        latestParentStartTime = task.getFinishTime() + edgeWeight;
                    }
                }
            }

            // Set latest starting time
            earliestTimeTaskCanStart = Math.max(earliestStartTimeForProcessor, latestParentStartTime);

            // Add task
            Task task = new Task(validNode, earliestTimeTaskCanStart, earliestTimeTaskCanStart + validNode.getVal(), processorID);
            newTasks = new ArrayList<>(schedule.getTasks());
            newTasks.add(task);
            Schedule newlyMadeSchedule = new Schedule(newTasks, numOfProcessors);

            // Set cost
            calculateCostFunction.setScheduleCost(newlyMadeSchedule);

            if(newlyMadeSchedule.isValidScheduleNoOverlap() && newlyMadeSchedule.isValidScheduleSatisfyDependencies(graph)){
                // Add potential schedule
                newSchedules.add(newlyMadeSchedule);
            }

            //check if visited an equivalent schedule already using hashes


        }
        return newSchedules;
    }


}
