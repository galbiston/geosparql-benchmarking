/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geographica;

import execution.QueryCase;
import execution.QueryLoader;
import execution.QueryPair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class MacroBenchmark {

    public static final String BASE_FOLDER = "../Core/geographica_benchmarking/macro_benchmark_queries";

    //Query Folders
    private static final String REVERSE_GEOCODING = BASE_FOLDER + "/reverse_geocoding";
    private static final String RAPID_MAPPING = BASE_FOLDER + "/rapid_mapping";
    private static final String MAP_SEARCH_AND_BROWSING = BASE_FOLDER + "/map_search_and_browsing";

    //Query Resources
    public static final List<QueryPair> TIMESTAMPS = QueryLoader.readQueryPairs(BASE_FOLDER + "/timestamps.txt");
    public static final List<QueryPair> POINTS = QueryLoader.readQueryPairs(BASE_FOLDER + "/points.txt");
    public static final List<QueryPair> GEONAMES = QueryLoader.readQueryPairs(BASE_FOLDER + "/geonames.txt");

    public static int getMapSearchAndBrowsingIndexSize() {
        return GEONAMES.size();
    }

    public static List<QueryCase> loadMapSearchAndBrowsingQueries(int index) {
        String name = GEONAMES.get(index).getLabel();
        String box = GEONAMES.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("MS0", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query0.spl").replace("TOPONYME", name)));
        queryCases.add(new QueryCase("MS1", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query1.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));
        queryCases.add(new QueryCase("MS2", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query2.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));
        return queryCases;
    }

    public static int getRapidMappingIndexSize() {
        return TIMESTAMPS.size();
    }

    public static List<QueryCase> loadRapidMappingQueries(int index) {
        String timestamp = TIMESTAMPS.get(index).getLabel();
        String polygon = TIMESTAMPS.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RM0", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query0.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM1", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query1.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM2", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query2.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM3", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query3.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM4", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query4.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM5", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query5.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        return queryCases;
    }

    public static int getReverseGeocodingIndexSize() {
        return POINTS.size();
    }

    public static List<QueryCase> loadReverseGeocodingQueries(int index) {
        String point = POINTS.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RG0", "ReverseGeocoding", QueryLoader.readFile(REVERSE_GEOCODING + "/Query0.spl").replace("GIVEN_POINT_IN_WKT", point)));
        queryCases.add(new QueryCase("RG1", "ReverseGeocoding", QueryLoader.readFile(REVERSE_GEOCODING + "/Query1.spl").replace("GIVEN_POINT_IN_WKT", point)));
        return queryCases;
    }

}
