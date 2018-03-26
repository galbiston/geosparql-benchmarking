package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.Main;
import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunGeosparqlJena extends RunSystemUnderTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunGeosparqlJena.class);

    @Override
    protected void initSystemUnderTest() throws Exception {
        sut = new GeosparqlJenaSUT(Main.GEOSPARQL_JENA_TDB_ASSEMBLY_FILE);
    }

    public static void main(String[] args) throws Exception {
        RunSystemUnderTest runGeosparql = new RunGeosparqlJena();

        runGeosparql.run(args);
    }

    public static void loadGeosparqlJena(File datasetFolder, HashMap<String, File> datasetMap) {
        LOGGER.info("Geosparql Jena Loading: Started");
        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());

        for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
            try {
                dataset.begin(ReadWrite.WRITE);
                String sourceRDFFile = entry.getValue().getAbsolutePath();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graph);
                Model model = RDFDataMgr.loadModel(sourceRDFFile);
                dataset.addNamedModel(graph, model);
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
}
