/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import execution.BenchmarkExecution;
import execution.TestSystem;
import execution.TestSystemFactory;
import execution_results.DatasetLoadResult;
import execution_results.DatasetLoadTimeResult;
import implementation.index.IndexOption;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class GeosparqlJenaInMemoryTestSystemFactory implements TestSystemFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "GeoSparqlJenaInMemory";

    protected Dataset dataset;
    protected final File resultsFolder;
    protected final Boolean inferenceEnabled;

    public GeosparqlJenaInMemoryTestSystemFactory(Dataset dataset, String resultsFolder, Boolean inferenceEnabled) {
        this.dataset = dataset;
        this.resultsFolder = new File(BenchmarkExecution.RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
        this.inferenceEnabled = inferenceEnabled;
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJenaTestSystem(dataset, IndexOption.MEMORY);
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
        dataset = DatasetFactory.createTxnMem();
        return true;
    }

    @Override
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration) {
        return loadDataset(datasetMap, inferenceEnabled, dataset, iteration);
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

    public static DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Boolean inferenceEnabled, Dataset dataset) {
        return loadDataset(datasetMap, inferenceEnabled, dataset, 0);
    }

    protected static DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Boolean inferenceEnabled, Dataset dataset, Integer iteration) {
        LOGGER.info("Geosparql Jena Memory Loading: Started");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();

        if (dataset == null) {
            dataset = DatasetFactory.createTxnMem();
        }
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
                LOGGER.error("Memory Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }
        long endNanoTime = System.nanoTime();
        LOGGER.info("Geosparql Jena Memory Loading: Completed");
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, iteration, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }
}
