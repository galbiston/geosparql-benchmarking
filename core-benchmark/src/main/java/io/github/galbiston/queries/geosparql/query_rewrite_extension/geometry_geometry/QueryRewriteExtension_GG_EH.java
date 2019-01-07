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
package io.github.galbiston.queries.geosparql.query_rewrite_extension.geometry_geometry;

import io.github.galbiston.execution.QueryCase;
import io.github.galbiston.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_GG_EH {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/geometry_geometry/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GG_EH1", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH2", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH3", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH4", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH5", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH6", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH7", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("QRE_GG_EH8", "QueryRewriteExtension_GG_EH", QueryLoader.readFile(BASE_FOLDER + "/QRE_GG_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
