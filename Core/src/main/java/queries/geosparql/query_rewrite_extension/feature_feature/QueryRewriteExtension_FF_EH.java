/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.query_rewrite_extension.feature_feature;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_FF_EH {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/feature_feature/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FF_EH1", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH2", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH3", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH4", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH5", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH6", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH7", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("QRE_FF_EH8", "QueryRewriteExtension_FF_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
