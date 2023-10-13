package model;

import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;

public class VisualiseGraph extends FxViewer {
    private org.graphstream.graph.Graph graph;

    public VisualiseGraph(org.graphstream.graph.Graph graph, Viewer.ThreadingModel threadingModel) {
        super(graph, threadingModel);
        this.graph = graph;
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
}
