package io;

public class SchedulingOptions {
    public int numProcessors;
    public int numCores = 1;
    public boolean isParallel = false;
    public boolean isVisualised = false;
    public String inputFileName;
    public String outputFileName;

    public SchedulingOptions(String inputFileName, int numProcessors) {
        this.inputFileName = inputFileName;
        this.numProcessors = numProcessors;
    }
    public void setDefaultOutputFileName() {
        outputFileName = inputFileName + "-output.dot";

        // remove .dot extension from input file name when including in output file name
        int lastDotIndex = inputFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            // Remove the file extension
            outputFileName = inputFileName.substring(0, lastDotIndex) + "-output.dot";
        }
    }

    public void setOutputFileName(String arg) {
        if (arg == null) {
            throw new RuntimeException("Output filename not specified");
        }

        outputFileName = arg + ".dot";
    }
}
