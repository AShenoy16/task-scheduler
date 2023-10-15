package algorithm.branchandbound;

import controller.VisualisationController;
import model.Graph;

public abstract class BranchAndBoundAlgorithm {

    public abstract void setController(VisualisationController controller);

    public abstract Schedule run(Graph graph, int numProcessors, int i);

    public abstract Schedule run(Graph graph, int numProcessors);

    public abstract ScheduledTask getCurrentDFSTask();

    public abstract int getShortestPathText();

    public abstract boolean getIsFinished();
}
