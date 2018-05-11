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

/**
 *
 *
 */
public class QueryRewriteExtension_FG_SimpleFeatures {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/query_rewrite_extension/feature_geometry/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FG_SF1", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfContains.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF2", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfCrosses.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF3", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF4", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfEquals.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF5", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfIntersects.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF6", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfOverlaps.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF7", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfTouches.spl")));
        queryCases.add(new QueryCase("QRE_FG_SF8", "QueryRewriteExtension_FG_SF", QueryLoader.readFile(BASE_FOLDER + "/sfWithin.spl")));
        return queryCases;
    }

}
