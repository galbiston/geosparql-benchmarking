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
package io.github.galbiston.geosparql_benchmarking.queries.geosparql.query_rewrite_extension.feature_feature;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.geosparql_benchmarking.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class QueryRewriteExtension_FF_SimpleFeatures {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/query_rewrite_extension/feature_feature/simple_features";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("QRE_FF_SF1", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF1-sfContains.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF2", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF2-sfCrosses.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF3", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF3-sfDisjoint.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF4", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF4-sfEquals.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF5", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF5-sfIntersects.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF6", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF6-sfOverlaps.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF7", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF7-sfTouches.spl")));
        queryCases.add(new QueryCase("QRE_FF_SF8", "QueryRewriteExtension_FF_SF", QueryLoader.readFile(BASE_FOLDER + "/QRE_FF_SF8-sfWithin.spl")));
        return queryCases;
    }

}
