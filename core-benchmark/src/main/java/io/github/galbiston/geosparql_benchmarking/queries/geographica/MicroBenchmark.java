/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.galbiston.geosparql_benchmarking.queries.geographica;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class MicroBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String PUBLISHED_FOLDER = "../queries/geographica_benchmarking/micro_benchmark_queries";
    public static final String UNION_FOLDER = "../queries/geographica_benchmarking_union/micro_benchmark_queries";

    //Query Folders
    public static final String NON_TOPOLOGICAL_FUNCTIONS = "/non_topological_functions";
    public static final String SPATIAL_JOINS = "/spatial_joins";
    public static final String SPATIAL_SELECTIONS = "/spatial_selections";
    public static final String AGGREGATIONS = "/aggregations";

    public static final List<QueryCase> loadQueryCases(String arg) {
        switch (arg) {
            case "micro":
                LOGGER.info("Query Set: Geographica Microbenchmark.");
                return loadMainQuerySet(QueryFormat.PUBLISHED);
            case "micro_non_topological":
                LOGGER.info("Query Set: Geographica Microbenchmark Non-Topological.");
                return loadNonTopologicalFunctionsQueries(QueryFormat.PUBLISHED);
            case "micro_spatial_joins":
                LOGGER.info("Query Set: Geographica Microbenchmark Spatial Joins.");
                return loadSpatialJoinsQueries(QueryFormat.PUBLISHED);
            case "micro_spatial_selections":
                LOGGER.info("Query Set: Geographica Microbenchmark Spatial Selections.");
                return loadSpatialSelectionsQueries(QueryFormat.PUBLISHED);
            case "micro_union":
                LOGGER.info("Query Set: Geographica Microbenchmark.");
                return loadMainQuerySet(QueryFormat.UNION);
            case "micro_non_topological_union":
                LOGGER.info("Query Set: Geographica Microbenchmark Non-Topological.");
                return loadNonTopologicalFunctionsQueries(QueryFormat.UNION);
            case "micro_spatial_joins_union":
                LOGGER.info("Query Set: Geographica Microbenchmark Spatial Joins.");
                return loadSpatialJoinsQueries(QueryFormat.UNION);
            case "micro_spatial_selections_union":
                LOGGER.info("Query Set: Geographica Microbenchmark Spatial Selections.");
                return loadSpatialSelectionsQueries(QueryFormat.UNION);
            default:
                LOGGER.error("Query Set: unrecognised option - {}", arg);
                return null;
        }
    }

    public static final String selectQueryFolder(QueryFormat queryFormat, String subFolder) {
        switch (queryFormat) {
            case UNION:
                return UNION_FOLDER + subFolder;
            default:
                return PUBLISHED_FOLDER + subFolder;
        }
    }

    public static final HashMap<String, String> loadQueryResources(QueryFormat queryFormat) {
        String baseFolder;
        switch (queryFormat) {
            case UNION:
                baseFolder = UNION_FOLDER;
                break;
            default:
                baseFolder = PUBLISHED_FOLDER;
                break;
        }

        HashMap<String, String> queryResource = new HashMap<>();

        //Query Resources
        queryResource.put("GIVEN_POINT", QueryLoader.readFile(baseFolder + "/givenPoint.txt"));
        queryResource.put("GIVEN_LINESTRING_1", QueryLoader.readFile(baseFolder + "/givenLineString1.txt"));
        queryResource.put("GIVEN_LINESTRING_2", QueryLoader.readFile(baseFolder + "/givenLineString2.txt"));
        queryResource.put("GIVEN_LINESTRING_3", QueryLoader.readFile(baseFolder + "/givenLineString3.txt"));
        queryResource.put("GIVEN_RADIUS", QueryLoader.readFile(baseFolder + "/givenRadius.txt"));
        queryResource.put("GIVEN_POLYGON", QueryLoader.readFile(baseFolder + "/givenPolygon.txt"));

        return queryResource;
    }

    public static final List<QueryCase> loadNonTopologicalFunctionsQuery_4() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query4.spl")));
        return queryCases;
    }

    public static final List<QueryCase> loadNonTopologicalFunctionsQuery_3() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", QueryLoader.readFile(NON_TOPOLOGICAL_FUNCTIONS + "/Query3.spl")));
        return queryCases;
    }

    /**
     * These queries use Strabon only syntax "strdf:extent" and "strdf:union"
     * that is not specified in the GeoSPARQL standard.
     *
     * @param queryFormat
     * @return List of query cases.
     */
    public static final List<QueryCase> loadAggregationsQueries(QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, AGGREGATIONS);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query28", "Aggregations", QueryLoader.readFile(queryFolder + "/Query28.spl")));
        queryCases.add(new QueryCase("Query29", "Aggregations", QueryLoader.readFile(queryFolder + "/Query29.spl")));
        return queryCases;
    }

    /**
     * Load set of all queries except those with Strabon only syntax (Query 6,
     * 28 and 29).
     *
     * @param queryFormat
     * @return List of query cases.
     */
    public static final List<QueryCase> loadMainQuerySet(QueryFormat queryFormat) {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadNonTopologicalFunctionsQueries(queryFormat));
        queryCases.addAll(loadSpatialSelectionsQueries(queryFormat));
        queryCases.addAll(loadSpatialJoinsQueries(queryFormat));
        return queryCases;
    }

    /**
     * Query 6 uses Strabon only syntax "strdf:area" that is not specified in
     * the GeoSPARQL standard.
     *
     * @return List of query cases.
     */
    public static final List<QueryCase> loadNonTopologicalFunctionsQuery_6() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query6", "NonTopologicalFunctions", QueryLoader.readFile(PUBLISHED_FOLDER + NON_TOPOLOGICAL_FUNCTIONS + "/Query6.spl")));
        return queryCases;
    }

    /**
     * These queries utilise specific WKT shapes which are loaded from resource
     * files.
     *
     * @param queryFormat
     * @return List of query cases.
     */
    public static final List<QueryCase> loadSpatialSelectionsQueries(QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, SPATIAL_SELECTIONS);
        HashMap<String, String> queryResources = loadQueryResources(queryFormat);

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query7", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query7.spl").replace("GIVEN_LINESTRING_1_IN_WKT", queryResources.get("GIVEN_LINESTRING_1"))));
        queryCases.add(new QueryCase("Query8", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query9", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query10", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query10.spl").replace("GIVEN_LINESTRING_2_IN_WKT", queryResources.get("GIVEN_LINESTRING_2"))));
        queryCases.add(new QueryCase("Query11", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query12", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query12.spl").replace("GIVEN_LINESTRING_3_IN_WKT", queryResources.get("GIVEN_LINESTRING_3"))));
        queryCases.add(new QueryCase("Query13", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", queryResources.get("GIVEN_POINT")).replace("GIVEN_RADIUS", queryResources.get("GIVEN_RADIUS"))));
        queryCases.add(new QueryCase("Query15", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", queryResources.get("GIVEN_POINT")).replace("GIVEN_RADIUS", queryResources.get("GIVEN_RADIUS"))));
        queryCases.add(new QueryCase("Query16", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query17", "SpatialSelections", QueryLoader.readFile(queryFolder + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        return queryCases;
    }

    public static final List<QueryCase> loadSpatialSelectionsQuery_14() {
        HashMap<String, String> queryResources = loadQueryResources(QueryFormat.PUBLISHED);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(PUBLISHED_FOLDER + SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", queryResources.get("GIVEN_POINT")).replace("GIVEN_RADIUS", queryResources.get("GIVEN_RADIUS"))));
        return queryCases;
    }

    public static final List<QueryCase> loadSpatialJoinsQueries(QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, SPATIAL_JOINS);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query18", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query18.spl")));
        queryCases.add(new QueryCase("Query19", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query19.spl")));
        queryCases.add(new QueryCase("Query20", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query20.spl")));
        queryCases.add(new QueryCase("Query21", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query21.spl")));
        queryCases.add(new QueryCase("Query22", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query22.spl")));
        queryCases.add(new QueryCase("Query23", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query23.spl")));
        queryCases.add(new QueryCase("Query24", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query24.spl")));
        queryCases.add(new QueryCase("Query25", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query25.spl")));
        queryCases.add(new QueryCase("Query26", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query26.spl")));
        queryCases.add(new QueryCase("Query27", "SpatialJoins", QueryLoader.readFile(queryFolder + "/Query27.spl")));
        return queryCases;
    }

    /**
     * This set of queries does not include Query 6 as it uses Strabon only
     * syntax that is not specified in the GeoSPARQL standard.
     *
     * @param queryFormat
     * @return List of query cases.
     */
    public static final List<QueryCase> loadNonTopologicalFunctionsQueries(QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, NON_TOPOLOGICAL_FUNCTIONS);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query1", "NonTopologicalFunctions", QueryLoader.readFile(queryFolder + "/Query1.spl")));
        queryCases.add(new QueryCase("Query2", "NonTopologicalFunctions", QueryLoader.readFile(queryFolder + "/Query2.spl")));
        queryCases.add(new QueryCase("Query3", "NonTopologicalFunctions", QueryLoader.readFile(queryFolder + "/Query3.spl")));
        queryCases.add(new QueryCase("Query4", "NonTopologicalFunctions", QueryLoader.readFile(queryFolder + "/Query4.spl")));
        queryCases.add(new QueryCase("Query5", "NonTopologicalFunctions", QueryLoader.readFile(queryFolder + "/Query5.spl")));
        return queryCases;
    }

}
