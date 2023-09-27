package io;

import model.Edge;
import model.Graph;
import model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IOHandler {
    public static Graph readDot(String filePath) throws IOException {
        var nodes = new ArrayList<Node>();
        var edges = new ArrayList<Edge>();

        var br = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("}") || line.contains("{")) {
                continue;
            }
            line = line.replaceAll("\\s", "");

            int weight = Integer.parseInt(line.substring(line.indexOf("=") + 1, line.indexOf("]")));

            boolean isEdge = line.contains("->");
            if (isEdge) {
                int arrowIndex = line.indexOf("-");
                int src = Integer.parseInt(line.substring(0, arrowIndex));
                int dest = Integer.parseInt(line.substring(arrowIndex + 2, line.indexOf("[")));
                edges.add(new Edge(src, dest, weight));
            } else {
                int id = Integer.parseInt(line.substring(0, line.indexOf("[")));
                nodes.add(new Node(id, weight));
            }
        }

        return new Graph(nodes, edges);
    }
}
