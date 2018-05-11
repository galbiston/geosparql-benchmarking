/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql.geometry_topology_vocabulary;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GeometryTopologyVocabulary_Egenhofer {

    public static final String BASE_FOLDER = "../queries/geosparql_benchmarking/topology_vocabulary_extension/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_EH1", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehContains.spl")));
        queryCases.add(new QueryCase("TVE_EH2", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCoveredBy.spl")));
        queryCases.add(new QueryCase("TVE_EH3", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCovers.spl")));
        queryCases.add(new QueryCase("TVE_EH4", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehDisjoint.spl")));
        queryCases.add(new QueryCase("TVE_EH5", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehEquals.spl")));
        queryCases.add(new QueryCase("TVE_EH6", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehInside.spl")));
        queryCases.add(new QueryCase("TVE_EH7", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehMeet.spl")));
        queryCases.add(new QueryCase("TVE_EH8", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehOverlap.spl")));
        return queryCases;
    }

}
