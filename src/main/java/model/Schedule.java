package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class creates a schedule instance
 */
public class Schedule {
    private List<Task> tasks;
    private int cost;
    private int gapTimes;
    private int finishTime;
    private int numProcessors;

    /**
     * This creates a new schedule with a list of tasks
     * @param tasks list of tasks to add
     * @param numProcessors number of processors
     */
    public Schedule(List<Task> tasks, int numProcessors) {
        this.tasks = tasks;
        this.numProcessors = numProcessors;
        this.tasks.sort(Comparator
                .comparingInt(Task::getProcessor)
                .thenComparingInt(Task::getStartTime)
        );
    }


    /**
     * This creates a new initial schedule with only one task (entry node)
     *
     * @param intialTask The initial task to add
     */
    public Schedule(Task intialTask, int numProcessors) {
        this.tasks = new ArrayList<>(List.of(intialTask));
        this.numProcessors = numProcessors;
    }

    public int getFinishTime(){
        Task maxtask = Collections.max(this.tasks, (obj1, obj2) -> Integer.compare(obj1.getFinishTime(), obj2.getFinishTime()));
        return maxtask.getFinishTime();
    }

    public int getNumProcessors() {
        return numProcessors;
    }

    public int getGapTimes() {
        return gapTimes;
    }

    public void setGapTimes(int gapTimes) {
        this.gapTimes = gapTimes;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * get all the tasks for a particular processor id
     * @param processorId
     * @return
     */
    private List<Task> getTaskByProcessor(int processorId){

        ArrayList<Task> processorTask = new ArrayList<>();

        for(Task task: tasks){
            if(task.getProcessor() == processorId){
                processorTask.add(task);
            }

        }
        
        return processorTask;
    }




    /**
     * This gets the free tasks for a specific schedule
     * @param graph
     * @return
     */
    public List<Node> getFreeNodes(Graph graph) {
        List<Node> freeTaskNodes = new ArrayList<>();

        ArrayList<Node> allScheduleNodes = getAllNodes();

        for (Node node : graph.getNodes()) {
            // Check if the node is not in the current schedule
            if (!allScheduleNodes.contains(node)) {
                List<Node> dependentNodes = graph.getDependenciesByNode(node);;

                if(dependentNodes.isEmpty()){
                    freeTaskNodes.add(node);
                    continue;
                }

                // Check if all parent nodes are in the schedule
                boolean allParentsInSchedule = allScheduleNodes.containsAll(dependentNodes);

                // If all parent nodes are in the schedule, add the node to the list of free nodes
                if (allParentsInSchedule) {
                    freeTaskNodes.add(node);
                }
            }
        }

        return freeTaskNodes;
    }



    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * This method gets all of the nodes in the schedule
     *
     * @return A list of nodes in the schedule
     */
    public ArrayList<Node> getAllNodes(){
        ArrayList<Node> allNodes = new ArrayList<>();
        for(Task task: tasks){
            allNodes.add(task.getNode());
        }

        return allNodes;
    }


    /**
     * Method to check if a schedule has any overlapping tasks
     * @return
     */
    public boolean isValidScheduleNoOverlap(){

        for( int i = 1; i <= this.numProcessors; i++){
            // loop through all the processors
            // get all the tasks on that processor
            List<Task> sortedTasks = getTaskByProcessor(i);

            for(int j = 0; j < sortedTasks.size() - 1; j++){
                Task currentTask = sortedTasks.get(j);
                Task nextTask = sortedTasks.get(j + 1);

                //make sure there's no overlap
                if (!(currentTask.getStartTime() < currentTask.getFinishTime() &&
                        currentTask.getFinishTime() <= nextTask.getStartTime() &&
                        nextTask.getStartTime() < nextTask.getFinishTime())) {
                    return false;
                }
            }

        }

        return true;

    }

    /**
     * Checks if schedule satifisies all dependencies in the graph
     * @param graph
     * @return
     */

    public boolean isValidScheduleSatisfyDependencies(Graph graph){

        for(Task task: tasks){
            Node node = task.getNode();

            List<Node> dependencies = graph.getDependenciesByNode(node);


            for(Node dependency: dependencies){
                // if parent on the same processor as child
                // no communication cost

                Task task1 = getTaskByNode(dependency);

                if(task1 == null){
                    return false;
                }

                if(task1.getProcessor() == task.getProcessor()){

                    // dependency not yet finished but child already started
                    if (task1.getFinishTime() > task.getStartTime()){
                        return false;
                    }

                }else{
                    // Parent, child on different processors, account for communication cost


                    int edgeWeight = graph.getAdjacencyMatrix()[task1.getNode().getId()][task.getNode().getId()];

                    // overlap occurs
                    if(task.getStartTime() < task1.getFinishTime() + edgeWeight){
                        return false;
                    }
                }
            }

        }

        // no overlap occurs
        return true;

    }

    /**
     * This method gets the Task of a schedule by its Node
     * @param node
     * @return
     */
    private Task getTaskByNode(Node node){
        for(Task task: tasks){
            if(task.getNode().getId() == node.getId()){
                return task;
            }
        }
        return null;
    }



    public int getCost() {
        return cost;
    }

    /**
     * This method checks if the schedule is complete given the input graph
     *
     * @param graph The input graph
     * @return True if the schedule is complete, False if not
     */
    public boolean isCompleteSchedule(Graph graph){
        return(tasks.size() == graph.getAdjacencyMatrix().length);
    }

    /**
     * Checks if a solution is valid
     * @param graph
     * @return
     */
    public boolean isValid(Graph graph){
        return (isValidScheduleNoOverlap() && isValidScheduleSatisfyDependencies(graph));
    }


    @Override
    public int hashCode() {
        return tasks.hashCode();
    }
}
