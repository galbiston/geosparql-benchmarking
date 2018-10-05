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
public class GeometryTopologyExtension_SimpleFeatures {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_topology_extension/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_SF1", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF1-sfContains.spl")));
        queryCases.add(new QueryCase("GTE_SF2", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF2-sfCrosses.spl")));
        queryCases.add(new QueryCase("GTE_SF3", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF3-sfDisjoint.spl")));
        queryCases.add(new QueryCase("GTE_SF4", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF4-sfEquals.spl")));
        queryCases.add(new QueryCase("GTE_SF5", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF5-sfIntersects.spl")));
        queryCases.add(new QueryCase("GTE_SF6", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF6-sfOverlaps.spl")));
        queryCases.add(new QueryCase("GTE_SF7", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF7-sfTouches.spl")));
        queryCases.add(new QueryCase("GTE_SF8", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/GTE_SF8-sfWithin.spl")));
        return queryCases;
    }

}
