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
package io.github.galbiston.geosparql_benchmarking.execution.cli;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.io.File;
import java.util.List;
import io.github.galbiston.geosparql_benchmarking.queries.geographica.MacroBenchmark;
import io.github.galbiston.geosparql_benchmarking.queries.geographica.MicroBenchmark;
import io.github.galbiston.geosparql_benchmarking.queries.geosparql.GeosparqlBenchmark;

/**
 *
 *
 */
public class QueryCaseLoader {

    public static final List<QueryCase> load(String queryCaseName, Integer iterations) {

        String arg = queryCaseName.toLowerCase();
        if (arg.startsWith("micro")) {
            return MicroBenchmark.loadQueryCases(arg);
        } else if (arg.startsWith("macro")) {
            return MacroBenchmark.loadQueryCases(arg, iterations);
        } else if (arg.startsWith("geosparql")) {
            return GeosparqlBenchmark.loadQueryCases(arg);
        } else {
            File fileArg = new File(queryCaseName);
            if (fileArg.exists()) {
                if (fileArg.isDirectory()) {
                    return QueryLoader.readFolder(fileArg);
                } else {
                    return QueryLoader.readQuery(fileArg.getAbsolutePath());
                }
            } else {
                throw new IllegalArgumentException("Unknown Query Case Set: " + queryCaseName + ". Expected to start with 'micro', 'macro', 'geosparql' or a file/folder path to load.");
            }
        }

    }
}
