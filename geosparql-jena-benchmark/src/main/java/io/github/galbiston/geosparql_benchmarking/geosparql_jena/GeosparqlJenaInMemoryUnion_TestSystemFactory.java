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
package io.github.galbiston.geosparql_benchmarking.geosparql_jena;

import io.github.galbiston.geosparql_benchmarking.execution.TestSystem;
import io.github.galbiston.geosparql_jena.implementation.index.IndexConfiguration.IndexOption;
import java.lang.invoke.MethodHandles;
import org.apache.jena.query.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class GeosparqlJenaInMemoryUnion_TestSystemFactory extends GeosparqlJenaInMemory_TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "GeoSparqlJenaInMemory_Union";

    public GeosparqlJenaInMemoryUnion_TestSystemFactory(Dataset dataset, String resultsFolder, Boolean inferenceEnabled) {
        super(dataset, resultsFolder, inferenceEnabled);
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJena_TestSystem(dataset, IndexOption.MEMORY, true);
    }

    @Override
    public String getTestSystemName() {
        return TEST_SYSTEM_NAME;
    }

}
