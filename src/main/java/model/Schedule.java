package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Schedule {
    private List<Task> tasks;
    private int cost;
    private int numProcessors;

    /**
     * This creates a new schedule given a list of tasks
     *
     * @param tasks A list of tasks to add to a schedule
     */
    public Schedule(List<Task> tasks, int numProcessors) {
        this.tasks = tasks;
        this.numProcessors = numProcessors;
    }

    /**
     * This creates a new initial schedule with only one task (entry node)
     *
     * @param intialTask The initial task to add
     */
    public Schedule(Task intialTask){
        this.tasks = new ArrayList<>(List.of(intialTask));
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

    public int getEarliestStartTimeForProcessor(int processor) {
        int earliestStartTime = 0;
        for (Task task : tasks) {
            if (task.getProcessor() == processor && task.getFinishTime() > earliestStartTime) {
                earliestStartTime = task.getFinishTime();
            }
        }
        return earliestStartTime;
    }

    public int getLatestParentStartTime(Node node, Graph graph) {
        int latestParentStartTime = 0;
        List<Node> parentNodes = graph.getParentNodes(node);

        for (Task task : tasks) {
            if (parentNodes.contains(task.getNode())) {
                int edgeWeight = graph.getAdjacencyMatrix()[task.getNode().getId()][node.getId()];
                if (task.getFinishTime() + edgeWeight > latestParentStartTime) {
                    latestParentStartTime = task.getFinishTime() + edgeWeight;
                }
            }
        }

        return latestParentStartTime;
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


    public boolean isValidScheduleSatisfyDependencies(Graph graph){

        for(Task task: tasks){
            Node node = task.getNode();

            List<Node> dependencies = graph.getParentsByNode(node);

//            List<Task> dependencies = getDependencies(node, graph);

            for(Node dependency: dependencies){
                // if parent on the same processor as child
                // make sure dependency finish time > task start time
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
                    // Parent, child on different processors, need to account for
                    // communication cost

                    //get the edge weight from parent to child from the graph

                    int edgeWeight = graph.getAdjacencyMatrix()[task1.getNode().getId()][task.getNode().getId()];

                    if(task.getStartTime() < task1.getFinishTime() + edgeWeight){
                        return false;
                    }
                }
            }

        }

        return true;

    }

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


    @Override
    public int hashCode() {
        return tasks.hashCode();
    }
}
