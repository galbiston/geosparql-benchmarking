/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking;

import geosparql_benchmarking.experiments.QueryLoader;
import java.time.Duration;
import java.util.HashMap;

/**
 *
 *
 */
public class BenchmarkParameters {

    public static final Integer ITERATIONS = 1; //10;
    public static final Duration TIMEOUT = Duration.ofSeconds(3600);

    //public static final HashMap<String, String> QUERY_MAP = QueryLoader.loadSpatialSelectionsQuery_14();
    public static final HashMap<String, String> QUERY_MAP = QueryLoader.loadNonTopologicalFunctionsQueries();
    //public static final HashMap<String, String> QUERY_MAP = QueryLoader.loadMainQuerySet();

}
