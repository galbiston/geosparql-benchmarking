/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class QueryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);

    //Query Resource Folders
    private static final String NON_TOPOLOGICAL_FUNCTIONS = "../Core/benchmarking_files/micro_benchmark_queries/non_topological_functions";
    private static final String SPATIAL_JOINS = "../Core/benchmarking_files/micro_benchmark_queries/spatial_joins";
    private static final String SPATIAL_SELECTIONS = "../Core/benchmarking_files/micro_benchmark_queries/spatial_selections";
    private static final String AGGREGATIONS = "../Core/benchmarking_files/micro_benchmark_queries/aggregations";

    //Given Shapes Files
    private static final String GIVEN_FOLDER = "../Core/benchmarking_files/given_files";
    private static final String GIVEN_POINT = QueryLoader.readFile(GIVEN_FOLDER + "/givenPoint.txt");
    private static final String GIVEN_RADIUS = QueryLoader.readFile(GIVEN_FOLDER + "/givenRadius.txt");
    private static final String GIVEN_LINESTRING_1 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString1.txt");
    private static final String GIVEN_LINESTRING_2 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString2.txt");
    private static final String GIVEN_LINESTRING_3 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString3.txt");
    private static final String GIVEN_POLYGON = QueryLoader.readFile(GIVEN_FOLDER + "/givenPolygon.txt");

    public static HashMap<String, String> loadSpatialSelectionsQuery_14() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("SpatialSelections#Query14", readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS));
        return queryMap;
    }

    /**
     * Load set of all queries except those with Strabon only syntax (Query 6,
     * 28 and 29).
     *
     * @return
     */
    public static HashMap<String, String> loadMainQuerySet() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.putAll(loadNonTopologicalFunctionsQueries());
        queryMap.putAll(loadSpatialSelectionsQueries());
        queryMap.putAll(loadSpatialJoinsQueries());
        return queryMap;
    }

    /**
     * This set of queries does not include Query 6 as it uses Strabon only
     * syntax that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static HashMap<String, String> loadNonTopologicalFunctionsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("NonTopologicalFunctions#Query1", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query1.spl"));
        queryMap.put("NonTopologicalFunctions#Query2", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query2.spl"));
        queryMap.put("NonTopologicalFunctions#Query3", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl"));
        queryMap.put("NonTopologicalFunctions#Query4", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl"));
        queryMap.put("NonTopologicalFunctions#Query5", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query5.spl"));
        return queryMap;
    }

    /**
     * Query 6 uses Strabon only syntax "strdf:area" that is not specified in
     * the GeoSPARQL standard.
     *
     * @return
     */
    public static HashMap<String, String> loadNonTopologicalFunctionsQuery_6() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("NonTopologicalFunctions#Query6", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query6.spl"));
        return queryMap;
    }

    /**
     * These queries utilise specific WKT shapes which are loaded from resource
     * files.
     *
     * @return
     */
    public static HashMap<String, String> loadSpatialSelectionsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("SpatialSelections#Query7", readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_1));
        queryMap.put("SpatialSelections#Query8", readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("SpatialSelections#Query9", readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("SpatialSelections#Query10", readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_2));
        queryMap.put("SpatialSelections#Query11", readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("SpatialSelections#Query12", readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_3));
        queryMap.put("SpatialSelections#Query13", readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("SpatialSelections#Query14", readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS));
        queryMap.put("SpatialSelections#Query15", readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS));
        queryMap.put("SpatialSelections#Query16", readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("SpatialSelections#Query17", readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));

        return queryMap;
    }

    public static HashMap<String, String> loadSpatialJoinsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("SpatialJoins#Query18", readFile(SPATIAL_JOINS + "/Query18.spl"));
        queryMap.put("SpatialJoins#Query19", readFile(SPATIAL_JOINS + "/Query19.spl"));
        queryMap.put("SpatialJoins#Query20", readFile(SPATIAL_JOINS + "/Query20.spl"));
        queryMap.put("SpatialJoins#Query21", readFile(SPATIAL_JOINS + "/Query21.spl"));
        queryMap.put("SpatialJoins#Query22", readFile(SPATIAL_JOINS + "/Query22.spl"));
        queryMap.put("SpatialJoins#Query23", readFile(SPATIAL_JOINS + "/Query23.spl"));
        queryMap.put("SpatialJoins#Query24", readFile(SPATIAL_JOINS + "/Query24.spl"));
        queryMap.put("SpatialJoins#Query25", readFile(SPATIAL_JOINS + "/Query25.spl"));
        queryMap.put("SpatialJoins#Query26", readFile(SPATIAL_JOINS + "/Query26.spl"));
        queryMap.put("SpatialJoins#Query27", readFile(SPATIAL_JOINS + "/Query27.spl"));
        return queryMap;
    }

    /**
     * These queries use Strabon only syntax "strdf:extent" and "strdf:union"
     * that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static HashMap<String, String> loadAggregationsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("Aggregations#Query28", readFile(AGGREGATIONS + "/Query28.spl"));
        queryMap.put("Aggregations#Query29", readFile(AGGREGATIONS + "/Query29.spl"));
        return queryMap;
    }

    public static String readFile(String filepath) {

        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }
    }

}
