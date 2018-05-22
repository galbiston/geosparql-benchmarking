/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.geometry_extension;

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
        queryCases.add(new QueryCase("GEQ1", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ1-Boundary.spl")));
        queryCases.add(new QueryCase("GEQ2", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ2-BufferDegrees.spl")));
        queryCases.add(new QueryCase("GEQ3", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ3-BufferMetres.spl")));
        queryCases.add(new QueryCase("GEQ4", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ4-ConvexHull.spl")));
        queryCases.add(new QueryCase("GEQ5", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ5-Difference.spl")));
        queryCases.add(new QueryCase("GEQ6", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ6-DistanceDegrees.spl")));
        queryCases.add(new QueryCase("GEQ7", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ7-DistanceMetres.spl")));
        queryCases.add(new QueryCase("GEQ8", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ8-Envelope.spl")));
        queryCases.add(new QueryCase("GEQ9", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ9-GetSRID.spl")));
        queryCases.add(new QueryCase("GEQ10", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ10-Intersection.spl")));
        queryCases.add(new QueryCase("GEQ11", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ11-SymmetricDifference.spl")));
        queryCases.add(new QueryCase("GEQ12", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ12-Union.spl")));
        return queryCases;
    }

}
