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
package queries.geosparql.geometry_extension;

import execution.QueryCase;
import execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class GeometryExtensionQueryFunctions {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_extension_query_functions";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GEQ1", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ1-Boundary.spl")));
        queryCases.add(new QueryCase("GEQ2", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ2-BufferDegrees.spl")));
        queryCases.add(new QueryCase("GEQ3", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ3-BufferMetres.spl")));
        queryCases.add(new QueryCase("GEQ4", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ4-ConvexHull.spl")));
        queryCases.add(new QueryCase("GEQ5", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ5-Difference.spl")));
        queryCases.add(new QueryCase("GEQ6", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ6-DistanceDegrees.spl")));
        queryCases.add(new QueryCase("GEQ7", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ7-DistanceMetres.spl")));
        queryCases.add(new QueryCase("GEQ8", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ8-Envelope.spl")));
        queryCases.add(new QueryCase("GEQ9", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ9-GetSRID.spl")));
        queryCases.add(new QueryCase("GEQ10", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ10-Intersection.spl")));
        queryCases.add(new QueryCase("GEQ11", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ11-SymmetricDifference.spl")));
        queryCases.add(new QueryCase("GEQ12", "GeometryExtensionQueryFunctions", QueryLoader.readFile(BASE_FOLDER + "/GEQ12-Union.spl")));
        return queryCases;
    }

}
