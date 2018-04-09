/**
 *
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import geosparql_benchmarking.experiments.QueryResult;
import geosparql_benchmarking.experiments.TestSystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

/**
 *
 */
public class StrabonTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Strabon strabon;

    String db;
    String user;
    String password;
    Integer port;
    String host;

    public StrabonTestSystem(String db, String user, String password, Integer port, String host) throws Exception {
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        this.host = host;

        restartPostgresService();

        strabon = new Strabon(db, user, password, port, host, true);
    }

    private static final String POSTGRESQL_SERVICE = "postgresql-10.3";

    //Restart service and clear caches where possible.
    //This has not been tested thoroughly and is based on the Geographica StrabonSUT but adapted for alternative Operating Systems.
    private void restartPostgresService() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            //Stop and start the service only.
            try {
                new ProcessExecutor().command("net", "stop", "postgresql")
                        .command("net", "start", POSTGRESQL_SERVICE)
                        .redirectOutput(Slf4jStream.ofCaller().asInfo())
                        .timeout(5, TimeUnit.SECONDS).execute();
            } catch (IOException | InterruptedException | TimeoutException ex) {
                LOGGER.error("Strabon Cache Clearing: {}", ex.getMessage());
            }
        } else if (osName.contains("nix") | osName.contains("nux") | osName.contains("aux") | osName.contains("mac")) {
            //Stop, drop caches and start the service. No documentation found to clear the OS caches.
            try {
                new ProcessExecutor().command("/bin/sh", "-c", "service postgresql stop")
                        .command("/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches")
                        .command("/bin/sh", "-c", "service postgresql start")
                        .redirectOutput(Slf4jStream.ofCaller().asInfo())
                        .timeout(5, TimeUnit.SECONDS).execute();
            } catch (IOException | InterruptedException | TimeoutException ex) {
                LOGGER.error("Strabon Cache Clearing: {}", ex.getMessage());
            }
        } else {
            LOGGER.error("Unrecognised OS name for Strabon Cache Clearing: {}", osName);
        }
    }

    @Override
    public QueryResult runQueryWithTimeout(String query, Duration timeout) {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        QueryTask runnable = new QueryTask(query, strabon);
        Future<?> future = executor.submit(runnable);

        try {
            LOGGER.debug("Strabon Future: Started");
            future.get(timeout.getSeconds(), TimeUnit.SECONDS);
            LOGGER.debug("Strabon Future: Completed");
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } finally {
            LOGGER.debug("Strabon: Executor Shutdown");
            executor.shutdown();
            System.gc();
        }

        QueryResult queryResult = runnable.getQueryResult();
        LOGGER.debug("Strabon Query: {}", queryResult);

        return queryResult;
    }

    @Override
    public QueryResult runUpdate(String query) throws MalformedQueryException {

        LOGGER.info("Strabon Update: Started");
        long startNanoTime = System.nanoTime();
        strabon.update(query, strabon.getSailRepoConnection());
        long resultsNanoTime = System.nanoTime();
        LOGGER.info("Strabon Update: Completed");

        return new QueryResult(startNanoTime, resultsNanoTime);
    }

    @Override
    public void close() {

        LOGGER.info("Closing..");
        try {
            strabon.close();
            strabon = null;
        } catch (Exception ex) {
            LOGGER.error("Exception closing Strabon: {}", ex.getMessage());
        }

        System.gc();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Cannot clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
        }
        LOGGER.info("Closed (caches not cleared)");
    }

    @Override
    public String translateQuery(String query) {
        String translatedQuery = query;

        //translatedQuery = translatedQuery.replaceAll("geof:distance", "strdf:distance");
        /*
        if (label.matches("Get_CLC_areas")
                || label.matches("Get_highways")
                || label.matches("Get_municipalities")
                || label.matches("Get_hotspots")
                || label.matches("Get_coniferous_forests_in_fire")
                || label.matches("Get_road_segments_affected_by_fire")) {
            translatedQuery = translatedQuery.replaceAll("<http://www.opengis.net/ont/geosparql#wktLiteral>", "strdf:WKT");
        }

        if (label.matches("List_GeoNames_categories_per_CLC_category")
                || label.matches("Count_GeoNames_categories_in_ContinuousUrbanFabric")) {
            translatedQuery = translatedQuery.replaceAll(
                    " } \\n	FILTER\\(geof:sfIntersects\\(\\?clcWkt, \\?fWkt\\)\\)\\. \\\n",
                    " \n	FILTER(geof:sfIntersects(?clcWkt, ?fWkt)). } \n");
        } else if (label.equals("Find_Closest_Populated_Place")
                || label.equals("Find_Closest_Motorway")) {
            translatedQuery = translatedQuery.replace("geof:distance", "strdf:distance");  //Moved from Parliament SUT and changed the Geographica query as this is a Strabon variation from GeoSPARQL standard.
        }
         */
        return translatedQuery;
    }

    private class QueryTask implements Runnable {

        private final String query;
        private final Strabon strabon;
        private QueryResult queryResult;

        public QueryTask(String query, Strabon strabon) {
            this.query = query;
            this.strabon = strabon;
            this.queryResult = new QueryResult();
        }

        public QueryResult getQueryResult() {
            return queryResult;
        }

        @Override
        public void run() {
            runQuery();
        }

        public void runQuery() {

            LOGGER.info("Query Evaluation: Started");
            Boolean isComplete = true;
            List<HashMap<String, String>> results = new ArrayList<>();
            long startNanoTime = System.nanoTime();
            long queryNanoTime;
            try {

                TupleQuery tupleQuery = (TupleQuery) strabon.query(query, Format.TUQU, strabon.getSailRepoConnection(), System.out);

                TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
                queryNanoTime = System.nanoTime();

                while (tupleQueryResult.hasNext()) {

                    BindingSet bindingSet = tupleQueryResult.next();
                    List<String> bindingNames = tupleQueryResult.getBindingNames();
                    HashMap<String, String> result = new HashMap<>();
                    for (String binding : bindingNames) {
                        Value value = bindingSet.getValue(binding);
                        String valueStr = value.stringValue();
                        result.put(binding, valueStr);
                    }
                    results.add(result);
                }

            } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
                queryNanoTime = startNanoTime;
                results.clear();
                isComplete = false;
            }
            long resultsNanoTime = System.nanoTime();

            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isComplete);
            LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
        }
    }

}
