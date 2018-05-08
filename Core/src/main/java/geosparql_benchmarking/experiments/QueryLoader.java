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
import java.util.ArrayList;
import java.util.List;
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
    private static final String MAP_SEARCH_AND_BROWSING = "../Core/benchmarking_files/macro_benchmark_queries/map_search_and_browsing";
    private static final String RAPID_MAPPING = "../Core/benchmarking_files/macro_benchmark_queries/rapid_mapping";
    private static final String REVERSE_GEOCODING = "../Core/benchmarking_files/macro_benchmark_queries/reverse_geocoding";

    //Given Shapes Files
    private static final String GIVEN_FOLDER = "../Core/benchmarking_files/given_files";
    private static final String GIVEN_POINT = QueryLoader.readFile(GIVEN_FOLDER + "/givenPoint.txt");
    private static final String GIVEN_RADIUS = QueryLoader.readFile(GIVEN_FOLDER + "/givenRadius.txt");
    private static final String GIVEN_LINESTRING_1 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString1.txt");
    private static final String GIVEN_LINESTRING_2 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString2.txt");
    private static final String GIVEN_LINESTRING_3 = QueryLoader.readFile(GIVEN_FOLDER + "/givenLineString3.txt");
    private static final String GIVEN_POLYGON = QueryLoader.readFile(GIVEN_FOLDER + "/givenPolygon.txt");
    private static final List<QueryPair> GEONAMES = QueryLoader.readQueryPairs(GIVEN_POINT + "/geonames.txt");
    private static final List<QueryPair> TIMESTAMPS = QueryLoader.readQueryPairs(GIVEN_POINT + "/timestamps.txt");
    private static final List<QueryPair> POINTS = QueryLoader.readQueryPairs(GIVEN_POINT + "/points.txt");

    public static List<QueryCase> loadSpatialSelectionsQuery_14() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query14", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        return queryCases;
    }

    public static List<QueryCase> loadNonTopologicalFunctionsQuery_3() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl")));
        return queryCases;
    }

    public static List<QueryCase> loadNonTopologicalFunctionsQuery_4() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl")));
        return queryCases;
    }

    /**
     * Load set of all queries except those with Strabon only syntax (Query 6,
     * 28 and 29).
     *
     * @return
     */
    public static List<QueryCase> loadMainQuerySet() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadNonTopologicalFunctionsQueries());
        queryCases.addAll(loadSpatialSelectionsQueries());
        queryCases.addAll(loadSpatialJoinsQueries());
        return queryCases;
    }

    /**
     * This set of queries does not include Query 6 as it uses Strabon only
     * syntax that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static List<QueryCase> loadNonTopologicalFunctionsQueries() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query1", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query1.spl")));
        queryCases.add(new QueryCase("Query2", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query2.spl")));
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl")));
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl")));
        queryCases.add(new QueryCase("Query5", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query5.spl")));
        return queryCases;
    }

    /**
     * Query 6 uses Strabon only syntax "strdf:area" that is not specified in
     * the GeoSPARQL standard.
     *
     * @return
     */
    public static List<QueryCase> loadNonTopologicalFunctionsQuery_6() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query6", "NonTopologicalFunctions", readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query6.spl")));
        return queryCases;
    }

    /**
     * These queries utilise specific WKT shapes which are loaded from resource
     * files.
     *
     * @return
     */
    public static List<QueryCase> loadSpatialSelectionsQueries() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query7", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_1)));
        queryCases.add(new QueryCase("Query8", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query9", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query10", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_2)));
        queryCases.add(new QueryCase("Query11", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query12", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_3)));
        queryCases.add(new QueryCase("Query13", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query14", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query15", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query16", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query17", "SpatialSelections", readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));

        return queryCases;
    }

    public static List<QueryCase> loadSpatialJoinsQueries() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query18", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query18.spl")));
        queryCases.add(new QueryCase("Query19", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query19.spl")));
        queryCases.add(new QueryCase("Query20", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query20.spl")));
        queryCases.add(new QueryCase("Query21", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query21.spl")));
        queryCases.add(new QueryCase("Query22", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query22.spl")));
        queryCases.add(new QueryCase("Query23", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query23.spl")));
        queryCases.add(new QueryCase("Query24", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query24.spl")));
        queryCases.add(new QueryCase("Query25", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query25.spl")));
        queryCases.add(new QueryCase("Query26", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query26.spl")));
        queryCases.add(new QueryCase("Query27", "SpatialJoins", readFile(SPATIAL_JOINS + "/Query27.spl")));
        return queryCases;
    }

    /**
     * These queries use Strabon only syntax "strdf:extent" and "strdf:union"
     * that is not specified in the GeoSPARQL standard.
     *
     * @return
     */
    public static List<QueryCase> loadAggregationsQueries() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query28", "Aggregations", readFile(AGGREGATIONS + "/Query28.spl")));
        queryCases.add(new QueryCase("Query29", "Aggregations", readFile(AGGREGATIONS + "/Query29.spl")));
        return queryCases;
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

    public static List<QueryPair> readQueryPairs(String filepath) {

        List<QueryPair> queryPairs = new ArrayList<>();
        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            while (buffer.ready()) {
                String line = buffer.readLine();
                String[] parts = line.split("/t");
                QueryPair queryPair = new QueryPair(parts[0], parts[1]);
                queryPairs.add(queryPair);
            }

        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }

        return queryPairs;
    }

    public static List<QueryCase> loadMapSearchAndBrowsingQueries(int index) {

        String name = GEONAMES.get(index).getLabel();
        String box = GEONAMES.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("MS0", "MapSearchAndBrowsing", readFile(MAP_SEARCH_AND_BROWSING + "/Query0.spl").replace("TOPONYME", name)));
        queryCases.add(new QueryCase("MS1", "MapSearchAndBrowsing", readFile(MAP_SEARCH_AND_BROWSING + "/Query1.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));
        queryCases.add(new QueryCase("MS2", "MapSearchAndBrowsing", readFile(MAP_SEARCH_AND_BROWSING + "/Query2.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));

        return queryCases;
    }

    public static List<QueryCase> loadRapidMappingQueries(int index) {

        String timestamp = TIMESTAMPS.get(index).getLabel();
        String polygon = TIMESTAMPS.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RM0", "RapidMapping", readFile(RAPID_MAPPING + "/Query0.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM1", "RapidMapping", readFile(RAPID_MAPPING + "/Query1.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM2", "RapidMapping", readFile(RAPID_MAPPING + "/Query2.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM3", "RapidMapping", readFile(RAPID_MAPPING + "/Query3.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM4", "RapidMapping", readFile(RAPID_MAPPING + "/Query4.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM5", "RapidMapping", readFile(RAPID_MAPPING + "/Query5.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));

        return queryCases;
    }

    public static List<QueryCase> loadReverseGeocodingQueries(int index) {

        String point = POINTS.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RG0", "ReverseGeocoding", readFile(REVERSE_GEOCODING + "/Query0.spl").replace("GIVEN_POINT_IN_WKT", point)));
        queryCases.add(new QueryCase("RG1", "ReverseGeocoding", readFile(REVERSE_GEOCODING + "/Query1.spl").replace("GIVEN_POINT_IN_WKT", point)));

        return queryCases;
    }

    public static int getMapSearchAndBrowsingIndexSize() {
        return GEONAMES.size();
    }

    public static int getRapidMappingIndexSize() {
        return TIMESTAMPS.size();
    }

    public static int getReverseGeocodingIndexSize() {
        return POINTS.size();
    }

}
