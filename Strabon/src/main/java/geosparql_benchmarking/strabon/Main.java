package geosparql_benchmarking.strabon;

import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.QueryLoader;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String db = "endpoint";
        String user = "postgres"; //String user = "postgres";
        String passwd = "postgres"; //String passwd = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"localhost"; //"127.0.0.1"
        String resultsFolder = "strabon";

        StrabonTestSystemFactory testSystemFactory = new StrabonTestSystemFactory(db, user, passwd, port, host, resultsFolder);

        HashMap<String, File> datasetMap = DatasetSources.getDatasets();
        Boolean inferenceEnabled = true;
        String baseURI = null;
        String format = "NTRIPLES";

        //StrabonTestSystemFactory.loadDataset(datasetMap, baseURI, format, inferenceEnabled, testSystemFactory);
        //Back up made: https://www.postgresql.org/docs/10/static/backup-dump.html
        //Benchmark
        Duration runtime = Duration.ofMinutes(30);
        Integer iterations = 1; //10;
        Duration timeout = Duration.ofSeconds(3600);

        //HashMap<String, String> queryMap = QueryLoader.loadSpatialSelectionsQuery_14();
        HashMap<String, String> queryMap = QueryLoader.loadNonTopologicalFunctionsQueries();
        //HashMap<String, String> queryMap = QueryLoader.loadMainQuerySet();
        BenchmarkExecution.run(testSystemFactory, iterations, timeout, queryMap);

    }

}
