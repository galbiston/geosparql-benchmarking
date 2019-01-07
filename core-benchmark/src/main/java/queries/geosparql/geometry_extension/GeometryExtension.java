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
public class GeometryExtension {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/geometry_extension";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("GE1", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE1-CRSConversion_Geodetic.spl")));
        queryCases.add(new QueryCase("GE2", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE2-CRSConversion_Projection.spl")));
        queryCases.add(new QueryCase("GE3", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE3-GMLSupport.spl")));
        queryCases.add(new QueryCase("GE4", "GeometryExtension", QueryLoader.readFile(BASE_FOLDER + "/GE4-WKTSupport.spl")));
        return queryCases;
    }

}
