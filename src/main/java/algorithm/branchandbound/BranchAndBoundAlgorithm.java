package algorithm.branchandbound;

import controller.VisualisationController;
import model.Graph;

/**
 * This abstract bnb parent class is needed to efficiently run either sequential or parallel as requested by the user
 */
public abstract class BranchAndBoundAlgorithm {

    public abstract void setController(VisualisationController controller);

    public abstract Schedule run(Graph graph, int numProcessors, int i);

    public abstract Schedule run(Graph graph, int numProcessors);

    public abstract ScheduledTask getCurrentDFSTask();

    public abstract int getShortestPathText();

    public abstract boolean getIsFinished();
}
