import io.IOHandler;
import model.Graph;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileRead {
    final String directory = "src/test/graphs/";

    @Test
    public void ReadGraph() throws IOException {
        Graph g = IOHandler.readDot(directory + "Nodes_7_OutTree.dot");
        assertEquals(g.getNodeWeightings(), List.of(5,6,5,6,4,7,7));
        assertEquals(g.getStartNodes(), List.of(0));
        assertEquals(g.getEndNodes(), List.of(2,3,4,5,6));
        assertArrayEquals(g.getAdjacencyMatrix()[0], new int[]{0,15,11,11,0,0,0});
        assertArrayEquals(g.getAdjacencyMatrix()[1], new int[] {0,0,0,0,19,4,21});
        assertArrayEquals(g.getAdjacencyMatrix()[2], new int[] {0,0,0,0,0,0,0});
        assertArrayEquals(g.getAdjacencyMatrix()[6], new int[] {0,0,0,0,0,0,0});
    }
}
