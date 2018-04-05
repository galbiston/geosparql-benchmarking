/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String NON_TOPOLOGICAL_FUNCTIONS = "micro_benchmark_queries/non_topological_functions";
    private static final String SPATIAL_JOINS = "micro_benchmark_queries/spatial_joins";
    private static final String SPATIAL_SELECTIONS = "micro_benchmark_queries/spatial_selections";
    private static final String AGGREGATIONS = "micro_benchmark_queries/aggregations";

    //Given Shapes Files
    private static final String GIVEN_FOLDER = "given_files";
    private static final String GIVEN_POINT = QueryLoader.readFile(GIVEN_FOLDER + "/givenPoint.txt");
    private static final String GIVEN_RADIUS = QueryLoader.readFile(GIVEN_FOLDER + "/givenRadius.txt");
    private static final String GIVEN_LINESTRING_1 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString1.txt");
    private static final String GIVEN_LINESTRING_2 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString2.txt");
    private static final String GIVEN_LINESTRING_3 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString3.txt");
    private static final String GIVEN_POLYGON = QueryLoader.readFile(GIVEN_FOLDER + "/givenPolygon.txt");

    /**
     * This set of queries does not include Query 6 as it uses Strabon only
     * syntax that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static HashMap<String, String> loadNonTopologicalFunctionsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("NonTopological Functions - Query1", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query1.spl"));
        queryMap.put("NonTopological Functions - Query2", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query2.spl"));
        queryMap.put("NonTopological Functions - Query3", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl"));
        queryMap.put("NonTopological Functions - Query4", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl"));
        queryMap.put("NonTopological Functions - Query5", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query5.spl"));
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
        queryMap.put("NonTopological Functions - Query6", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query6.spl"));
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
        queryMap.put("Spatial Selections - Query7", readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_1));
        queryMap.put("Spatial Selections - Query8", readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("Spatial Selections - Query9", readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("Spatial Selections - Query10", readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_2));
        queryMap.put("Spatial Selections - Query11", readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("Spatial Selections - Query12", readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_3));
        queryMap.put("Spatial Selections - Query13", readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("Spatial Selections - Query14", readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS));
        queryMap.put("Spatial Selections - Query15", readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS));
        queryMap.put("Spatial Selections - Query16", readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));
        queryMap.put("Spatial Selections - Query17", readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON));

        return queryMap;
    }

    public static HashMap<String, String> loadSpatialJoinsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("Spatial Joins - Query18", readFile(SPATIAL_JOINS + "/Query18.spl"));
        queryMap.put("Spatial Joins - Query19", readFile(SPATIAL_JOINS + "/Query19.spl"));
        queryMap.put("Spatial Joins - Query20", readFile(SPATIAL_JOINS + "/Query20.spl"));
        queryMap.put("Spatial Joins - Query21", readFile(SPATIAL_JOINS + "/Query21.spl"));
        queryMap.put("Spatial Joins - Query22", readFile(SPATIAL_JOINS + "/Query22.spl"));
        queryMap.put("Spatial Joins - Query23", readFile(SPATIAL_JOINS + "/Query23.spl"));
        queryMap.put("Spatial Joins - Query24", readFile(SPATIAL_JOINS + "/Query24.spl"));
        queryMap.put("Spatial Joins - Query25", readFile(SPATIAL_JOINS + "/Query25.spl"));
        queryMap.put("Spatial Joins - Query26", readFile(SPATIAL_JOINS + "/Query26.spl"));
        queryMap.put("Spatial Joins - Query27", readFile(SPATIAL_JOINS + "/Query27.spl"));
        return queryMap;
    }

    /**
     * These queries use Strabon only syntax "strdf:extent" and "strdf:union"
     * that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static HashMap<String, String> loadAggergationsQueries() {

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("Aggregations - Query28", readFile(AGGREGATIONS + "/Query28.spl"));
        queryMap.put("Aggregations - Query29", readFile(AGGREGATIONS + "/Query29.spl"));
        return queryMap;
    }

    public static String readFile(String filename) {

        InputStream input = QueryLoader.class.getResourceAsStream(filename);

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {} - {}", filename, ex.getMessage());
            return null;
        }
    }

}
