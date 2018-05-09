/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql.query_rewrite_extension.feature_geometry;

import geosparql_benchmarking.experiments.QueryCase;
import geosparql_benchmarking.experiments.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class QueryRewriteExtension_FG_RCC8 {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/query_rewrite_extension/feature_geometry/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FG_RCC1", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8dc.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC2", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8ec.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC3", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8eq.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC4", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8nttp.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC5", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8nttpi.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC6", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8po.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC7", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8tpp.spl")));
        queryCases.add(new QueryCase("QRE_FG_RCC8", "QueryRewriteExtension_FG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8tppi.spl")));
        return queryCases;
    }

}
