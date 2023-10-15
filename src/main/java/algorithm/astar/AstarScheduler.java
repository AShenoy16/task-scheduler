package algorithm.astar;
import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AstarScheduler {

    private PriorityQueue<Schedule> open = new PriorityQueue<>(new CostFunctionComparator());
    private HashSet<Integer> closed = new HashSet<>();

    private HashSet<Integer> openHash = new HashSet<>();

    private CalculateCostFunction calculateCostFunction;

    /**
     * This runs the Astar algorithm on a input graph and number of processors
     *
     * @param graph The input graph
     * @param numProcessors The number of processors
     * @return A complete schedule
     */
    public Schedule run(Graph graph, int numProcessors){

        List<Schedule> newSchedules;
        calculateCostFunction = new CalculateCostFunction(graph);

        // set bottom level values for every node
        // create dependencies for every entry node
        for (Node entryNode : graph.getStartNodes()) {
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);

        }

        List<Node> validEntryNodes = calculateCostFunction.getHighestBottomLevelNodes();


        // create initial schedules and add to priority queue
        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes, numProcessors);
        open.addAll(initialSchedules);

        while (open.size() != 0){
            // pop top schedule from queue
            Schedule partialSchedule = open.poll();

            if(partialSchedule.isCompleteSchedule(graph)){
                open.clear();
                return partialSchedule;
            }

            // sort the freeNodes by bottomLevel
            List<Node> sortedNodes = partialSchedule.getFreeNodes(graph)
                    .stream()
                    .sorted(Comparator.comparingInt(node -> calculateCostFunction.bottomLevelofNode(node)))
                    .toList();


            // create new schedules
            newSchedules = createPartialSchedules(sortedNodes, numProcessors, partialSchedule, graph);

            open.addAll(newSchedules);

            // add all hashes to openHash
            newSchedules.forEach(schedule -> openHash.add(schedule.hashCode()));
        }

        return null;
    }

    /**
     * This method creates the initial schedules using the valid entry nodes
     *
     * @param entryNodes The valid entry nodes
     * @return A list of the initial schedules
     */
    public List<Schedule> createInitialSchedules(List<Node> entryNodes, int numProcessors){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();

        // Create new schedule for every valid entry node
        for(Node entryNode : entryNodes){
            Task task = new Task(entryNode, 0, entryNode.getVal(), 1);
            Schedule newlyMadeSchedule = new Schedule(task, numProcessors);
            calculateCostFunction.setScheduleCost(newlyMadeSchedule);
            newSchedules.add(newlyMadeSchedule);
        }

        return newSchedules;
    }

    /**
     * This method creates new partial schedules based on valid child nodes/tasks
     *
     * @param validNodes A list of valid nodes to add on as a task to the schedules
     * @param numOfProcessors The number of processors
     * @param schedule The existing schedule
     * @param graph The graph
     * @return A new list of partial schedules with the an additional task added
     */
    public List<Schedule> createPartialSchedules(List<Node> validNodes, int numOfProcessors, Schedule schedule, Graph graph){
        // create empty list of schedules, parent nodes and new tasks to add
        List<Schedule> newSchedules = new ArrayList<>();
        List<Node> parentNodes;
        List<Task> newTasks;


        // go through each free task and perform expansion
        for(Node validNode : validNodes){
            parentNodes = graph.getParentNodes(validNode);

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

                        if(task.getProcessor() == processorID){
                            latestParentStartTime = Math.max(latestParentStartTime, task.getFinishTime());
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

                // calculate gap times
                int gapStartTime = 0;
                for(Task newTask : newTasks){
                    if(newTask.getProcessor() == processorID){
                        gapStartTime = Math.max(gapStartTime, newTask.getFinishTime());
                    }
                }
                int gapTime = earliestTimeTaskCanStart - gapStartTime;

                newTasks.add(task);
                Schedule newlyMadeSchedule = new Schedule(newTasks, numOfProcessors);
                newlyMadeSchedule.setGapTimes(schedule.getGapTimes() + gapTime);

                // Prune 1:
                // check if it's a valid schedule

                if(!newlyMadeSchedule.isValid(graph)){
                    continue;
                }

                int hash = newlyMadeSchedule.hashCode();

                // Prune 2:
                // check if in open or closed
                if(closed.contains(hash) || openHash.contains(hash)){
                    continue;
                }


                // Set cost
                calculateCostFunction.setScheduleCost(newlyMadeSchedule);


                newSchedules.add(newlyMadeSchedule);


            }
        }

        // add schedule to closed
        closed.add(schedule.hashCode());

        return newSchedules;

    }

}
