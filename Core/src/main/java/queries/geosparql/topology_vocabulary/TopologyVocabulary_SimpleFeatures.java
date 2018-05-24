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
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class TopologyVocabulary_SimpleFeatures {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/topology_vocabulary_extension/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_SF1", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF1-sfContains.spl")));
        queryCases.add(new QueryCase("TVE_SF2", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF2-sfCrosses.spl")));
        queryCases.add(new QueryCase("TVE_SF3", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF3-sfDisjoint.spl")));
        queryCases.add(new QueryCase("TVE_SF4", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF4-sfEquals.spl")));
        queryCases.add(new QueryCase("TVE_SF5", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF5-sfIntersects.spl")));
        queryCases.add(new QueryCase("TVE_SF6", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF6-sfOverlaps.spl")));
        queryCases.add(new QueryCase("TVE_SF7", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF7-sfTouches.spl")));
        queryCases.add(new QueryCase("TVE_SF8", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/TVE_SF8-sfWithin.spl")));
        return queryCases;
    }

}
