package visualisation;

import algorithm.branchandbound.PartialSolution;
import model.Node;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class VisualiseGraph extends FxViewer {
    private final org.graphstream.graph.Graph graph;
    private final Queue<PartialSolution> partialSolutionQueue = new LinkedList<>();
    private final int n;
    private int nodeIndex;
    private List<Node> nodes;

    public VisualiseGraph(org.graphstream.graph.Graph graph, Viewer.ThreadingModel threadingModel) {
        super(graph, threadingModel);
        this.graph = graph;
        n = graph.getNodeCount();
        nodeIndex = n + 3;
        initialiseLabels();
    }

    public void initialiseLabels() {
        for (org.graphstream.graph.Node node : graph) {
            node.setAttribute("ui.label", node.getId() + "");
            node.setAttribute("ui.style", "text-alignment: center;\n"
                    +"\tstroke-mode: plain; stroke-color:grey; stroke-width: 3px;"
                    + "\tfill-mode: plain; fill-color: grey;\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: black; text-style: bold;\n");
        }
    }

    /**
     * Visualize traversal of a schedule
     */
    public void visualizeQueuedSchedule() {
        if (nodeIndex > n + 2) {
            nodeIndex = 0;
            nodes = partialSolutionQueue.poll().getVisitedNodes();
            resetNodeColours();
            return;
        }

        if (nodeIndex < n) {
            org.graphstream.graph.Node node = graph.getNode(String.valueOf(nodes.get(nodeIndex).getId()));
            node.setAttribute("ui.style", "fill-color: #4895EF;");
        }

        if (nodeIndex == n-1) {
            for (org.graphstream.graph.Node node : graph) {
                node.setAttribute("ui.style", "stroke-color:#B4F89E;");
            }
        }

        nodeIndex++;
    }

    /**
     *  Reset all GraphStream graph nodes to white
     */
    public void resetNodeColours() {
        for (org.graphstream.graph.Node node : graph) {
            node.setAttribute("ui.style", "fill-color: white; stroke-color:grey;");
        }
    }

    public Queue<PartialSolution> getPartialSolutionQueue() {
        return partialSolutionQueue;
    }
}
