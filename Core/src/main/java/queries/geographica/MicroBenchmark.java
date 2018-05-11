/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geographica;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class MicroBenchmark {

    public static final String BASE_FOLDER = "../queries/geographica_benchmarking/micro_benchmark_queries";

    //Query Folders
    public static final String NON_TOPOLOGICAL_FUNCTIONS = BASE_FOLDER + "/non_topological_functions";
    public static final String SPATIAL_JOINS = BASE_FOLDER + "/spatial_joins";
    public static final String SPATIAL_SELECTIONS = BASE_FOLDER + "/spatial_selections";
    public static final String AGGREGATIONS = BASE_FOLDER + "/aggregations";

    //Query Resources
    public static final String GIVEN_POINT = QueryLoader.readFile(BASE_FOLDER + "/givenPoint.txt");
    public static final String GIVEN_LINESTRING_1 = QueryLoader.readFile(BASE_FOLDER + "/givenLineString1.txt");
    public static final String GIVEN_LINESTRING_2 = QueryLoader.readFile(BASE_FOLDER + "/givenLineString2.txt");
    public static final String GIVEN_LINESTRING_3 = QueryLoader.readFile(BASE_FOLDER + "/givenLineString3.txt");
    public static final String GIVEN_RADIUS = QueryLoader.readFile(BASE_FOLDER + "/givenRadius.txt");
    public static final String GIVEN_POLYGON = QueryLoader.readFile(BASE_FOLDER + "/givenPolygon.txt");

    public static List<QueryCase> loadNonTopologicalFunctionsQuery_4() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl")));
        return queryCases;
    }

    public static List<QueryCase> loadNonTopologicalFunctionsQuery_3() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl")));
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
        queryCases.add(new QueryCase("Query28", "Aggregations", QueryLoader.readFile(AGGREGATIONS + "/Query28.spl")));
        queryCases.add(new QueryCase("Query29", "Aggregations", QueryLoader.readFile(AGGREGATIONS + "/Query29.spl")));
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
     * Query 6 uses Strabon only syntax "strdf:area" that is not specified in
     * the GeoSPARQL standard.
     *
     * @return
     */
    public static List<QueryCase> loadNonTopologicalFunctionsQuery_6() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query6", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query6.spl")));
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
        queryCases.add(new QueryCase("Query7", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_1)));
        queryCases.add(new QueryCase("Query8", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query9", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query10", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_2)));
        queryCases.add(new QueryCase("Query11", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query12", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_3)));
        queryCases.add(new QueryCase("Query13", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query15", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query16", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query17", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        return queryCases;
    }

    public static List<QueryCase> loadSpatialSelectionsQuery_14() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        return queryCases;
    }

    public static List<QueryCase> loadSpatialJoinsQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query18", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query18.spl")));
        queryCases.add(new QueryCase("Query19", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query19.spl")));
        queryCases.add(new QueryCase("Query20", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query20.spl")));
        queryCases.add(new QueryCase("Query21", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query21.spl")));
        queryCases.add(new QueryCase("Query22", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query22.spl")));
        queryCases.add(new QueryCase("Query23", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query23.spl")));
        queryCases.add(new QueryCase("Query24", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query24.spl")));
        queryCases.add(new QueryCase("Query25", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query25.spl")));
        queryCases.add(new QueryCase("Query26", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query26.spl")));
        queryCases.add(new QueryCase("Query27", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query27.spl")));
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
        queryCases.add(new QueryCase("Query1", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query1.spl")));
        queryCases.add(new QueryCase("Query2", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query2.spl")));
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl")));
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl")));
        queryCases.add(new QueryCase("Query5", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query5.spl")));
        return queryCases;
    }

}
