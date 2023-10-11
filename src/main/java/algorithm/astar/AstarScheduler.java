package algorithm.astar;
import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.*;
import java.util.concurrent.*;

public class AstarScheduler {

    private PriorityQueue<Schedule> open = new PriorityQueue<>(new CostFunctionComparator());
    private HashSet<Integer> closed = new HashSet<>();

    //TODO sort out CalculateCostFunction instances (maybe make into singleton?)
    private CalculateCostFunction calculateCostFunction;

    /**
     * This runs the Astar algorithm on a input graph and number of processors
     *
     * @param graph The input graph
     * @param numProcessors The number of processors
     * @return A complete schedule
     */
    public Schedule run(Graph graph, int numProcessors){
        long startTimeNano = System.nanoTime();
        long finishTimeNano;

        List<Schedule> newSchedules;
        // TODO remove default constructor of CalculateCostFunction if possible (may need to extract methods + graph class is coupled)
        calculateCostFunction = new CalculateCostFunction(graph);

        // set bottom level values for every node
        // create dependencies for every entry node
        for (Node entryNode : graph.getStartNodes()) {
            calculateCostFunction.setBottomLevelMap(entryNode);
            graph.createDependencies(entryNode);

        }

        List<Node> validEntryNodes = calculateCostFunction.getHighestBottomLevelNodes();
        if(validEntryNodes.isEmpty()){
            System.out.println("Why are entry nodes empty????");
            return null;
        }

        List<Node> sortedList =  calculateCostFunction.getSortedBottomLevel();
        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes);
        open.addAll(initialSchedules);
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        while (open.size() != 0){
            Schedule partialSchedule = open.poll();

            if(partialSchedule.isCompleteSchedule(graph)){
                if(partialSchedule.isValidScheduleNoOverlap() && partialSchedule.isValidScheduleSatisfyDependencies(graph)){
                    finishTimeNano = System.nanoTime();
                    System.out.println("Elapsed Time (Nanoseconds): " + (finishTimeNano - startTimeNano) + " ns");
                    open.clear();
                    return partialSchedule;
                }

            }

            // sort the freeNodes by bottomLevel
            newSchedules = createPartialSchedules(partialSchedule.getFreeNodes(graph), numProcessors, partialSchedule, graph);
//            newSchedules = createPartialSchedulesThreads(partialSchedule.getFreeNodes(graph), numProcessors, 4, partialSchedule, graph, executorService);

            open.addAll(newSchedules);
        }
        
        //TODO add proper fail state?
        return null;
    }

    /**
     * This method creates the initial schedules using the valid entry nodes
     *
     * @param entryNodes The valid entry nodes
     * @return A list of the initial schedules
     */
    public List<Schedule> createInitialSchedules(List<Node> entryNodes){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();

        // Create new schedule for every valid entry node
        for(Node entryNode : entryNodes){
            Task task = new Task(entryNode, 0, entryNode.getVal(), 1);
            Schedule newlyMadeSchedule = new Schedule(task);
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

        // maybe instead of creating free tasks everytime we make a queue or something

        //TODO optimise
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
                Schedule newlyMadeSchedule = new Schedule(newTasks);

                // Set cost
                calculateCostFunction.setScheduleCost(newlyMadeSchedule);

                // if not valid skip

                // if both of them are true valid schedule and add to new schedules
                // if one of them isn't true, predicate is true and go to next iteration

                // Prune 2: remove any invalid schedules
                if(!(newlyMadeSchedule.isValidScheduleNoOverlap() && newlyMadeSchedule.isValidScheduleSatisfyDependencies(graph))){
                    continue;

                }

                //if present in either closed or open list, discard the state
                // Prune 2: removes any duplicates

                //TODO find faster way to check if it's in open
                if(closed.contains(newlyMadeSchedule.hashCode())){
                    continue;
                }

                // not present in closed or open and valid -> add to newSchedules(open)

                newSchedules.add(newlyMadeSchedule);


            }
        }

        // add schedule to closed
        closed.add(schedule.hashCode());

        return newSchedules;

    }

    public List<Schedule> createPartialSchedulesThreads(List<Node> validNodes, int numOfProcessors, int numThreads, Schedule schedule, Graph graph, ExecutorService executorService){
        int size = validNodes.size();
        int chunkSize = (int) Math.ceil((double) size / numThreads);

        // Create a list of Callable tasks
        List<Callable<List<Schedule>>> tasks = new ArrayList<>();
        List<Schedule> schedules = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * chunkSize;
            int endIndex = Math.min((i + 1) * chunkSize, size);

            if (startIndex >= endIndex) {
                break; // No more tasks to create
            }

//            List<Node> threadNodes = validNodes.subList(startIndex, endIndex);
            tasks.add(() -> new MyCallable(validNodes.subList(startIndex, endIndex), numOfProcessors, schedule, graph).call());
        }

        try {
            // Invoke all tasks and collect results
            List<Future<List<Schedule>>> futures = executorService.invokeAll(tasks);
            for (Future<List<Schedule>> future : futures) {
                schedules.addAll(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return schedules;
//        List<Schedule> schedules = new ArrayList<>();
//        List<List<Node>> threadNodes = new ArrayList<>();
//        int size = validNodes.size();
//        int chunkSize = (int) Math.ceil((double) size / numThreads);
//
//        // Split valid nodes between the threads equally
//        for (int i = 0; i < size; i += chunkSize) {
//            int end = Math.min(size, i + chunkSize);
//            threadNodes.add(validNodes.subList(i, end));
//        }
//
//        // Assign thread the different nodes
//        try {
//            List<Callable<List<Schedule>>>tasks = new ArrayList<>();
////            List<Future<List<Schedule>>> futures;
//            for (int i = 0; i < numThreads; i++) {
//                if(i >= threadNodes.size()){
//                    break;
//                }
//                tasks.add(new MyCallable(threadNodes.get(i), numOfProcessors, schedule, graph));
//            }
//
////            futures = executorService.invokeAll(tasks);
//            // Wait for all threads to run before running main thread
////            executorService.shutdown();
////            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//
//            for (Future<List<Schedule>> future : executorService.invokeAll(tasks)) {
//                schedules.addAll(future.get());
//            }
//
//            return schedules;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
    }

    class MyCallable implements Callable<List<Schedule>> {
        private List<Node> validNodes;
        private int numOfProcessors;
        private Schedule schedule;
        private Graph graph;

        public MyCallable(List<Node> validNodes, int numOfProcessors, Schedule schedule, Graph graph) {
            this.validNodes = validNodes;
            this.numOfProcessors = numOfProcessors;
            this.schedule = schedule;
            this.graph = graph;
        }

        @Override
        public List<Schedule> call() {
            // Perform some computation on the sublist
            return createPartialSchedules(validNodes, numOfProcessors, schedule, graph);
        }
    }

//    public List<Schedule> expansion(int numOfProcessors, Schedule schedule, Graph graph) {
//        List<Schedule> newSchedules = new ArrayList<>();
//        List<Node> freeTaskNodes = schedule.getFreeNodes(graph);
//
//        for (Node node : freeTaskNodes) {
//            // Iterate through all available processors
//            for (int processor = 1; processor <= numOfProcessors; processor++) {
//                int earliestStartTimeForProcessor = schedule.getEarliestStartTimeForProcessor(processor);
//                int latestParentStartTime = schedule.getLatestParentStartTime(node, graph);
//                int earliestTimeTaskCanStart = Math.max(earliestStartTimeForProcessor, latestParentStartTime);
//
//                // Create a new task for the free node scheduled on the current processor
//                Task task = new Task(node, earliestTimeTaskCanStart, earliestTimeTaskCanStart + node.getVal(), processor);
//
//                // Clone the existing schedule and add the new task
//                List<Task> newTasks = new ArrayList<>(schedule.getTasks());
//                newTasks.add(task);
//                Schedule newSchedule = new Schedule(newTasks, numOfProcessors);
//
//                // Calculate and set the cost for the new schedule
//                calculateCostFunction.setScheduleCost(newSchedule);
//
//                // Add the new schedule to the list
//                newSchedules.add(newSchedule);
//            }
//        }
//
//        return newSchedules;
//    }

//
//
//
//
//    public void createPartialSchedulesThreads(List<Node> validNodes, int numOfProcessors, int numThreads, Schedule schedule, Graph graph){
//        List<List<Node>> threadNodes = new ArrayList<>();
//        int size = validNodes.size();
//        int chunkSize = (int) Math.ceil((double) size / numThreads);
//
//        // Split valid nodes between the threads equally
//        for (int i = 0; i < size; i += chunkSize) {
//            int end = Math.min(size, i + chunkSize);
//            threadNodes.add(validNodes.subList(i, end));
//        }
//
//
//        // Assign thread the different nodes
//        try {
//            var threads = new ArrayList<Thread>();
//
//            for (int i = 0; i < numThreads; i++) {
//                // Can probably optimise this
//                if(i > threadNodes.size()){
//                    break;
//                }
//                var thread = new Thread(() -> {
//                    // Not 100% sure if this will work
//                    createPartialSchedules(threadNodes.get(i), numOfProcessors, schedule, graph);
//                });
//
//                threads.add(thread);
//                thread.start();
//            }
//
//            // Wait for all threads to run before running main thread
//            for (var thread : threads) {
//                thread.join();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

}
