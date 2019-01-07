/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import execution.cli.ExecutionParameters;
import geosparql_benchmarking.geosparql_jena.SystemType;

/**
 *
 *
 */
public class JenaExecutionParameters extends ExecutionParameters {

    //9) Query Cases
    @Parameter(names = {"--systemType", "-st"}, description = "System Type for Apache Jena benchmarking. Expected: TDB, TDB_UNION, MEMORY, MEMORY_UNION, NO_INDEX.", converter = SystemTypeConverter.class, order = 8)
    private SystemType systemType = SystemType.MEMORY;

    public SystemType getSystemType() {
        return systemType;
    }

    public String toSummary() {
        return systemType + ", " + super.toSummary();
    }

    @Override
    public String toString() {
        return "JenaExecutionParameters{" + toSummary() + '}';
    }

    public static JenaExecutionParameters extract(String benchmarkName, String[] args) {

        JenaExecutionParameters executionParameters = new JenaExecutionParameters();

        JCommander jCommander = JCommander.newBuilder()
                .addObject(executionParameters)
                .build();

        jCommander.setProgramName(benchmarkName);
        jCommander.parse(args);
        executionParameters.finish();

        if (executionParameters.isHelp()) {
            jCommander.usage();
        }

        return executionParameters;
    }

}
