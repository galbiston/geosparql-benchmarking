package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.DatasetLoadResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");
    public static final String GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME = "geosparql_jena_tdb";
    public static final String GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME = "geosparql_jena_mem";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        HashMap<String, File> datasetMap = DatasetSources.getDatasets();
        Boolean inferenceEnabled = true;
        Boolean includeMemoryTestSystem = true;
        //GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        storeLoadDatasetResults(datasetMap, inferenceEnabled, includeMemoryTestSystem);
        //runJenaTDB(GEOSPARQL_JENA_TDB_FOLDER, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP);

        //runJenaMem(datasetMap, inferenceEnabled, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP);
    }

    public static void storeLoadDatasetResults(HashMap<String, File> datasetMap, Boolean inferenceEnabled, Boolean includeMemoryTestSystem) {
        try {
            FileUtils.deleteDirectory(GEOSPARQL_JENA_TDB_FOLDER);

            DatasetLoadResult tdbDatasetLoadResult = GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
            DatasetLoadResult.writeResultsFile(new File(BenchmarkExecution.RESULTS_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME), tdbDatasetLoadResult);

            if (includeMemoryTestSystem) {
                Dataset dataset = DatasetFactory.createTxnMem();
                DatasetLoadResult memDatasetLoadResult = GeosparqlJenaMemTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, dataset);

                DatasetLoadResult.writeResultsFile(new File(BenchmarkExecution.RESULTS_FOLDER, GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME), memDatasetLoadResult);
            }
        } catch (IOException ex) {
            LOGGER.error("TDB Folder deletion: {} - {}", GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath(), ex.getMessage());
        }
    }

    public static void runJenaTDB(File geosparqlJenaTDBFolder, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {
        BenchmarkExecution.runWarm(new GeosparqlJenaTDBTestSystemFactory(geosparqlJenaTDBFolder, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME), iterations, timeout, queryMap);
        BenchmarkExecution.runCold(new GeosparqlJenaTDBTestSystemFactory(geosparqlJenaTDBFolder, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME), iterations, timeout, queryMap);
    }

    public static void runJenaMem(HashMap<String, File> datasetMap, Boolean inferenceEnabled, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        Dataset dataset = DatasetFactory.createTxnMem();
        GeosparqlJenaMemTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, dataset);
        BenchmarkExecution.runWarm(new GeosparqlJenaMemTestSystemFactory(dataset, GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME), iterations, timeout, queryMap);
        BenchmarkExecution.runCold(new GeosparqlJenaMemTestSystemFactory(dataset, GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME), iterations, timeout, queryMap);
    }

    private static void rdfsGeosparqlJenaTest() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());

        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }

    }

    private static void exportGeosparqlJenaTest() {
        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        Model model = dataset.getNamedModel(GraphURI.LGD_URI);
        try (FileOutputStream out = new FileOutputStream(new File("lgd-jena.ttl"))) {
            RDFDataMgr.write(out, model, Lang.TTL);
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
    }

}
