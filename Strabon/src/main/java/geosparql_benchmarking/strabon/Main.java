package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final Boolean DEBUG_MESSAGES = true;

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

        String postgresBinPath = "\"C:\\Program Files\\PostgreSQL\\10\\bin\\";
        String postgresDataPath = "\"C:\\Program Files\\PostgreSQL\\10\\data\\\"";

        StrabonTestSystemFactory testSystemFactory = new StrabonTestSystemFactory(db, user, passwd, port, host, resultsFolder, postgresBinPath, postgresDataPath);

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
        //HashMap<String, String> queryMap = QueryLoader.loadNonTopologicalFunctionsQueries();
        //HashMap<String, String> queryMap = QueryLoader.loadMainQuerySet();
        //BenchmarkExecution.run(testSystemFactory, iterations, timeout, queryMap);
        rdfsStrabonTest(testSystemFactory);

    }

    private static void rdfsStrabonTest(StrabonTestSystemFactory testSystemFactory) {

        //String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://strdf.di.uoa.gr/ontology#WKT>";
        String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try (StrabonTestSystem strabonTestSystem = testSystemFactory.getStrabonTestSystem()) {
            Strabon strabon = strabonTestSystem.getStrabon();
            TupleQuery tupleQuery = (TupleQuery) strabon.query(queryString, Format.TUQU, strabon.getSailRepoConnection(), System.out);
            SPARQLResultsCSVWriter csvWriter = new SPARQLResultsCSVWriter(System.out);
            tupleQuery.evaluate(csvWriter);

            /*
            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();

            while (tupleQueryResult.hasNext()) {

                BindingSet bindingSet = tupleQueryResult.next();
                List<String> bindingNames = tupleQueryResult.getBindingNames();
                StringBuilder sb = new StringBuilder();
                for (String binding : bindingNames) {
                    Value value = bindingSet.getValue(binding);
                    String valueStr = value.stringValue();
                    sb.append(value).append("-").append(valueStr).append(". ");
                }
            }
            tupleQueryResult.close();
             */
        } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

}
