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
    HashSet<Integer> closed = new HashSet<>();
    HashSet<Integer> openHash = new HashSet<>();
    public Schedule run(Graph graph, int numProcessors, int numThreads) {
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

        //TODO Add dynamic number of threads, set to 4 threads for now
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes, numProcessors);
        List<Callable<Schedule>> tasks = new ArrayList<>();

        if(initialSchedules.size() > numThreads){
            for(Schedule schedule : initialSchedules){
                tasks.add(() -> new MyCallable(numProcessors, schedule, graph).call());
            }
        } else {
            List<Schedule> secondInitialSchedules = new ArrayList<>();
            for(Schedule schedule1 : initialSchedules){
                secondInitialSchedules.addAll(createPartialSchedules(schedule1.getFreeNodes(graph), numProcessors, schedule1, graph));
            }
            for(Schedule schedule2 : secondInitialSchedules){
                tasks.add(() -> new MyCallable(numProcessors, schedule2, graph).call());
            }
        }

        try {
            // Invoke all tasks and collect results
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


    private Schedule getlowestCostSchedule(List<Schedule> scheduleList) {
        if (scheduleList.isEmpty()) {
            return null; // Handle the case when the list is empty
        }

        Schedule lowestCostSchedule = scheduleList.get(0);
        int lowestCost = lowestCostSchedule.getCost();

        for (Schedule schedule : scheduleList) {
            int cost = schedule.getCost();
            if (cost < lowestCost) {
                lowestCost = cost;
                lowestCostSchedule = schedule;
            }
        }

        return lowestCostSchedule;
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

    public Schedule createPartialSchedulesParallel2(Schedule schedule, int numProcessors, Graph graph) {
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
    class MyCallable implements Callable<Schedule> {
        private int numOfProcessors;
        private Schedule schedule;
        private Graph graph;

        public MyCallable(int numOfProcessors, Schedule schedule, Graph graph) {
            this.numOfProcessors = numOfProcessors;
            this.schedule = schedule;
            this.graph = graph;
        }

        @Override
        public Schedule call() {
            // Perform some computation on the sublist
            return createPartialSchedulesParallel2(schedule, numOfProcessors, graph);
        }
    }
//    public List<Schedule> createPartialSchedulesParallel(
//            List<Node> validNodes, int numOfProcessors, Schedule schedule, Graph graph) {
//
//        // concurrently handle valid node partial schedule creation
//        // map each validNode to a list of partial schedules created by createPartialSchedules
//        // return the list
//        return validNodes.parallelStream()
//                .flatMap(validNode -> createPartialSchedules(validNode, numOfProcessors, schedule, graph).stream())
//                .collect(Collectors.toList());
//    }

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
                int gapStartTime = 0;

                for(Task task1 : newTasks){
                    if(task1.getProcessor() == processorID){
                        gapStartTime = Math.max(gapStartTime, task1.getFinishTime());
                    }
                }
                int gapTime = earliestTimeTaskCanStart - gapStartTime;

                newTasks.add(task);
//                Collections.sort(newTasks,  Comparator.comparing(Task::getProcessor));
                Schedule newlyMadeSchedule = new Schedule(newTasks, numOfProcessors);
                newlyMadeSchedule.setIdleTime(schedule.getIdleTime() + gapTime);

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

                int hash = newlyMadeSchedule.hashCode();

                //TODO find faster way to check if it's in open
                if(closed.contains(hash) || openHash.contains(hash)){
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
//    public List<Schedule> createPartialSchedules(Node validNode, int numOfProcessors, Schedule schedule, Graph graph) {
//
//        List<Schedule> newSchedules = new ArrayList<>();
//        List<Node> parentNodes = graph.getDependenciesByNode(validNode);
//        List<Task> newTasks;
//
//        int earliestStartTimeForProcessor;
//        int latestParentStartTime;
//        int earliestTimeTaskCanStart;
//
//        // Add Task for each processor
//        for(int processorID = 1 ; processorID <= numOfProcessors; processorID++){
//            earliestStartTimeForProcessor = 0;
//            latestParentStartTime = 0;
//
//            // Get all existing task in schedule to get latest starting time
//            for(Task task : schedule.getTasks()){
//                // This will get the latest finish time of any task for a particular processor (processorID)
//                if(task.getProcessor() == processorID && task.getFinishTime() > earliestStartTimeForProcessor){
//                    earliestStartTimeForProcessor = task.getFinishTime();
//                }
//
//                // This checks if the task is a parent task
//                if(parentNodes.contains(task.getNode())){
//                    int edgeWeight = graph.getAdjacencyMatrix()[task.getNode().getId()][validNode.getId()];
//
//                    // if the parent task processor is the same as the current processor we are in, then there will be no edge weight value added
//                    if(task.getProcessor() == processorID && task.getFinishTime() > latestParentStartTime){
//                        latestParentStartTime = task.getFinishTime();
//                    } else if (task.getFinishTime() + edgeWeight > latestParentStartTime) {
//                        latestParentStartTime = task.getFinishTime() + edgeWeight;
//                    }
//                }
//            }
//
//            // Set latest starting time
//            earliestTimeTaskCanStart = Math.max(earliestStartTimeForProcessor, latestParentStartTime);
//
//            // Add task
//            Task task = new Task(validNode, earliestTimeTaskCanStart, earliestTimeTaskCanStart + validNode.getVal(), processorID);
//            newTasks = new ArrayList<>(schedule.getTasks());
//            newTasks.add(task);
//            Schedule newlyMadeSchedule = new Schedule(newTasks);
//
//            // Set cost
//            calculateCostFunction.setScheduleCost(newlyMadeSchedule);
//
//            if(newlyMadeSchedule.isValidScheduleNoOverlap() && newlyMadeSchedule.isValidScheduleSatisfyDependencies(graph)){
//                // Add potential schedule
//                newSchedules.add(newlyMadeSchedule);
//            }
//
//            //check if visited an equivalent schedule already using hashes
//
//
//        }
//        return newSchedules;
//    }


}
