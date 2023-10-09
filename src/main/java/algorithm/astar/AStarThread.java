package algorithm.astar;

import model.Schedule;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class AStarThread extends ForkJoinWorkerThread {
    private int priority; // Add a priority field

    /**
     * Creates a ForkJoinWorkerThread operating in the given pool with a specific priority.
     *
     * @param pool     the pool this thread works in
     * @param priority the priority for this thread
     * @throws NullPointerException if pool is null
     */
    protected AStarThread(ForkJoinPool pool, int priority) {
        super(pool);
        this.priority = priority; // Set the priority field
        setPriority(priority);
    }


    public static class CustomWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private final ForkJoinPool pool;
        private final List<Schedule> schedules; // Add schedules as a field

        public CustomWorkerThreadFactory(ForkJoinPool pool, List<Schedule> schedules) {
            this.pool = pool;
            this.schedules = schedules; // Initialize the schedules field
        }

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            int priority = calculatePriority(); // Calculate the priority based on schedules
            return new AStarThread(pool, priority);
        }

        private int calculatePriority() {
            // Initialize a variable to hold the minimum cost
            int minCost = Integer.MAX_VALUE;

            // Find the minimum cost among all schedules
            for (Schedule schedule : schedules) {
                int cost = schedule.getCost();
                if (cost < minCost) {
                    minCost = cost;
                }
            }

            // Calculate priority inversely proportional to the minimum cost
            int scaleFactor = 100; // Adjust this value based on your requirements
            return Thread.MAX_PRIORITY - (minCost / scaleFactor);
        }

    }
}
