/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.topology_vocabulary;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class TopologyVocabulary_RCC8 {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/topology_vocabulary_extension/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_RCC1", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("TVE_RCC2", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("TVE_RCC3", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("TVE_RCC4", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("TVE_RCC5", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("TVE_RCC6", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("TVE_RCC7", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("TVE_RCC8", "TopologyVocabularyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/TVE_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
