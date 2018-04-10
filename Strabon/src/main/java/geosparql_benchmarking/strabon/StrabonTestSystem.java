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
import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        restartPostgresService(host, port);
        strabon = new Strabon(db, user, password, port, host, true);
    }

    //Restart PostgreSQL and clear caches where possible.
    //Postgres PGDATA needs to be set for PostreSQL/version/data and PostgreSQL/version/bin needs to be added to the PATH variable.
    //Windows requires the PostrgreSQL service to be stopped if running: net stop POSTGRESQL_SERVICE
    //POSTRGRESQL_SERVICE can be found in Task Manager:Services - e.g. postgresql-x64-10
    //Commands run directly in PowerShell need '&' and a space at the start.
    private static final String POSTGRES_BIN_PATH = "\"C:\\Program Files\\PostgreSQL\\10\\bin\\";
    private static final String PG_ISREADY_PATH = POSTGRES_BIN_PATH + "pg_isready\"";
    private static final String PG_CTL_PATH = POSTGRES_BIN_PATH + "pg_ctl\"";
    private static final String POSTGRES_DATA_PATH = "\"C:\\Program Files\\PostgreSQL\\10\\data\\\"";

    private void restartPostgresService(String host, Integer port) {

        try {

            String[] postgresReady = {PG_ISREADY_PATH, "-h", host, "-p", port.toString()};
            Process pr = Runtime.getRuntime().exec(postgresReady);
            int readyResult = pr.waitFor();
            System.out.println(StringUtils.join(postgresReady, " "));
            LOGGER.info("Postgres {}", pr.exitValue());
            if (readyResult == 0) {
                //Stop Postgresql
                String[] postgresStop = {PG_CTL_PATH, "stop", "-s", "-w", "-m", "fast"};
                pr = Runtime.getRuntime().exec(postgresStop);
                int stopResult = pr.waitFor();
                System.out.println(StringUtils.join(postgresStop, " "));
                if (stopResult > 0) {
                    LOGGER.error("PostgreSQL failed to stop: Exit Value - {}", stopResult);
                } else {
                    LOGGER.info("Postgres stopped");
                }
            }
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("nix") | osName.contains("nux") | osName.contains("aux")) {
                //Stop, drop caches and start the service. No documentation found to clear other OS caches.
                String[] dropCaches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
                pr = Runtime.getRuntime().exec(dropCaches);
                int cacheDropResult = pr.waitFor();
                if (cacheDropResult > 0) {
                    LOGGER.error("Dropping caches failed: Exit Value - {}", pr.exitValue());
                }
            }

            //Start Postgresql
            String[] postgresStart = {PG_CTL_PATH, "start", "-w", "-o", "\"-h " + host + "\"", "-o", "\"-p " + port + "\"", "-D", POSTGRES_DATA_PATH};
            System.out.println(StringUtils.join(postgresStart, " "));
            pr = Runtime.getRuntime().exec(postgresStart);
            int startResult = pr.waitFor();
            if (startResult > 0) {
                LOGGER.error("PostgreSQL failed to start: Exit Value - {}", startResult);
            } else {
                LOGGER.info("Postgres started");
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.error("Strabon Cache Clearing: {}", ex.getMessage());
        }
    }

    /*
    //Default service name according to: https://www.postgresql.org/docs/10/static/app-pg-ctl.html
    private static final String POSTGRESQL_SERVICE = "PostgreSQL";

    String osName = System.getProperty("os.name").toLowerCase();

    if (osName.contains (
        "win")) {
                //Stop and start the service only.
                new ProcessExecutor().command("net", "stop", POSTGRESQL_SERVICE)
                .command("net", "start", POSTGRESQL_SERVICE)
                .redirectOutput(Slf4jStream.ofCaller().asInfo())
                .timeout(5, TimeUnit.SECONDS).execute();

    }

    else if (osName.contains (
        "nix") | osName.contains("nux") | osName.contains("aux") | osName.contains("mac")) {
                //Stop, drop caches and start the service. No documentation found to clear the OS caches.

                new ProcessExecutor().command("/bin/sh", "-c", "service postgresql stop")
                .command("/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches")
                .command("/bin/sh", "-c", "service postgresql start")
                .redirectOutput(Slf4jStream.ofCaller().asInfo())
                .timeout(5, TimeUnit.SECONDS).execute();

    }


        else {
                LOGGER.error("Unrecognised OS name for Strabon Cache Clearing: {}", osName);
    }

     */
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
