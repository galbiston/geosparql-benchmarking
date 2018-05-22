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
public class GeometryExtension {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/geometry_extension";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GE1", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE1-CRSConversion_Geodetic.spl")));
        queryCases.add(new QueryCase("GE2", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE2-CRSConversion_Projection.spl")));
        queryCases.add(new QueryCase("GE3", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE3-GMLSupport.spl")));
        queryCases.add(new QueryCase("GE4", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE4-WKTSupport.spl")));
        return queryCases;
    }

}
