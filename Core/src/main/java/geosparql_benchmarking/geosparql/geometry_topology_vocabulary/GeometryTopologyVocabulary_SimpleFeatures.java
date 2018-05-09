/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql.geometry_topology_vocabulary;

import geosparql_benchmarking.experiments.QueryCase;
import geosparql_benchmarking.experiments.QueryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GeometryTopologyVocabulary_SimpleFeatures {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/topology_vocabulary_extension/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_SF1", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfContains.spl")));
        queryCases.add(new QueryCase("TVE_SF2", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfCrosses.spl")));
        queryCases.add(new QueryCase("TVE_SF3", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfDisjoint.spl")));
        queryCases.add(new QueryCase("TVE_SF4", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfEquals.spl")));
        queryCases.add(new QueryCase("TVE_SF5", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfIntersects.spl")));
        queryCases.add(new QueryCase("TVE_SF6", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfOverlaps.spl")));
        queryCases.add(new QueryCase("TVE_SF7", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfTouches.spl")));
        queryCases.add(new QueryCase("TVE_SF8", "TopologyVocabularyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfWithin.spl")));
        return queryCases;
    }

}
