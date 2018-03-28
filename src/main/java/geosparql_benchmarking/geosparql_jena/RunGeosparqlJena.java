package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.Main;
import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunGeosparqlJena extends RunSystemUnderTest {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected void initSystemUnderTest() throws Exception {
        sut = new GeosparqlJenaSUT(Main.GEOSPARQL_JENA_TDB_ASSEMBLY_FILE);
    }

    public static void main(String[] args) throws Exception {
        RunSystemUnderTest runGeosparql = new RunGeosparqlJena();

        runGeosparql.run(args);
    }

    public static void loadDataset(File datasetFolder, HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
        LOGGER.info("Geosparql Jena Loading: Started");
        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        Model geosparqlSchema = RDFDataMgr.loadModel(RunGeosparqlJena.class.getClassLoader().getResource("geosparql_vocab_all.rdf").toString());

        for (Entry<String, File> entry : datasetMap.entrySet()) {
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
                LOGGER.error("TDB Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }
        dataset.close();
        TDBFactory.release(dataset);

        LOGGER.info("Geosparql Jena Loading: Completed");
    }

    public static void runBenchmark(File resultsFolder, Integer runtime, Integer timeout, List<String> queryList) {

        for (String query : queryList) {
            try {
                LOGGER.info("GeoSPARQL Jena Benchmark - {}: Started", query);
                String[] experimentArgs = {"--logpath", resultsFolder.getAbsolutePath(), "--runtime", runtime.toString(), "--timeout", timeout.toString(), "run", query};
                RunGeosparqlJena.main(experimentArgs);
                LOGGER.info("GeoSPARQL Jena Benchmark - {}: Completed", query);
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex);
            }
        }

    }
}
