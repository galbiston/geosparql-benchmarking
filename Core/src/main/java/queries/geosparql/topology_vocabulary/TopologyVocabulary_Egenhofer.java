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
public class TopologyVocabulary_Egenhofer {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/topology_vocabulary_extension/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_EH1", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("TVE_EH2", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("TVE_EH3", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("TVE_EH4", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("TVE_EH5", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("TVE_EH6", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("TVE_EH7", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("TVE_EH8", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
