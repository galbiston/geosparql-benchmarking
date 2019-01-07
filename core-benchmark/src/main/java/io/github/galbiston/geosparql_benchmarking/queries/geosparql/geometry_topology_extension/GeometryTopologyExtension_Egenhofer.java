/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.galbiston.geosparql_benchmarking.queries.geosparql.geometry_topology_extension;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.geosparql_benchmarking.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class GeometryTopologyExtension_Egenhofer {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_topology_extension/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_EH1", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("GTE_EH2", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("GTE_EH3", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("GTE_EH4", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("GTE_EH5", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("GTE_EH6", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("GTE_EH7", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("GTE_EH8", "GeometryTopologyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/GTE_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
