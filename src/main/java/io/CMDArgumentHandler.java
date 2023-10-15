package io;

import org.apache.commons.cli.*;

public class CMDArgumentHandler {
    public static SchedulingOptions getSchedulingOptions(String[] args) {
        if (args == null || args.length < 2) {
            throw new RuntimeException("InputFileName or numProcessors arguments not supplied");
        }

        var options = new Options();
        options.addOption("p", true, "use N cores for execution in parallel");
        options.addOption("v", false, "visualise the search;");
        options.addOption("o", true, "output file is named OUTPUT");

        CommandLineParser parser = new DefaultParser();
        try {
            var schedulingOptions = new SchedulingOptions(args[0], Integer.valueOf(args[1]));

            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("p")) {
                schedulingOptions.isParallel = true;
                schedulingOptions.numCores = Integer.parseInt(cmd.getOptionValue("p"));
            }

            if (cmd.hasOption("v")) {
                schedulingOptions.isVisualised = true;
            }

            if (cmd.hasOption("o")) {
                schedulingOptions.setOutputFileName(cmd.getOptionValue("o"));
            } else {
                schedulingOptions.setDefaultOutputFileName();
            }

            return schedulingOptions;

        } catch (ParseException e) {
            throw new RuntimeException("Invalid arguments provided");
        }
    }
}
