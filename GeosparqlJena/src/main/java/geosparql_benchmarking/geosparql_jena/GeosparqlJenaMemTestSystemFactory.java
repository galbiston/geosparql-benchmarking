/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.experiments.BenchmarkExecution;
import static geosparql_benchmarking.experiments.BenchmarkExecution.RESULTS_FOLDER;
import geosparql_benchmarking.experiments.TestSystem;
import geosparql_benchmarking.experiments.TestSystemFactory;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
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
public class GeosparqlJenaMemTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Dataset dataset;
    private final File resultsFolder;

    public GeosparqlJenaMemTestSystemFactory(Dataset dataset, String resultsFolder) {
        this.dataset = dataset;
        this.resultsFolder = new File(RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJenaTestSystem(dataset);
    }

    @Override
    public String getTestSystemName() {
        return "GeoSparqlJenaMem";
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    public static Dataset loadDataset(HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
        LOGGER.info("Geosparql Jena Memory Loading: Started");
        Dataset dataset = DatasetFactory.create();
        Model geosparqlSchema = RDFDataMgr.loadModel(BenchmarkExecution.class.getClassLoader().getResource("geosparql_vocab_all.rdf").toString());

        for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
            try {
                dataset.begin(ReadWrite.WRITE);
                String sourceRDFFile = entry.getValue().getAbsolutePath();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graph);
                Model dataModel = RDFDataMgr.loadModel(sourceRDFFile);
                if (inferenceEnabled) {
                    InfModel infModel = ModelFactory.createRDFSModel(geosparqlSchema, dataModel);
                    infModel.prepare();
                    dataset.addNamedModel(graph, infModel);
                } else {
                    dataset.addNamedModel(graph, dataModel);
                }
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graph);
                dataset.commit();

            } catch (RuntimeException ex) {
                LOGGER.error("Memory Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }

        LOGGER.info("Geosparql Jena Memory Loading: Completed");

        return dataset;
    }
}
