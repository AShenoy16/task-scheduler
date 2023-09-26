package io;

import model.Graph;
import model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IOHandler {
    public Graph readDot(String filePath) throws IOException {
        var graph = new Graph();
        var nodes = new ArrayList<Node>();
        var br = new BufferedReader(new FileReader(filePath));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("}") || line.contains("{")) continue;
                int equals = line.indexOf("=");
                int closeBracket = line.indexOf("]");
                int weight = Integer.parseInt(line.substring(equals+1, closeBracket));
        }

        return null;
    }
}
