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

import io.github.galbiston.geosparql_benchmarking.execution.BenchmarkExecution;
import io.github.galbiston.geosparql_benchmarking.execution.TestSystemFactory;
import io.github.galbiston.geosparql_benchmarking.geosparql_jena.cli.JenaExecutionParameters;
import java.io.File;
import java.lang.invoke.MethodHandles;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");
    public static final String GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME = "geosparql_jena_tdb";
    public static final String GEOSPARL_JENA_TDB_UNION_RESULTS_FOLDER_NAME = "geosparql_jena_tdb_union";
    public static final String GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME = "geosparql_jena_in_memory";
    public static final String GEOSPARL_JENA_IN_MEMORY_UNION_RESULTS_FOLDER_NAME = "geosparql_jena_in_memory_union";
    public static final String GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME = "geosparql_jena_no_index";
    public static final File GEOSPARQL_SCHEMA_FILE = new File("geosparql_vocab_all_v1_0_1_updated.rdf");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            JenaExecutionParameters parameters = JenaExecutionParameters.extract("GeoSPARQL-Jena", args);
            TestSystemFactory testSystemFactory = buildTestSystemFactory(parameters);
            BenchmarkExecution.runType(testSystemFactory, parameters);

        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }
    }

    public static TestSystemFactory buildTestSystemFactory(JenaExecutionParameters parameters) {
        SystemType systemType = parameters.getSystemType();
        TestSystemFactory testSystemFactory;
        switch (systemType) {
            case TDB:
                testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case TDB_UNION:
                testSystemFactory = new GeosparqlJenaTDBUnion_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_UNION_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case NO_INDEX:
                testSystemFactory = new GeosparqlJenaTDBNoIndex_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case MEMORY_UNION:
                Dataset memDataset = DatasetFactory.createTxnMem();
                GeosparqlJenaInMemoryUnion_TestSystemFactory.loadDataset(parameters.getDatasetMap(), parameters.getInferenceEnabled(), memDataset);
                testSystemFactory = new GeosparqlJenaInMemoryUnion_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_UNION_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            default:
                memDataset = DatasetFactory.createTxnMem();
                GeosparqlJenaInMemory_TestSystemFactory.loadDataset(parameters.getDatasetMap(), parameters.getInferenceEnabled(), memDataset);
                testSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
        }

        return testSystemFactory;
    }

}
