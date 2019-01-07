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
package io.github.galbiston.geosparql_benchmarking.queries.geosparql.topology_vocabulary;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import java.util.ArrayList;
import java.util.List;
import static io.github.galbiston.geosparql_benchmarking.queries.geosparql.GeosparqlBenchmark.CONFORMANCE_FOLDER;

/**
 *
 *
 */
public class TopologyVocabulary_Egenhofer {

    public static final String BASE_FOLDER = CONFORMANCE_FOLDER + "/topology_vocabulary_extension/egenhofer";

    public static List<QueryCase> loadQueries() {
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("TVE_EH1", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH1-ehContains.spl")));
        queryCases.add(new QueryCase("TVE_EH2", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH2-ehCoveredBy.spl")));
        queryCases.add(new QueryCase("TVE_EH3", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH3-ehCovers.spl")));
        queryCases.add(new QueryCase("TVE_EH4", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH4-ehDisjoint.spl")));
        queryCases.add(new QueryCase("TVE_EH5", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH5-ehEquals.spl")));
        queryCases.add(new QueryCase("TVE_EH6", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH6-ehInside.spl")));
        queryCases.add(new QueryCase("TVE_EH7", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH7-ehMeet.spl")));
        queryCases.add(new QueryCase("TVE_EH8", "TopologyVocabularyExtension_EH", QueryLoader.readFile(BASE_FOLDER + "/TVE_EH8-ehOverlap.spl")));
        return queryCases;
    }

}
