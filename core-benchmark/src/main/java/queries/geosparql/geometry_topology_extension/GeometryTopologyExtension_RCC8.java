/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.geometry_topology_extension;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class GeometryTopologyExtension_RCC8 {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_topology_extension/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_RCC1", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("GTE_RCC2", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("GTE_RCC3", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("GTE_RCC4", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("GTE_RCC5", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("GTE_RCC6", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("GTE_RCC7", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("GTE_RCC8", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
