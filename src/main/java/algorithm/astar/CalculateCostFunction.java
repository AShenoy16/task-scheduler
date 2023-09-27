package algorithm.astar;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CalculateCostFunction {
    private Graph graph;
    public HashMap<Node,Integer> bottomLevelMap = new HashMap<>();

    public HashMap<Node,Integer> getBottomLevelMap(){
        return bottomLevelMap;
    }
    public int setBottomLevelMap(Node node){
        for (Node child : graph.getChildrenNodes(node)) {
            // Recursive call to start from exit nodes
            setBottomLevelMap(child);
        }

        // Get maximum bottom level value out of children nodes
        int maxChildBottomLevel = graph.getChildrenNodes(node).stream()
                .mapToInt(Node::getVal)
                .max()
                .orElse(0);

        // Calculate bottom level value
        int bottomLevel = maxChildBottomLevel + node.getVal();

        // Save bottom level value to map
        bottomLevelMap.put(node,bottomLevel);

        return bottomLevel;
    }

    public void getCostFunction(){}

    // this gets the cost function
    public void setCostFunction(){}

}
