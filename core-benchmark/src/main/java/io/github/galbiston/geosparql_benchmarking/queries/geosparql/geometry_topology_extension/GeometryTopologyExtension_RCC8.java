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
public class GeometryTopologyExtension_RCC8 {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_topology_extension/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GTE_RCC1", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("GTE_RCC2", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("GTE_RCC3", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("GTE_RCC4", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("GTE_RCC5", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("GTE_RCC6", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("GTE_RCC7", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("GTE_RCC8", "GeometryTopologyExtension_RCC8", QueryLoader.readFile(BASE_FOLDER + "/GTE_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
