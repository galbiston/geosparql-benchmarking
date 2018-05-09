/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql.query_rewrite_extension.feature_feature;

import geosparql_benchmarking.geosparql.query_rewrite_extension.*;
import geosparql_benchmarking.experiments.QueryCase;
import geosparql_benchmarking.experiments.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class QueryRewriteExtension_FF_SimpleFeatures {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/query_rewrite_extension/feature_feature/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FF_SF1", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfContains.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF2", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfCrosses.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF3", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF4", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfEquals.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF5", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfIntersects.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF6", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfOverlaps.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF7", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfTouches.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF8", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/sfWithin.spl")));
        return queryCases;
    }

}
