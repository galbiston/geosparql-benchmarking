/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.query_rewrite_extension.geometry_geometry;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class QueryRewriteExtension_GG_EH {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/query_rewrite_extension/geometry_geometry/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GG_EH1", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehContains.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH2", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCoveredBy.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH3", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCovers.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH4", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH5", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehEquals.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH6", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehInside.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH7", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehMeet.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH8", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/ehOverlap.spl")));
        return queryCases;
    }

}
