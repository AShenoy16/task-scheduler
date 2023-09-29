package algorithm.astar;
import model.Graph;
import model.Node;
import model.Schedule;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AstarScheduler {


    private PriorityQueue<Schedule> open;

    private CalculateCostFunction calculateCostFunction = new CalculateCostFunction();
    public Schedule run(Graph graph, int numProcessors){
        // calculate bottom level for each node, this can be done by calling setBottomLevelMap on each entry node
        // add first scheule to prio queue

        // if need to, have a for loop for all the possible entry nodes
        // but I think we don't need this as A* should take care of it

        // set bottom level values for every node
        for (Node entryNode : graph.getStartNodes()) {
            calculateCostFunction.setBottomLevelMap(entryNode);
        }

        //TODO (maybe) optimise getting the valid entry nodes
        List<Node> validEntryNodes = calculateCostFunction.getHighestBottomLevelNodes();
        if(validEntryNodes.isEmpty()){
            System.out.println("Why are entry nodes empty????");
            return null;
        }

        List<Node>  sortedList =  calculateCostFunction.getSortedBottomLevel();
        List<Schedule> initialSchedules = createInitialSchedules(validEntryNodes, numProcessors);
        open.addAll(initialSchedules);

        // return the hashmap

        // get the sorted list descending order,
        // get first entry node

        while (open.size() != 0){
            Schedule partialSchedule = open.poll();

            if(partialSchedule.isCompleteSchedule()){
                return partialSchedule;
            }
            //expand partialSchedule into children and compute cost function for each child




                //create new schules and then for every possible child that can happen
                // (make sure that it's valid child, all parents of that child are already in schedule)

            List<Schedule> newSchedules = createPartialSchedules(graph.getValidChildrenNodes(partialSchedule, sortedList), numProcessors, partialSchedule, graph);

                    // create the new tasks that can be in these schedules

                // tell prio queue to know cost function for the scheulde

            // add all new child schedules to open prio queue
            open.addAll(newSchedules);
        }
    }
    public List<Schedule> createInitialSchedules(List<Node> entryNodes, int numOfProcessors){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();

        for(Node entryNode : entryNodes){
            Task task = new Task(entryNode, 0, entryNode.getVal(), 1);
            Schedule newlyMadeSchedule = new Schedule(task, numOfProcessors);
            calculateCostFunction.setScheduleCost(newlyMadeSchedule);
            newSchedules.add(newlyMadeSchedule);
        }

        return newSchedules;
    }
    public List<Schedule> createPartialSchedules(List<Node> validChildNodes, int numOfProcessors, Schedule schedule, Graph graph){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();

        //TODO optimise
        for(Node validChildNode : validChildNodes){
            // find latest time for particular processor
            // from the schedule, find task with latest finish times in each processor
            // find parent with latest finish times including any possible edge values
            // get minimum starting time possible for particular processor

            List<Node> parentNodes = graph.getParentNodes(validChildNode);

            //TODO rename variables, among other other things
            int earliestStartTimeForProcessor;
            int latestParentStartTime;
            int earliestTimeTaskCanStart;

            //
            for(int processorID = 1 ; processorID <= numOfProcessors; processorID++){
                earliestStartTimeForProcessor = 0;
                latestParentStartTime = 0;
                List<Node> scheduleNodes = schedule.getAllNodes();

                // This for loop gets the start time
                for(Task task : schedule.getTasks()){
                    // this will get the latest finish time of any task for a particular processor (processorID)
                    if(task.getProcessor() == processorID && task.getFinishTime() > earliestStartTimeForProcessor){
                        earliestStartTimeForProcessor = task.getFinishTime();
                    }

                    // This checks if the task is a parent task
                    if(parentNodes.contains(task.getNode())){
                        int edgeWeight = graph.getAdjacencyMatrix()[task.getNode().getId()][validChildNode.getId()];

                        // if the parent task processor is the same as the current processor we are in, then there will be no edge weight value added
                        if(task.getProcessor() == processorID && latestParentStartTime > task.getFinishTime()){
                            latestParentStartTime = task.getFinishTime();
                        } else if (task.getFinishTime() + edgeWeight > latestParentStartTime) {
                            latestParentStartTime = task.getFinishTime() + edgeWeight;
                        }
                    }
                }

                earliestTimeTaskCanStart = Math.max(earliestStartTimeForProcessor, latestParentStartTime);

                Task task = new Task(validChildNode, earliestTimeTaskCanStart, earliestTimeTaskCanStart + validChildNode.getVal(), processorID);
                Schedule newlyMadeSchedule = new Schedule(task, numOfProcessors);
                calculateCostFunction.setScheduleCost(newlyMadeSchedule);
                newSchedules.add(newlyMadeSchedule);
            }
        }

        return newSchedules;

    }

}
