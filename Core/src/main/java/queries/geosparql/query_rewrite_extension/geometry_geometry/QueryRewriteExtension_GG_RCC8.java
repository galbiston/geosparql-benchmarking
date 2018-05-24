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
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_GG_RCC8 {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/geometry_geometry/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GG_RCC1", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC2", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC3", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC4", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC5", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC6", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC7", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("QRE_GG_RCC8", "QueryRewriteExtension_GG_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
