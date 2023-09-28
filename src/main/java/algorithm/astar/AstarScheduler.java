package algorithm.astar;
import model.Schedule;

import java.util.PriorityQueue;

public class AstarScheduler {


    private PriorityQueue<Schedule> open;

    public Schedule run(){

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

            //expand partialSchdule into children and compute cost function for each child

                //create new schules and then for every possible child that can happen
                    // create and add the new tasks that can be in these schedules

                // tell prio queue to know cost function for the scheulde

            // add all new child schedules to open prio queue
            open.add();
        }
    }

}
