/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.query_rewrite_extension.geometry_feature;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class QueryRewriteExtension_GF_EH {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/query_rewrite_extension/geometry_feature/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GF_EH1", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH2", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH3", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH4", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH5", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH6", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH7", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("QRE_GF_EH8", "QueryRewriteExtension_GF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
