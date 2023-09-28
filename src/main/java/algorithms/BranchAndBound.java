package algorithms;

import model.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchAndBound {
    public Schedule run(int numProcesses, Graph graph){
        State state = new State(numProcesses, graph);
        Map<Integer, List<ScheduledTask>> rootQueue = new HashMap<>();
        for(Integer n : graph.getStartNodes()){
            rootQueue.put(n, new ArrayList<>());
            ScheduledTask task = new ScheduledTask(0,0,n,null, graph);
            PartialSolution partialSolution = new PartialSolution(task, numProcesses);
            partialSolution.getRootQueue().putAll(rootQueue);
            dfs(state, partialSolution);
        }
        return null;
    }


    private void dfs(State state, PartialSolution partialSolution) {
    }

}
