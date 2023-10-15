package io;

import algorithm.branchandbound.ScheduledTask;
import model.*;

import java.io.*;
import java.util.ArrayList;

public class IOHandler {
    private String edgesString = "";

    /**
     * Read a .dot file and return a Graph object.
     *
     * @param filePath Directory of the .dot file
     * @return Graph object containing adjacency matrix, start nodes and end nodes
     */
    public Graph readDot(String filePath) {
        try {
            var nodes = new ArrayList<Node>();
            var edges = new ArrayList<Edge>();

            var br = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = br.readLine()) != null) {
                boolean hasWeight = line.contains("Weight");
                if (!hasWeight || line.contains("}") || line.contains("{")) {
                    continue;
                }
                String l = line.replaceAll("\\s", "");

                int weight = Integer.parseInt(l.substring(l.indexOf("=") + 1, l.indexOf("]")));

                boolean isEdge = l.contains("->");
                if (isEdge) {
                    int arrowIndex = l.indexOf("-");
                    int src = Integer.parseInt(l.substring(0, arrowIndex));
                    int dest = Integer.parseInt(l.substring(arrowIndex + 2, l.indexOf("[")));
                    edges.add(new Edge(src, dest, weight));
                    appendToEdgesString(line);
                } else {
                    int id = Integer.parseInt(l.substring(0, l.indexOf("[")));
                    nodes.add(new Node(id, weight));
                }
            }
            return new Graph(nodes, edges);

        } catch (IOException e) {
            throw new RuntimeException("Ensure the input dot graph is valid");
        }
    }

    /**
     * Construct a String containing all edges to add to output file.
     *
     * @param edge edge to add to String
     */
    private void appendToEdgesString(String edge) {
        edgesString += edgesString.isEmpty() ? "\t" + edge : "\n" + "\t" + edge;
    }

    /**
     * Write schedule to a .dot file
     *
     * @param schedule schedule to create output file from
     * @param fileName file name of the output file
     */
    public void writeDot(Schedule schedule, String fileName) {
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            bw.write("digraph \"" + fileName + "\" {");
            bw.newLine();

            for (Task t : schedule.getTasks()) {
                bw.write("\t\t" + t.getNode().getId() + "\t" + "[" + "Weight=" + t.getNode().getVal() + ",Start=" + t.getStartTime() + ",Processor=" + t.getProcessor() + "];");
                bw.newLine();
            }

            if (!edgesString.isEmpty()) {
                bw.write(edgesString);
                bw.newLine();
            }

            bw.write("}");

        } catch (IOException e) {
            throw new RuntimeException("Encountered error while creating output file");
        }
    }

    /**
     * This method writes the Branch and Bound schedule to an output dot file
     * @param bnbSchedule the branch and bound schedule
     * @param fileName to write to
     */
    public void bnbWriteDot(algorithm.branchandbound.Schedule bnbSchedule, String fileName) {
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            bw.write("digraph \"" + fileName + "\" {");
            bw.newLine();

            for (ScheduledTask scheduledTask: bnbSchedule.getScheduledTaskList()) {

                bw.write("\t\t" + scheduledTask.getNode().getId() + "\t" + "[" + "Weight=" + scheduledTask.getNode().getVal() + ",Start=" + scheduledTask.getStartTime() + ",Processor=" + scheduledTask.getProcessorId() + "];");
                bw.newLine();
            }

            if (!edgesString.isEmpty()) {
                bw.write(edgesString);
                bw.newLine();
            }

            bw.write("}");

        } catch (IOException e) {
            throw new RuntimeException("Encountered error while creating output file");
        }
    }
}
