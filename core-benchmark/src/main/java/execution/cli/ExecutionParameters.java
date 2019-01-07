/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import data_setup.BenchmarkParameters;
import execution.BenchmarkType;
import execution.DatasetInfo;
import execution.QueryCase;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import queries.geosparql.GeosparqlBenchmark;

/**
 *
 *
 */
public class ExecutionParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //1) Benchmark Type
    @Parameter(names = {"--benchmark_type", "-bt"}, description = "Benchmarking Type: BOTH, WARM, COLD, DATALOAD", converter = BenchmarkTypeConverter.class, order = 0)
    private BenchmarkType benchmarkType = BenchmarkType.BOTH;

    //2) Iterations
    @Parameter(names = {"--iterations", "-i"}, description = "Number of iterations.", order = 1)
    private Integer iterations = BenchmarkParameters.ITERATIONS;

    //3) Timeout
    @Parameter(names = {"--timeout", "-t"}, description = "Duration until query timeout.", order = 2)
    private Duration timeout = BenchmarkParameters.TIMEOUT;

    //4) Line Limit
    @Parameter(names = {"--lineLimit", "-l"}, description = "Number of lines of output in result files.", order = 3)
    private Integer lineLimit = BenchmarkParameters.RESULT_LINE_LIMIT_ZERO;

    //5) Inferencing Enabled
    @Parameter(names = {"--inferencing", "-inf"}, description = "Inferencing enabled.", arity = 1, order = 4)
    private Boolean inferenceEnabled = true;

    //6) Dataset Map
    @Parameter(names = {"--dataset", "-d"}, description = "Dataset to be used in benchmarking. Either 'GreekGrid', 'WGS84', 'WGS84_Legacy', 'CRS84' or a file/folder path to load.", converter = DatasetConverter.class, order = 5)
    private DatasetInfo datasetInfo = DatasetInfo.WGS_84;

    //7) Query Cases
    @Parameter(names = {"--queryCases", "-q"}, description = "Query cases to be used in benchmarking. Either 'micro', 'macro', 'geosparql' (or other more specific sets in 'query' package) or a file/folder path to load.", order = 6)
    private String queryCaseName = "geosparql";

    private List<QueryCase> queryCases = GeosparqlBenchmark.loadAll();

    //8) Help
    @Parameter(names = {"--help", "-h"}, description = "Application help. @path/to/file can be used to submit parameters in a file.", help = true, order = 7)
    private boolean help = false;

    public List<QueryCase> getQueryCases() {
        return queryCases;
    }

    public BenchmarkType getBenchmarkType() {
        return benchmarkType;
    }

    public Boolean getInferenceEnabled() {
        return inferenceEnabled;
    }

    public TreeMap<String, File> getDatasetMap() {
        return datasetInfo.getDatasetMap();
    }

    public Integer getIterations() {
        return iterations;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public Integer getLineLimit() {
        return lineLimit;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isReady() {
        return !help;
    }

    public String toSummary() {
        return "benchmarkType=" + benchmarkType + ", iterations=" + iterations + ", timeout=" + timeout + ", lineLimit=" + lineLimit + ", inferenceEnabled=" + inferenceEnabled + ", datasetName=" + datasetInfo.getDatasetName() + ", queryCases=" + queryCases;
    }

    @Override
    public String toString() {
        return "ExecutionParameters{" + "benchmarkType=" + benchmarkType + ", iterations=" + iterations + ", timeout=" + timeout + ", lineLimit=" + lineLimit + ", inferenceEnabled=" + inferenceEnabled + ", datasetName=" + datasetInfo.getDatasetName() + ", queryCases=" + queryCases + '}';
    }

    protected void finish() {
        //Query case loading can be dependent upon the number of iterations, i.e. macro queries.
        queryCases = QueryCaseLoader.load(queryCaseName, iterations);
    }

    public static ExecutionParameters extract(String benchmarkName, String[] args) {

        ExecutionParameters executionParameters = new ExecutionParameters();

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
