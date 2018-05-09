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
public class GeometryTopologyExtension_Egenhofer {

    public static final String BASE_FOLDER = "../Core/geosparql_benchmarking/geometry_topology_extension/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_EH1", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehContains.spl")));
        queryCases.add(new QueryCase("GTE_EH2", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCoveredBy.spl")));
        queryCases.add(new QueryCase("GTE_EH3", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehCovers.spl")));
        queryCases.add(new QueryCase("GTE_EH4", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehDisjoint.spl")));
        queryCases.add(new QueryCase("GTE_EH5", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehEquals.spl")));
        queryCases.add(new QueryCase("GTE_EH6", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehInside.spl")));
        queryCases.add(new QueryCase("GTE_EH7", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehMeet.spl")));
        queryCases.add(new QueryCase("GTE_EH8", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/ehOverlap.spl")));
        return queryCases;
    }

}
