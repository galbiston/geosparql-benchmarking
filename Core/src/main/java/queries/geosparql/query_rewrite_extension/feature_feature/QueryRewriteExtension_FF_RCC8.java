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

/**
 *
 *
 */
public class QueryRewriteExtension_FF_RCC8 {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/query_rewrite_extension/feature_feature/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FF_RCC1", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC2", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC3", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC4", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC5", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC6", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC7", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("QRE_FF_RCC8", "QueryRewriteExtension_FF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
