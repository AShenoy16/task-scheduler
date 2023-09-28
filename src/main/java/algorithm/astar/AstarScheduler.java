package algorithm.astar;
import model.Node;
import model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AstarScheduler {


    private PriorityQueue<Schedule> open;

    public Schedule run(){
        // calculate bottom level for each node, this can be done by calling setBottomLevelMap on each entry node
        // add first scheule to prio queue

        // if need to, have a for loop for all the possible entry nodes
        // but I think we don't need this as A* should take care of it
//
//        for(){
//            // loop through all entry nodes
//            // find one with longest bottom node
//
//        }

        // get first entry node

        while (open.size() != 0){
            Schedule partialSchdule = open.poll();

            if(partialSchdule.isCompleteSchedule()){
                return partialSchdule;
            }
            //expand partialSchedule into children and compute cost function for each child



                //create new schules and then for every possible child that can happen
                // (make sure that it's valid child, all parents of that child are already in schedule)
                    // create the new tasks that can be in these schedules
            List<Schedule> newList = createPartialSchedules();
                // tell prio queue to know cost function for the scheulde

            // add all new child schedules to open prio queue
            open.addAll(newList);
        }
    }
    public List<Schedule> initialSchedules(List<Node> entryNodes){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();
        // FOR EVERY entry node create new TASK
        // create new schedule with new task make sure added task is early as possible
        // calculate and set cost
        CalculateCostFunction calculateCostFunction = new CalculateCostFunction();
        calculateCostFunction.setScheduleCost(newlyMadeSchedule);
        // add schedule to list of schedules


        return newSchedules;
    }
    public List<Schedule> createPartialSchedules(Schedule schedule){
        // create empty list of schedules
        List<Schedule> newSchedules = new ArrayList<>();
        //TODO Call method or anything to get the valid child tasks

        // FOR EVERY CHILD TASK
            // for every processor
                //TODO create method calculate start times

                // instantiate task with processor
                // create new schedule with new task make sure added task is early as possible
                 // calculate and set cost
        CalculateCostFunction calculateCostFunction = new CalculateCostFunction();
        calculateCostFunction.setScheduleCost(newlyMadeSchedule);
                // add schedule to list of schedules


        return newSchedules;

    }

}
