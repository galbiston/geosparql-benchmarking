/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.query_rewrite_extension.feature_geometry;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_FG_SimpleFeatures {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/feature_geometry/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FG_SF1", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF1-sfContains.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF2", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF2-sfCrosses.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF3", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF3-sfDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF4", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF4-sfEquals.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF5", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF5-sfIntersects.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF6", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF6-sfOverlaps.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF7", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF7-sfTouches.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF8", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FG_SF8-sfWithin.spl")));
        return queryCases;
    }

}
