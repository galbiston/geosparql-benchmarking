/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geosparql;

import execution.QueryCase;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import queries.geosparql.geometry_extension.GeometryExtension;
import queries.geosparql.geometry_extension.GeometryExtensionProperties;
import queries.geosparql.geometry_extension.GeometryExtensionQueryFunctions;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_Egenhofer;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_RCC8;
import queries.geosparql.geometry_topology_extension.GeometryTopologyExtension_SimpleFeatures;
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
import queries.geosparql.topology_vocabulary.TopologyVocabulary_Egenhofer;
import queries.geosparql.topology_vocabulary.TopologyVocabulary_RCC8;
import queries.geosparql.topology_vocabulary.TopologyVocabulary_SimpleFeatures;

/**
 *
 *
 */
public class GeosparqlBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final List<QueryCase> loadQueryCases(String arg) {
        switch (arg) {
            case "geosparql":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark.");
                return loadAll();
            case "geosparql_geometry":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Geometry Extension.");
                return loadGeometryExtension();
            case "geosparql_geometry_properties":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Geometry Extension Properties.");
                return loadGeometryExtensionProperties();
            case "geosparql_geometry_query_functions":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Geometry Extension Query Functions.");
                return loadGeometryExtensionQueryFunctions();
            case "geosparql_geometry_topology":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Geometry Topology Extension.");
                return loadGeometryTopologyExtension();
            case "geosparql_topology_vocabulary":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Topology Vocabulary.");
                return loadTopologyVocabulary();
            case "geosparql_query_rewrite":
                LOGGER.info("Query Set: GeoSPARQL Microbenchmark Query Rewrite Extension.");
                return loadQueryRewriteExtension();
            default:
                LOGGER.error("Query Set: unrecognised option - {}", arg);
                return null;
        }
    }

    public static List<QueryCase> loadAll() {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadGeometryExtension());
        queryCases.addAll(loadGeometryExtensionProperties());
        queryCases.addAll(loadGeometryExtensionQueryFunctions());
        queryCases.addAll(loadGeometryTopologyExtension());
        queryCases.addAll(loadTopologyVocabulary());
        queryCases.addAll(loadQueryRewriteExtension());
        return queryCases;
    }

    public static final List<QueryCase> loadGeometryExtension() {
        return GeometryExtension.loadQueries();
    }

    public static final List<QueryCase> loadGeometryExtensionProperties() {
        return GeometryExtensionProperties.loadQueries();
    }

    public static final List<QueryCase> loadGeometryExtensionQueryFunctions() {
        return GeometryExtensionQueryFunctions.loadQueries();
    }

    public static final List<QueryCase> loadGeometryTopologyExtension() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(GeometryTopologyExtension_Egenhofer.loadQueries());
        queryCases.addAll(GeometryTopologyExtension_RCC8.loadQueries());
        queryCases.addAll(GeometryTopologyExtension_SimpleFeatures.loadQueries());
        return queryCases;
    }

    public static final List<QueryCase> loadTopologyVocabulary() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(TopologyVocabulary_Egenhofer.loadQueries());
        queryCases.addAll(TopologyVocabulary_RCC8.loadQueries());
        queryCases.addAll(TopologyVocabulary_SimpleFeatures.loadQueries());
        return queryCases;
    }

    public static final List<QueryCase> loadQueryRewriteExtension() {
        List<QueryCase> queryCases = new ArrayList<>();
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
