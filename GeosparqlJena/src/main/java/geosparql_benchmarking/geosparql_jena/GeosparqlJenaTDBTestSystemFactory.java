/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.DatasetLoadResult;
import geosparql_benchmarking.experiments.DatasetLoadTimeResult;
import geosparql_benchmarking.experiments.TestSystem;
import geosparql_benchmarking.experiments.TestSystemFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.tdb2.solver.stats.Stats;
import org.apache.jena.tdb2.solver.stats.StatsResults;
import org.apache.jena.tdb2.store.DatasetGraphTDB;
import org.apache.jena.tdb2.sys.TDBInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tdb2.tdbstats;

/**
 *
 * @author Gerg
 */
public class GeosparqlJenaTDBTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "GeoSparqlJenaTDB";

    private final File datasetFolder;
    private final File resultsFolder;
    private final Boolean inferenceEnabled;

    public GeosparqlJenaTDBTestSystemFactory(File datasetFolder, String resultsFolder, Boolean inferenceEnabled) {
        this.datasetFolder = datasetFolder;
        this.resultsFolder = new File(BenchmarkExecution.RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
        this.inferenceEnabled = inferenceEnabled;
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJenaTestSystem(datasetFolder);
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
    public DatasetLoadResult loadDataset(HashMap<String, File> datasetMap, Integer iteration) {
        return loadDataset(datasetFolder, datasetMap, inferenceEnabled, iteration);
    }

    public static Boolean clearDataset(File datasetFolder) {
        try {
            FileUtils.deleteDirectory(datasetFolder);
            return true;
        } catch (IOException ex) {
            LOGGER.error("TDB Folder deletion: {} - {}", datasetFolder.getAbsolutePath(), ex.getMessage());
            return false;
        }
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
    public static DatasetLoadResult loadDataset(File datasetFolder, HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
        return loadDataset(datasetFolder, datasetMap, inferenceEnabled, 0);
    }

    private static DatasetLoadResult loadDataset(File datasetFolder, HashMap<String, File> datasetMap, Boolean inferenceEnabled, Integer iteration) {
        LOGGER.info("Geosparql Jena Loading: Started");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();

        Dataset dataset = TDB2Factory.connectDataset(datasetFolder.getAbsolutePath());
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
                    dataset.addNamedModel(graphName, infModel);
                } else {
                    dataset.addNamedModel(graphName, dataModel);
                }
                dataset.commit();

                long datasetEndNanoTime = System.nanoTime();
                DatasetLoadTimeResult datasetLoadTimeResult = new DatasetLoadTimeResult(graphName, datasetStartNanoTime, datasetEndNanoTime);
                datasetLoadTimeResults.add(datasetLoadTimeResult);
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graphName);
            } catch (RuntimeException ex) {
                isCompleted = false;
                LOGGER.error("TDB Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }

        dataset.close();

        optimiseTDB(datasetFolder);
        long endNanoTime = System.nanoTime();
        LOGGER.info("Geosparql Jena Loading: Completed");
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, iteration, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }

    public static void optimiseTDB(File datasetFolder) {
        //TDB Optimisation file generation based on:
        //https://jena.apache.org/documentation/tdb/optimizer.html#generating-statistics-for-union-graphs
        //https://github.com/apache/jena/blob/master/jena-cmds/src/main/java/tdb2/tdbstats.java
        //Throws Exception that datasetGraphTDB is closed.

        Dataset dataset = TDB2Factory.connectDataset(datasetFolder.getAbsolutePath());

        DatasetGraph datasetGraph = dataset.asDatasetGraph();
        File statsFile = new File(datasetFolder, "stats.opt");
        try (FileOutputStream outputStream = new FileOutputStream(statsFile)) {
            DatasetGraphTDB datasetGraphTDB = TDBInternal.getDatasetGraphTDB(datasetGraph);

            Node unionGraph = NodeFactory.createURI("urn:x-arq:UnionGraph");
            tdbstats.init();
            StatsResults statsResults = tdbstats.stats(datasetGraphTDB, unionGraph);
            Stats.write(outputStream, statsResults);
        } catch (IOException ex) {
            LOGGER.error("Optimise TDB Error: {} - {}", statsFile, ex.getMessage());
        } finally {
            dataset.close();
        }
    }
}
