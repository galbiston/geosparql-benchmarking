package gr.uoa.di.rdf.Geographica.systemsundertest;

import gr.uoa.di.rdf.Geographica.experiments.Experiment;
import gr.uoa.di.rdf.Geographica.experiments.MacroComputeStatisticsExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MacroGeocodingExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MacroMapSearchExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MacroRapidMappingExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MacroReverseGeocodingExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MicroAggregationsExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MicroJoinsExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MicroNonTopologicalExperiment;
import gr.uoa.di.rdf.Geographica.experiments.MicroSelectionsExperiment;
import gr.uoa.di.rdf.Geographica.experiments.SyntheticExperiment;
import gr.uoa.di.rdf.Geographica.queries.QueriesSet;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RunSystemUnderTest {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected Options options = new Options();
    protected CommandLine cmd = null;
    protected SystemUnderTest sut = null;

    protected void printHelp() {
        System.err.println("Usage: " + this.getClass().getSimpleName() + " [options] (run|print) (MicroNonTopological|MicroSelections|MicroJoins|MicroAggregations|MacroMapSearch|MacroRapidMapping|MacroReverseGeocoding|Synthetic)+");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getSimpleName(), options);
    }

    protected void addOptions() {
        options.addOption("?", "help", false, "Print help message");

        options.addOption("q", "queries", true, "List of queries to run");
        options.addOption("r", "repetitions", true, "Repetitions for experiments (default: 5)");
        options.addOption("t", "timeout", true, "Timeout (seconds) for experiments (default 30mins)");
        options.addOption("m", "runtime", true, "Run time (minutes) for experiments (Macro scenarios) (default: 2hours)");

        options.addOption("N", "syntheticN", true, "Parameter for synthetic experiments");
        options.addOption("l", "logpath", true, "Log path");
    }

    private void logAllArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }
    }

    protected void logOptions() {
        int repetitions = Integer.parseInt((cmd.getOptionValue("r") != null ? cmd.getOptionValue("r") : "5"));
        int timeoutSecs = (cmd.getOptionValue("t") != null ? Integer.parseInt(cmd.getOptionValue("t")) : 30 * 60); // 30 mins
        int runTimeInMinutes = (cmd.getOptionValue("m") != null ? Integer.parseInt(cmd.getOptionValue("runtime")) : 2 * 60); // 2 hours
        int syntheticN = Integer.parseInt((cmd.getOptionValue("N") != null ? cmd.getOptionValue("N") : "0"));

        LOGGER.info("Shared options");
        LOGGER.info("Repetitions:\t{}", repetitions);
        LOGGER.info("Time out:\t{} seconds", timeoutSecs);
        LOGGER.info("Run time:\t{} minutes", runTimeInMinutes);
        LOGGER.info("N:\t{}", syntheticN);
        LOGGER.info("Log Path:\t{}", cmd.getOptionValue("logpath"));
        LOGGER.info("Queries to run:\t{}", cmd.getOptionValue("queries"));

    }

    protected abstract void initSystemUnderTest() throws Exception;

    private String[] parseArguments(String[] args) {
        DefaultParser parser = new DefaultParser();

        try {
            cmd = parser.parse(options, args);

            // Print help if required
            if (cmd.hasOption("?")) {
                System.out.println("Checkpoint #1");
                printHelp();
                System.exit(0);
            }

            args = cmd.getArgs();
            // Check arguments
            if (args.length < 2) {
                System.out.println("Arguments not correct");
                System.out.println("Arguments: " + Arrays.toString(args));
                printHelp();
                System.exit(-1);
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            System.out.println("Checkpoint #5");
            printHelp();
        }
        return args;
    }

    private void runExperiment(String[] args) throws MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException, IOException {

        int repetitions = Integer.parseInt((cmd.getOptionValue("r") != null ? cmd.getOptionValue("r") : "5"));
        int timeoutSecs = (cmd.getOptionValue("t") != null ? Integer.parseInt(cmd.getOptionValue("t")) : 30 * 60); // 30 mins
        int runTimeInMinutes = (cmd.getOptionValue("m") != null ? Integer.parseInt(cmd.getOptionValue("runtime")) : 2 * 60); // 2 hours

        int syntheticN = Integer.parseInt((cmd.getOptionValue("N") != null ? cmd.getOptionValue("N") : "0"));
        String logPath = cmd.getOptionValue("l");

        // List of queries to run
        String queriesToRunString = cmd.getOptionValue("q");
        int[] queriesToRun = null;
        if (queriesToRunString != null) {
            String[] queriesToRunStringArray = queriesToRunString.split(" ");
            queriesToRun = new int[queriesToRunStringArray.length];
            for (int i = 0; i < queriesToRunStringArray.length; i++) {
                queriesToRun[i] = Integer.parseInt(queriesToRunStringArray[i]);
            }
        }

        // Select and execute experiments
        Experiment experiment = null;

        for (int i = 1; i < args.length; i++) {
            // Micro experiments
            if (args[i].equalsIgnoreCase("MicroNonTopological")) {
                experiment = new MicroNonTopologicalExperiment(sut, repetitions, timeoutSecs, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MicroSelections")) {
                experiment = new MicroSelectionsExperiment(sut, repetitions, timeoutSecs, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MicroJoins")) {
                experiment = new MicroJoinsExperiment(sut, repetitions, timeoutSecs, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MicroAggregations")) {
                experiment = new MicroAggregationsExperiment(sut, repetitions, timeoutSecs, queriesToRun, logPath);
                // Macro experiments
            } else if (args[i].equalsIgnoreCase("MacroReverseGeocoding")) {
                experiment = new MacroReverseGeocodingExperiment(sut, repetitions, timeoutSecs, runTimeInMinutes, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MacroMapSearch")) {
                experiment = new MacroMapSearchExperiment(sut, repetitions, timeoutSecs, runTimeInMinutes, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MacroRapidMapping")) {
                experiment = new MacroRapidMappingExperiment(sut, repetitions, timeoutSecs, runTimeInMinutes, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MacroComputeStatistics")) {
                experiment = new MacroComputeStatisticsExperiment(sut, repetitions, timeoutSecs, runTimeInMinutes, queriesToRun, logPath);
            } else if (args[i].equalsIgnoreCase("MacroGeocoding")) {
                experiment = new MacroGeocodingExperiment(sut, repetitions, timeoutSecs, runTimeInMinutes, queriesToRun, logPath);
                // Synthetic
            } else if (args[i].equalsIgnoreCase("Synthetic")) {
                if (syntheticN == 0) {
                    System.err.println("For synthetic data experiments parameter N>0 is obligatory");
                }
                experiment = new SyntheticExperiment(sut, repetitions, timeoutSecs, syntheticN, queriesToRun, logPath);
            } else {
                System.err.println("Error: " + args[i] + " is not recognized.");
                System.err.println("Only MicroNonTopological, MicroSelections, MicroJoins, MicroAggreagations.");
                System.exit(-1);
            }

            // Run, test or print queries of experiments
            if (args[0].equalsIgnoreCase("run")) {
                LOGGER.info("Start {}", experiment.getClass().getName());
                experiment.run();
                LOGGER.info("End {}", experiment.getClass().getName());
            } else if (args[0].equalsIgnoreCase("print")) {
                System.out.println("\n" + experiment.getClass().getName() + "\n");
                QueriesSet qs = experiment.getQueriesSet();
                for (int j = 0; j < qs.getQueriesN(); j++) {
                    if (qs.getQuery(j, 0).getQuery() != null) {
                        System.out.println("\nQuery " + j + " - " + qs.getQuery(j, 0).getLabel() + ":\n" + qs.getQuery(j, 0).getQuery());
                    }
                }
            } else {
                System.out.println("Checkpoint #4");
                printHelp();
            }
        }
    }

    public void run(String[] args) throws Exception {

        addOptions();

        logAllArguments(args);

        args = parseArguments(args);

        logOptions();

        initSystemUnderTest();

        runExperiment(args);

        System.exit(0);
    }
}
