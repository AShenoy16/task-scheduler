package algorithm.astar;

import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AstarParallel {
    private CalculateCostFunction calculateCostFunction;
    private int globalCost = Integer.MAX_VALUE;
    private HashSet<Integer> closed = new HashSet<>();
    private HashSet<Integer> openHash = new HashSet<>();

    /**
     * This runs the Astar algorithm using multiple threads in parallel on an input graph and number of processors
     *
     * @param graph The input graph
     * @param numProcessors The number of processors
     * @return A complete schedule
     */
    public Schedule run(Graph graph, int numProcessors, int numThreads) {
        calculateCostFunction = new CalculateCostFunction(graph);

        // Set bottom level values for every node
        // Create dependencies for every entry node
        for (Node entryNode : graph.getStartNodes()) {
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);
        }

        List<Node> validEntryNodes = calculateCostFunction.getHighestBottomLevelNodes();
        if (validEntryNodes.isEmpty()) {
            return null;
        }

        // Create new threads
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Create initial schedules
        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes, numProcessors);
        List<Callable<Schedule>> tasks = new ArrayList<>();

        // Set schedules to the threads for parallelisation
        if(initialSchedules.size() > numThreads){
            for(Schedule schedule : initialSchedules){
                tasks.add(() -> new ScheduleCallable(numProcessors, schedule, graph).call());
            }
        } else {
            List<Schedule> secondInitialSchedules = new ArrayList<>();
            for(Schedule schedule1 : initialSchedules){
                secondInitialSchedules.addAll(createPartialSchedules(schedule1.getFreeNodes(graph), numProcessors, schedule1, graph));
            }
            for(Schedule schedule2 : secondInitialSchedules){
                tasks.add(() -> new ScheduleCallable(numProcessors, schedule2, graph).call());
            }
        }

        try {
            // Invoke all threads and collect results
            List<Future<Schedule>> futures = executorService.invokeAll(tasks);
            for (Future<Schedule> future : futures) {
                if(future.get() != null && future.get().getCost() == globalCost){
                    executorService.shutdown();
                    openHash.clear();
                    closed.clear();
                    return future.get();
                }
            }

            return null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * This method creates the initial schedules using the valid entry nodes
     *
     * @param entryNodes The valid entry nodes
     * @return A list of the initial schedules
     */
    private List<Schedule> createInitialSchedules(List<Node> entryNodes, int numProcessors) {
        List<Schedule> newSchedules = new ArrayList<>();
        for (Node entryNode : entryNodes) {
            Task task = new Task(entryNode, 0, entryNode.getVal(), 1);
            Schedule newlyMadeSchedule = new Schedule(task, numProcessors);
            calculateCostFunction.setScheduleCost(newlyMadeSchedule);
            newSchedules.add(newlyMadeSchedule);
        }
        return newSchedules;
    }

    /**
     * This method gets the optimal schedule based on a parent schedule input, the number of processors, and a graph.
     * This method is called in each thread.
     *
     * @param schedule The parent schedule to start the Astar algorithm
     * @param numProcessors The number of processors
     * @param graph The graph
     * @return The optimal schedule
     */
    private Schedule getOptimalSchedule(Schedule schedule, int numProcessors, Graph graph) {
        PriorityQueue<Schedule> open2 = new PriorityQueue<>(new CostFunctionComparator());
        open2.add(schedule);

        while (open2.size() != 0) {
            Schedule partialSchedule = open2.poll();
            if(globalCost <= partialSchedule.getCost()){
                open2.clear();
                return null;
            }
            if (partialSchedule.isCompleteSchedule(graph)) {
                open2.clear();
                globalCost = partialSchedule.getCost();
                return partialSchedule;
            }
            List<Node> sortedNodes = partialSchedule.getFreeNodes(graph)
                    .stream()
                    .sorted(Comparator.comparingInt(node -> calculateCostFunction.bottomLevelofNode(node)))
                    .toList();
            List<Schedule> newSchedules = createPartialSchedules(sortedNodes, numProcessors, partialSchedule, graph);

            open2.addAll(newSchedules);
            newSchedules.parallelStream().forEach(s -> openHash.add(s.hashCode()));
        }

        return null;
    }

    /**
     * This Callable represents a task that can be executed asynchronously via multiple threads.
     * This is the way we are able to parallelise the Astar algorithm
     */
    class ScheduleCallable implements Callable<Schedule> {
        private int numOfProcessors;
        private Schedule schedule;
        private Graph graph;

        public ScheduleCallable(int numOfProcessors, Schedule schedule, Graph graph) {
            this.numOfProcessors = numOfProcessors;
            this.schedule = schedule;
            this.graph = graph;
        }

        @Override
        public Schedule call() {
            // Perform some computation on the sublist
            return getOptimalSchedule(schedule, numOfProcessors, graph);
        }
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

                //Get gap time
                int gapTime = earliestTimeTaskCanStart - gapStartTime;
                newTasks.add(task);
                Schedule newlyMadeSchedule = new Schedule(newTasks, numOfProcessors);

                //Add gap time
                newlyMadeSchedule.setGapTimes(schedule.getGapTimes() + gapTime);

                // Prune 1:
                // check if it's a valid schedule
                if(!newlyMadeSchedule.isValid(graph)){
                    continue;
                }

                // Remove any duplicate schedules
                int hash = newlyMadeSchedule.hashCode();
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
