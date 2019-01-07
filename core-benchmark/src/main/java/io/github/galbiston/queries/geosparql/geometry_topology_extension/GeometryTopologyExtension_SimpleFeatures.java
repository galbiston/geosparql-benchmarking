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
package io.github.galbiston.queries.geosparql.geometry_topology_extension;

import io.github.galbiston.execution.QueryCase;
import io.github.galbiston.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

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
