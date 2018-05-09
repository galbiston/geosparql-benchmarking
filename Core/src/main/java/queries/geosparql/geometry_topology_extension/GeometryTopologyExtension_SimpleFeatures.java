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

/**
 *
 *
 */
public class GeometryTopologyExtension_SimpleFeatures {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/geometry_topology_extension/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_SF1", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfContains.spl")));
        queryCases.add(new QueryCase("GTE_SF2", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfCrosses.spl")));
        queryCases.add(new QueryCase("GTE_SF3", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfDisjoint.spl")));
        queryCases.add(new QueryCase("GTE_SF4", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfEquals.spl")));
        queryCases.add(new QueryCase("GTE_SF5", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfIntersects.spl")));
        queryCases.add(new QueryCase("GTE_SF6", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfOverlaps.spl")));
        queryCases.add(new QueryCase("GTE_SF7", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfTouches.spl")));
        queryCases.add(new QueryCase("GTE_SF8", "GeometryTopologyExtension_SF", QueryLoader.readFile(BASE_FOLDER + "/sfWithin.spl")));
        return queryCases;
    }

}
