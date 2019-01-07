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
package io.github.galbiston.queries.geosparql.query_rewrite_extension.geometry_feature;

import io.github.galbiston.execution.QueryCase;
import io.github.galbiston.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_GF_RCC8 {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/geometry_feature/rcc8";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_GF_RCC1", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC1-rcc8dc.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC2", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC2-rcc8ec.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC3", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC3-rcc8eq.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC4", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC4-rcc8nttp.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC5", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC5-rcc8nttpi.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC6", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC6-rcc8po.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC7", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC7-rcc8tpp.spl")));
        queryCases.add(new QueryCase("QRE_GF_RCC8", "QueryRewriteExtension_GF_RCC8", QueryLoader.readFile(BASE_FOLDER + "/QRE_GF_RCC8-rcc8tppi.spl")));
        return queryCases;
    }

}
