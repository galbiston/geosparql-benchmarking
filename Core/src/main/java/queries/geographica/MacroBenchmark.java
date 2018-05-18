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
import java.util.Random;

/**
 *
 *
 */
public class MacroBenchmark {

    public static final String BASE_FOLDER = "../queries/geographica_benchmarking/macro_benchmark_queries";
    private static final Random RANDOM = new Random();

    //Query Folders
    private static final String REVERSE_GEOCODING = BASE_FOLDER + "/reverse_geocoding";
    private static final String RAPID_MAPPING = BASE_FOLDER + "/rapid_mapping";
    private static final String MAP_SEARCH_AND_BROWSING = BASE_FOLDER + "/map_search_and_browsing";

    public static List<QueryCase> loadAll() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadMapSearchAndBrowsingQueries());
        queryCases.addAll(loadRapidMappingQueries());
        queryCases.addAll(loadReverseGeocodingQueries());
        return queryCases;
    }

    public static List<QueryCase> loadMapSearchAndBrowsingQueries() {
        List<QueryPair> geonames = QueryLoader.readQueryPairs(BASE_FOLDER + "/geonames.txt");
        int index = RANDOM.nextInt(geonames.size());
        String name = geonames.get(index).getLabel();
        String box = geonames.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("MS0", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query0.spl").replace("TOPONYME", name)));
        queryCases.add(new QueryCase("MS1", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query1.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));
        queryCases.add(new QueryCase("MS2", "MapSearchAndBrowsing", QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query2.spl").replace("GIVEN_RECTANGLE_IN_WKT", box)));
        return queryCases;
    }

    public static List<QueryCase> loadRapidMappingQueries() {
        List<QueryPair> timestamps = QueryLoader.readQueryPairs(BASE_FOLDER + "/timestamps.txt");
        int index = RANDOM.nextInt(timestamps.size());
        String timestamp = timestamps.get(index).getLabel();
        String polygon = timestamps.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RM0", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query0.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM1", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query1.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM2", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query2.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM3", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query3.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM4", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query4.spl").replace("TIMESTAMP", timestamp).replace("GIVEN_POLYGON_IN_WKT", polygon)));
        queryCases.add(new QueryCase("RM5", "RapidMapping", QueryLoader.readFile(RAPID_MAPPING + "/Query5.spl").replace("GIVEN_POLYGON_IN_WKT", polygon)));
        return queryCases;
    }

    public static List<QueryCase> loadReverseGeocodingQueries() {
        List<QueryPair> points = QueryLoader.readQueryPairs(BASE_FOLDER + "/points.txt");
        int index = RANDOM.nextInt(points.size());
        String point = points.get(index).getGeometry();
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("RG0", "ReverseGeocoding", QueryLoader.readFile(REVERSE_GEOCODING + "/Query0.spl").replace("GIVEN_POINT_IN_WKT", point)));
        queryCases.add(new QueryCase("RG1", "ReverseGeocoding", QueryLoader.readFile(REVERSE_GEOCODING + "/Query1.spl").replace("GIVEN_POINT_IN_WKT", point)));
        return queryCases;
    }

}
