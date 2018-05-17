/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql;

import execution.QueryCase;
import java.util.ArrayList;
import java.util.List;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_Egenhofer;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_RCC8;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_SimpleFeatures;
import queries.geosparql.geometry_topology_vocabulary.GeometryTopologyVocabulary_Egenhofer;
import queries.geosparql.geometry_topology_vocabulary.GeometryTopologyVocabulary_RCC8;
import queries.geosparql.geometry_topology_vocabulary.GeometryTopologyVocabulary_SimpleFeatures;
import queries.geosparql.query_rewrite_extension.feature_feature.QueryRewriteExtension_FF_EH;
import queries.geosparql.query_rewrite_extension.feature_feature.QueryRewriteExtension_FF_RCC8;
import queries.geosparql.query_rewrite_extension.feature_feature.QueryRewriteExtension_FF_SimpleFeatures;
import queries.geosparql.query_rewrite_extension.feature_geometry.QueryRewriteExtension_FG_EH;
import queries.geosparql.query_rewrite_extension.feature_geometry.QueryRewriteExtension_FG_RCC8;
import queries.geosparql.query_rewrite_extension.feature_geometry.QueryRewriteExtension_FG_SimpleFeatures;
import queries.geosparql.query_rewrite_extension.geometry_feature.QueryRewriteExtension_GF_EH;
import queries.geosparql.query_rewrite_extension.geometry_feature.QueryRewriteExtension_GF_RCC8;
import queries.geosparql.query_rewrite_extension.geometry_feature.QueryRewriteExtension_GF_SimpleFeatures;
import queries.geosparql.query_rewrite_extension.geometry_geometry.QueryRewriteExtension_GG_EH;
import queries.geosparql.query_rewrite_extension.geometry_geometry.QueryRewriteExtension_GG_RCC8;
import queries.geosparql.query_rewrite_extension.geometry_geometry.QueryRewriteExtension_GG_SimpleFeatures;

/**
 *
 *
 */
public class GeosparqlBenchmark {

    public static List<QueryCase> loadAll() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(GeometryExtension.loadQueries());
        queryCases.addAll(GeometryExtensionProperties.loadQueries());
        queryCases.addAll(GeometryExtensionQueryFunctions.loadQueries());
        queryCases.addAll(GeometryTopologyExtension_Egenhofer.loadQueries());
        queryCases.addAll(GeometryTopologyExtension_RCC8.loadQueries());
        queryCases.addAll(GeometryTopologyExtension_SimpleFeatures.loadQueries());
        queryCases.addAll(GeometryTopologyVocabulary_Egenhofer.loadQueries());
        queryCases.addAll(GeometryTopologyVocabulary_RCC8.loadQueries());
        queryCases.addAll(GeometryTopologyVocabulary_SimpleFeatures.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FF_EH.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FF_RCC8.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FF_SimpleFeatures.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FG_EH.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FG_RCC8.loadQueries());
        queryCases.addAll(QueryRewriteExtension_FG_SimpleFeatures.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GF_EH.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GF_RCC8.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GF_SimpleFeatures.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GG_EH.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GG_RCC8.loadQueries());
        queryCases.addAll(QueryRewriteExtension_GG_SimpleFeatures.loadQueries());
        return queryCases;
    }

}
