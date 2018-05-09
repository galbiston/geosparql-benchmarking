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
public class QueryRewriteExtension_GF_RCC8 {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/query_rewrite_extension/geometry_feature/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GF_RCC1", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8dc.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC2", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8ec.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC3", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8eq.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC4", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8nttp.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC5", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8nttpi.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC6", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8po.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC7", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8tpp.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC8", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/rcc8tppi.spl")));
        return queryCases;
    }

}
