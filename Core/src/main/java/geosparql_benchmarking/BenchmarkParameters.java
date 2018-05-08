/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking;

import geosparql_benchmarking.experiments.QueryCase;
import geosparql_benchmarking.experiments.QueryLoader;
import geosparql_benchmarking.geographica.MicroBenchmark;
import java.time.Duration;
import java.util.List;

/**
 *
 *
 */
public class BenchmarkParameters {

    public static final Integer ITERATIONS = 5; //1;
    public static final Duration TIMEOUT = Duration.ofSeconds(3600);

    /**
     * Set to zero for no query result output.
     */
    public static final Integer RESULT_LINE_LIMIT_ZERO = 0;

    /**
     * 5000 line limit per query result output file.
     */
    public static final Integer RESULT_LINE_LIMIT_5000 = 5000;

    //public static final List<QueryCase> QUERY_CASES = QueryLoader.loadSpatialSelectionsQuery_14();
    //public static final List<QueryCase> QUERY_CASES = QueryLoader.loadNonTopologicalFunctionsQueries();
    public static final List<QueryCase> QUERY_CASES = MicroBenchmark.loadMainQuerySet();

}
