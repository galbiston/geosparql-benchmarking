/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package geosparql_benchmarking.test_systems;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import geosparql_benchmarking.experiments.QueryResult;
import geosparql_benchmarking.experiments.TestSystem;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 *
 */
public class StrabonTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrabonTestSystem.class);

    private Strabon strabon = null;

    String db;
    String user;
    String passwd;
    Integer port;
    String host;

    public StrabonTestSystem(String db, String user, String passwd, Integer port, String host) {
        this.db = db;
        this.user = user;
        this.passwd = passwd;
        this.port = port;
        this.host = host;
    }

    @Override
    public String getName() {
        return "Strabon";
    }

    @Override
    public void initialize() {

        try {
            strabon = new Strabon(db, user, passwd, port, host, true);
        } catch (Exception ex) {
            LOGGER.error("Strabon: Failed to initialize - {}", ex.getMessage());
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
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } catch (TimeoutException e) {
            LOGGER.info("Strabon Query Timeout: Restarting");
            this.restart();
            LOGGER.info("Strabon Timeout: Closing");
            this.close();
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

        return new QueryResult(startNanoTime, resultsNanoTime, true);
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
    public void restart() {

        String[] restart_postgres = {"/bin/sh", "-c", "service postgresql restart"};

        Process pr;

        try {
            LOGGER.info("Strabon (Postgres): Restarting");

            pr = Runtime.getRuntime().exec(restart_postgres);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                LOGGER.error("Postgres Stopping Failed");
            }

            Thread.sleep(5000);

            if (strabon != null) {
                try {
                    strabon.close();
                } catch (Exception ex) {
                    LOGGER.error("Strabon Close Failed: {}", ex.getMessage());
                } finally {
                    strabon = null;
                }
            }

            strabon = new Strabon(db, user, passwd, port, host, true);
            LOGGER.info("Strabon (Postgres): Restarted");
        } catch (Exception ex) {
            LOGGER.error("Strabon Restart Failed: {}", ex.getMessage());
        }
    }

    @Override
    public void clearCaches() {

        String[] stop_postgres = {"/bin/sh", "-c", "service postgresql stop"};
        String[] clear_caches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
        String[] start_postgres = {"/bin/sh", "-c", "service postgresql start"};

        Process pr;

        try {
            LOGGER.info("Clearing caches...");

            pr = Runtime.getRuntime().exec(stop_postgres);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                LOGGER.error("Something went wrong while stoping postgres");
            }
//			System.in.read();

            pr = Runtime.getRuntime().exec(clear_caches);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                LOGGER.error("Something went wrong while clearing caches");
            }
//			System.in.read();

            pr = Runtime.getRuntime().exec(start_postgres);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                LOGGER.error("Something went wrong while clearing caches");
            }

            Thread.sleep(5000);
            LOGGER.info("Caches cleared");
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Cannot clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
        }
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

            LOGGER.info("Evaluating query");
            List<HashMap<String, String>> results = new ArrayList<>();
            boolean isCompleted = true;
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
                LOGGER.error("Execption: {}", ex.getMessage());
                queryNanoTime = startNanoTime;
                isCompleted = false;
            }
            long resultsNanoTime = System.nanoTime();

            LOGGER.info("Query evaluated");
            LOGGER.info("Elapsed Time - Query: {}, Results: {}", queryNanoTime - startNanoTime, resultsNanoTime - queryNanoTime);
            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isCompleted);
        }
    }

    public static void loadDataset(HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
        LOGGER.info("Strabon Loading: Started");
        Strabon strabon = null;
        try {
            String db = "endpoint";
            String user = "postgres"; //String user = "postgres";
            String passwd = "postgres"; //String passwd = "postgres";
            Integer port = 5432;
            String host = "localhost"; //"localhost"; //"127.0.0.1"
            Boolean checkForLockTable = true;
            strabon = new Strabon(db, user, passwd, port, host, checkForLockTable);

            String baseURI = null;
            String format = "NTRIPLES";

            for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
                String src = entry.getValue().toURI().toURL().toString();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", src, graph);
                strabon.storeInRepo(src, baseURI, graph, format, inferenceEnabled);
                LOGGER.info("Loading: {} into {}: Completed", src, graph);
            }

        } catch (Exception ex) {
            LOGGER.error("Load Strabon exception: {}", ex);
        } finally {
            if (strabon != null) {
                strabon.close();
            }
        }
        LOGGER.info("Strabon Loading: Completed");

    }
}
