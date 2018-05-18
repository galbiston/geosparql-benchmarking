/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_setup.BenchmarkParameters;
import data_setup.Dataset_CRS84;
import data_setup.Dataset_Greek_Grid;
import data_setup.Dataset_WGS84;
import data_setup.Dataset_WGS84_Legacy;
import data_setup.GraphURI;
import execution.BenchmarkExecution.BenchmarkType;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import queries.geographica.MacroBenchmark;
import queries.geographica.MicroBenchmark;
import queries.geosparql.GeosparqlBenchmark;

/**
 *
 *
 */
public class ExecutionParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<QueryCase> queryCases;
    private final BenchmarkType benchmarkType;
    private final Boolean inferenceEnabled;
    private final TreeMap<String, File> datasetMap;
    private final Integer iterations;
    private final Duration timeout;
    private final Integer lineLimit;

    public ExecutionParameters(List<QueryCase> queryCases, BenchmarkType benchmarkType, Boolean inferenceEnabled, TreeMap<String, File> datasetMap, Integer iterations, Duration timeout, Integer lineLimit) {
        this.queryCases = queryCases;
        this.benchmarkType = benchmarkType;
        this.inferenceEnabled = inferenceEnabled;
        this.datasetMap = datasetMap;
        this.iterations = iterations;
        this.timeout = timeout;
        this.lineLimit = lineLimit;

    }

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
        return datasetMap;
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

    private static final Integer QUERY_CASE_POSITION = 0;
    private static final Integer BENCHMARK_TYPE_POSITION = 1;
    private static final Integer INFERENCE_ENABLED_POSITION = 2;
    private static final Integer ITERATIONS_POSITION = 3;
    private static final Integer TIMEOUT_POSITION = 4;
    private static final Integer LINE_LIMIT_POSITION = 5;
    private static final Integer DATASET_POSITION = 6;

    public static final ExecutionParameters extract(String[] args) {
        return extract(args, 0);
    }

    public static final ExecutionParameters extract(String[] args, Integer argOffset) {

        LOGGER.info("---------------------------Benchmarking Parameters----------------------------");
        List<QueryCase> queryCases;
        if (args.length > QUERY_CASE_POSITION + argOffset) {
            switch (args[QUERY_CASE_POSITION + argOffset].toLowerCase()) {
                case "micro":
                    queryCases = MicroBenchmark.loadMainQuerySet();
                    LOGGER.info("Query Set: Geographica Microbenchmark.");
                    break;
                case "macro":
                    queryCases = MacroBenchmark.loadAll();
                    LOGGER.info("Query Set: Geographica Macrobenchmark.");
                    break;
                case "geosparql":
                    queryCases = GeosparqlBenchmark.loadAll();
                    LOGGER.info("Query Set: GeoSPARQL Microbenchmark.");
                default:
                    File fileArg = new File(args[QUERY_CASE_POSITION + argOffset]);
                    if (fileArg.exists()) {
                        if (fileArg.isDirectory()) {
                            queryCases = QueryLoader.readFolder(fileArg);
                        } else {
                            queryCases = QueryLoader.readQuery(fileArg.getAbsolutePath());
                        }
                    } else {
                        LOGGER.error("Unknown Query Case Set: {}. Expected 'micro', 'macro', 'geosparql' or file path to load.", args[QUERY_CASE_POSITION + argOffset]);
                        throw new IllegalArgumentException("Unknown Query Case Set: " + args[QUERY_CASE_POSITION + argOffset] + ". Expected 'micro', 'macro', 'geosparql' or file path to load.");
                    }
            }
        } else {
            queryCases = GeosparqlBenchmark.loadAll();
            LOGGER.info("Query Set: Defaulting to GeoSPARQL Microbenchmark.");
        }

        BenchmarkType benchmarkType;
        if (args.length > BENCHMARK_TYPE_POSITION + argOffset) {
            benchmarkType = BenchmarkExecution.BenchmarkType.valueOf(args[BENCHMARK_TYPE_POSITION + argOffset].toUpperCase());
            LOGGER.info("Benchmark Type: {}", benchmarkType);
        } else {
            benchmarkType = BenchmarkExecution.BenchmarkType.BOTH;
            LOGGER.info("Benchmark Type: Defaulting to BOTH benchmarks.");
        }

        Boolean inferenceEnabled;
        if (args.length > INFERENCE_ENABLED_POSITION + argOffset) {
            inferenceEnabled = Boolean.valueOf(args[INFERENCE_ENABLED_POSITION + argOffset]);
            LOGGER.info("Inference Enabled: {}", inferenceEnabled);
        } else {
            inferenceEnabled = true;
            LOGGER.info("Inference Enabled: Defaulting to true");
        }

        Integer iterations;
        if (args.length > ITERATIONS_POSITION + argOffset) {
            iterations = Integer.parseInt(args[ITERATIONS_POSITION + argOffset]);
            LOGGER.info("Iterations: {}", iterations);
        } else {
            iterations = BenchmarkParameters.ITERATIONS;
            LOGGER.info("Iterations: Defaulting to {}", iterations);
        }

        Duration timeout;
        if (args.length > TIMEOUT_POSITION + argOffset) {
            timeout = Duration.ofSeconds(Integer.parseInt(args[TIMEOUT_POSITION + argOffset]));
            LOGGER.info("Timout: {}", timeout);
        } else {
            timeout = BenchmarkParameters.TIMEOUT;
            LOGGER.info("Timout: Defaulting to {}", timeout.toString());
        }

        Integer lineLimit;
        if (args.length > LINE_LIMIT_POSITION + argOffset) {
            lineLimit = Integer.parseInt(args[LINE_LIMIT_POSITION + argOffset]);
            LOGGER.info("Line Limit: {}", lineLimit);
        } else {
            lineLimit = BenchmarkParameters.RESULT_LINE_LIMIT_ZERO;
            LOGGER.info("Line Limit: Defaulting to line limit zero, so no detailed results output.");
        }

        TreeMap<String, File> datasetMap;
        if (args.length > DATASET_POSITION + argOffset) {
            switch (args[DATASET_POSITION + argOffset].toLowerCase()) {
                case "GreekGrid":
                    datasetMap = Dataset_Greek_Grid.getAll();
                    LOGGER.info("Dataset: GreekGrid");
                    break;
                case "WGS84":
                    datasetMap = Dataset_WGS84.getAll();
                    LOGGER.info("Dataset: WG84");
                    break;
                case "WGS84_Legacy":
                    datasetMap = Dataset_WGS84_Legacy.getAll();
                    LOGGER.info("Dataset: WG84 Legacy");
                    break;
                case "CRS84":
                    datasetMap = Dataset_CRS84.getAll();
                    LOGGER.info("Dataset: CRS84");
                default:
                    File fileArg = new File(args[DATASET_POSITION + argOffset]);
                    if (fileArg.exists()) {
                        File[] files;
                        if (fileArg.isDirectory()) {
                            files = fileArg.listFiles();
                        } else {
                            files = new File[]{fileArg};
                        }
                        datasetMap = new TreeMap<>();
                        for (File file : files) {
                            datasetMap.put(GraphURI.USER + file.getName(), file);
                        }

                    } else {
                        LOGGER.error("Unknown Data Set: {}. Expected 'GreekGrid', 'WGS84', 'WGS84_Legacy', 'CRS84' or file path to load.", args[DATASET_POSITION + argOffset]);
                        throw new IllegalArgumentException("Unknown Data Set: " + args[DATASET_POSITION + argOffset] + ". Expected 'GreekGrid', 'WGS84', 'WGS84_Legacy', 'CRS84' or file path to load.");
                    }

            }
        } else {
            datasetMap = Dataset_CRS84.getAll();
            LOGGER.info("Dataset: Defaulting to CRS84");
        }

        LOGGER.info("--------------------------------------------------------------------------------");
        return new ExecutionParameters(queryCases, benchmarkType, inferenceEnabled, datasetMap, iterations, timeout, lineLimit);
    }

}
