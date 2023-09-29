package algorithm.astar;

import model.Schedule;

import java.util.Comparator;

/**
 * Priority queue comparator for processing the lowest cost schedules first
 */
public class CostFunctionComparator implements Comparator<Schedule> {
    @Override
    public int compare(Schedule s1, Schedule s2) {
        return Integer.compare(s2.getCost(), s1.getCost());
    }
}
