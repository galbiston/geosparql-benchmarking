/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GeometryExtensionQueryFunctions {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/geometry_extension_query_functions";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GEQ1", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/Boundary.spl")));
        queryCases.add(new QueryCase("GEQ2", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/BufferMetres.spl")));
        queryCases.add(new QueryCase("GEQ3", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/BufferRadians.spl")));
        queryCases.add(new QueryCase("GEQ4", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/ConvexHull.spl")));
        queryCases.add(new QueryCase("GEQ5", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/Difference.spl")));
        queryCases.add(new QueryCase("GEQ6", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/DistanceMetres.spl")));
        queryCases.add(new QueryCase("GEQ7", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/DistanceRadians.spl")));
        queryCases.add(new QueryCase("GEQ8", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/Envelope.spl")));
        queryCases.add(new QueryCase("GEQ9", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GetSRID.spl")));
        queryCases.add(new QueryCase("GEQ10", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/Intersection.spl")));
        queryCases.add(new QueryCase("GEQ11", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/SymmetricDifference.spl")));
        queryCases.add(new QueryCase("GEQ12", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/Union.spl")));
        return queryCases;
    }

}
