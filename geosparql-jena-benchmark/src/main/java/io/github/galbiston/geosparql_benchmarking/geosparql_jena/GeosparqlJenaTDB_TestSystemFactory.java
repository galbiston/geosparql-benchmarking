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
import io.github.galbiston.geosparql_benchmarking.execution.TestSystem;
import io.github.galbiston.geosparql_benchmarking.execution.TestSystemFactory;
import io.github.galbiston.geosparql_benchmarking.execution_results.DatasetLoadResult;
import io.github.galbiston.geosparql_benchmarking.execution_results.DatasetLoadTimeResult;
import io.github.galbiston.geosparql_jena.implementation.index.IndexConfiguration.IndexOption;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.solver.stats.Stats;
import org.apache.jena.tdb.solver.stats.StatsResults;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tdb.tdbstats;

/**
 *
 * @author Gerg
 */
public class GeosparqlJenaTDB_TestSystemFactory implements TestSystemFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "GeoSparqlJenaTDB";

    protected final File datasetFolder;
    protected final File resultsFolder;
    protected final Boolean inferenceEnabled;

    public GeosparqlJenaTDB_TestSystemFactory(File datasetFolder, String resultsFolder, Boolean inferenceEnabled) {
        this.datasetFolder = datasetFolder;
        this.resultsFolder = new File(BenchmarkExecution.RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
        this.inferenceEnabled = inferenceEnabled;
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJena_TestSystem(datasetFolder, IndexOption.MEMORY);
    }

    @Override
    public String getTestSystemName() {
        return TEST_SYSTEM_NAME;
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    @Override
    public Boolean clearDataset() {
        return clearDataset(datasetFolder);
    }

    @Override
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration) {
        return loadDataset(datasetFolder, datasetMap, inferenceEnabled, iteration);
    }

    @Override
    public Boolean clearLoadDataset(TreeMap<String, File> datasetMap) {

        Boolean isClear = clearDataset();
        if (isClear) {
            DatasetLoadResult loadResult = loadDataset(datasetMap, 0);
            return loadResult.getIsCompleted();
        }
        return isClear;
    }

    public static Boolean clearDataset(File datasetFolder) {

        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        Boolean isCleared;
        try {
            dataset.begin(ReadWrite.WRITE);
            Iterator<String> iterator = dataset.listNames();
            while (iterator.hasNext()) {
                String graphName = iterator.next();
                dataset.removeNamedModel(graphName);
            }
            Model defaultModel = dataset.getDefaultModel();
            defaultModel.removeAll();
            dataset.commit();
            isCleared = true;
        } catch (Exception ex) {
            LOGGER.error("TDB Folder clearance: {} - {}", datasetFolder.getAbsolutePath(), ex.getMessage());
            isCleared = false;
        } finally {
            dataset.end();
        }

        dataset.close();
        TDBFactory.release(dataset);
        return isCleared;
    }

    /**
     * Loads the dataset into the target location. No clearing of the dataset is
     * done before this and may be recommended.
     *
     * @param datasetFolder
     * @param datasetMap
     * @param inferenceEnabled
     * @return
     */
    public static DatasetLoadResult loadDataset(File datasetFolder, TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        return loadDataset(datasetFolder, datasetMap, inferenceEnabled, 0);
    }

    private static DatasetLoadResult loadDataset(File datasetFolder, TreeMap<String, File> datasetMap, Boolean inferenceEnabled, Integer iteration) {
        LOGGER.info("Geosparql Jena Loading: Started");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();

        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        Model geosparqlSchema = RDFDataMgr.loadModel(Main.GEOSPARQL_SCHEMA_FILE.getAbsolutePath());

        for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
            try {
                dataset.begin(ReadWrite.WRITE);
                String sourceRDFFile = entry.getValue().getAbsolutePath();
                String graphName = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graphName);
                long datasetStartNanoTime = System.nanoTime();
                Model dataModel = RDFDataMgr.loadModel(sourceRDFFile);
                if (inferenceEnabled) {
                    InfModel infModel = ModelFactory.createRDFSModel(geosparqlSchema, dataModel);
                    infModel.prepare();
                    if (graphName.isEmpty()) {
                        Model defaultModel = dataset.getDefaultModel();
                        defaultModel.add(infModel);
                    } else {
                        dataset.addNamedModel(graphName, infModel);
                    }
                } else {
                    if (graphName.isEmpty()) {
                        Model defaultModel = dataset.getDefaultModel();
                        defaultModel.add(dataModel);
                    } else {
                        dataset.addNamedModel(graphName, dataModel);
                    }
                }
                dataset.commit();

                long datasetEndNanoTime = System.nanoTime();
                DatasetLoadTimeResult datasetLoadTimeResult = new DatasetLoadTimeResult(graphName, datasetStartNanoTime, datasetEndNanoTime);
                datasetLoadTimeResults.add(datasetLoadTimeResult);
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graphName);
            } catch (Exception ex) {
                isCompleted = false;
                LOGGER.error("TDB Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }

        dataset.close();
        TDBFactory.release(dataset);

        optimiseTDB(datasetFolder);
        long endNanoTime = System.nanoTime();
        LOGGER.info("Geosparql Jena Loading: Completed");
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, iteration, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }

    public static void optimiseTDB(File datasetFolder) {
        //TDB Optimisation file generation based on:
        //https://jena.apache.org/documentation/tdb/optimizer.html#generating-statistics-for-union-graphs
        //https://github.com/apache/jena/blob/master/jena-cmds/src/main/java/tdb/tdbstats.java
        //Throws Exception that dataset is closed if run immediately after running the dataset.
        //Can run afterwards without any issue.

        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());

        dataset.begin(ReadWrite.READ);
        File statsFile = new File(datasetFolder, "stats.opt");
        try (FileOutputStream outputStream = new FileOutputStream(statsFile)) {
            DatasetGraphTDB datasetGraphTDB = TDBInternal.getDatasetGraphTDB(dataset);

            Node unionGraph = NodeFactory.createURI("urn:x-arq:UnionGraph");
            tdbstats.init();
            StatsResults statsResults = tdbstats.stats(datasetGraphTDB, unionGraph);
            Stats.write(outputStream, statsResults);
        } catch (IOException ex) {
            LOGGER.error("Optimise TDB Error: {} - {}", statsFile, ex.getMessage());
        } finally {
            dataset.end();
            dataset.close();
            TDBFactory.release(dataset);
        }
    }

    @Override
    public void loadDataset(String path) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
