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
package io.github.galbiston.geosparql_benchmarking.queries.geosparql.geometry_extension;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.geosparql_benchmarking.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class GeometryExtensionProperties {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_extension_properties";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GEP1", "GeometryExtensionProperties", QueryLoader.readFile(BASE_FOLDER + "/GEP1-CoordinateDimension.spl")));
        queryCases.add(new QueryCase("GEP2", "GeometryExtensionProperties", QueryLoader.readFile(BASE_FOLDER + "/GEP2-Dimension.spl")));
        queryCases.add(new QueryCase("GEP3", "GeometryExtensionProperties", QueryLoader.readFile(BASE_FOLDER + "/GEP3-IsEmpty.spl")));
        queryCases.add(new QueryCase("GEP4", "GeometryExtensionProperties", QueryLoader.readFile(BASE_FOLDER + "/GEP4-IsSimple.spl")));
        queryCases.add(new QueryCase("GEP5", "GeometryExtensionProperties", QueryLoader.readFile(BASE_FOLDER + "/GEP5-SpatialDimension.spl")));
        return queryCases;
    }

}
