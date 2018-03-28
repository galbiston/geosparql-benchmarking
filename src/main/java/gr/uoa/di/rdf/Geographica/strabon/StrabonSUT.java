/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import gr.uoa.di.rdf.Geographica.systemsundertest.SystemUnderTest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
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

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 *
 */
public class StrabonSUT implements SystemUnderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrabonSUT.class);

    private Strabon strabon = null;
    private HashMap<String, String> firstResult = new HashMap<>();

    String db = null;
    String user = null;
    String passwd = null;
    Integer port = null;
    String host = null;

    public StrabonSUT(String db, String user, String passwd, Integer port,
            String host) throws Exception {

        this.db = db;
        this.user = user;
        this.passwd = passwd;
        this.port = port;
        this.host = host;
    }

    @Override
    public HashMap<String, String> getFirstResult() {
        return firstResult;
    }

    @Override
    public Object getSystem() {
        return this.strabon;
    }

    @Override
    public void initialize() {

        try {
            strabon = new Strabon(db, user, passwd, port, host, true);
        } catch (Exception e) {
            LOGGER.error("Cannot initialize strabon");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
        }
    }

    static class Executor implements Runnable {

        private final String query;
        private final Strabon strabon;
        private long[] returnValue;
        private final HashMap<String, String> firstResult = new HashMap<>();

        public Executor(String query, Strabon strabon, int timeoutSecs) {
            this.query = query;
            this.strabon = strabon;
            this.returnValue = new long[]{timeoutSecs + 1, timeoutSecs + 1, timeoutSecs + 1, -1};
        }

        public long[] getRetValue() {
            return returnValue;
        }

        public HashMap<String, String> getFirstBindingSet() {
            return firstResult;
        }

        @Override
        public void run() {
            try {
                runQuery();
            } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void runQuery() throws MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException, IOException {

            LOGGER.info("Evaluating query...");
            TupleQuery tupleQuery = (TupleQuery) strabon.query(query,
                    eu.earthobservatory.utils.Format.TUQU, strabon.getSailRepoConnection(), (OutputStream) System.out);

            long results = 0;

            long t1 = System.nanoTime();
            TupleQueryResult result = tupleQuery.evaluate();
            long t2 = System.nanoTime();

            if (result.hasNext()) {

                BindingSet bindingSet = result.next();
                List<String> bindingNames = result.getBindingNames();
                for (String binding : bindingNames) {

                    Value value = bindingSet.getValue(binding);
                    String valueStr = value.stringValue();
                    firstResult.put(binding, valueStr);
                }
                results++;
            }
            while (result.hasNext()) {
                results++;
                result.next();
            }
            long t3 = System.nanoTime();

            LOGGER.info("Query evaluated");
            this.returnValue = new long[]{t2 - t1, t3 - t2, t3 - t1, results};
        }
    }

    @Override
    public long[] runQueryWithTimeout(String query, int timeoutSecs) {
        //maintains a thread for executing the doWork method
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        //set the executor thread working
        Executor runnable = new Executor(query, strabon, timeoutSecs);

        final Future<?> future = executor.submit(runnable);
        boolean isTimedout = false;
        //check the outcome of the executor thread and limit the time allowed for it to complete
        long tt1 = System.nanoTime();
        try {
            LOGGER.debug("Future started");
            future.get(timeoutSecs, TimeUnit.SECONDS);
            LOGGER.debug("Future end");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            isTimedout = true;
            LOGGER.info("time out!");
            LOGGER.info("Restarting Strabon...");
            this.restart();
            LOGGER.info("Closing Strabon...");
            this.close();
        } finally {
            LOGGER.debug("Future canceling...");
            future.cancel(true);
            LOGGER.debug("Executor shutting down...");
            executor.shutdown();
            try {
                LOGGER.debug("Executor waiting for termination...");
                executor.awaitTermination(timeoutSecs, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.gc();
        }

        LOGGER.debug("RetValue: " + Arrays.toString(runnable.getRetValue()));

        if (isTimedout) {
            long tt2 = System.nanoTime();
            return new long[]{tt2 - tt1, tt2 - tt1, tt2 - tt1, -1};
        } else {
            this.firstResult = runnable.getFirstBindingSet();
            return runnable.getRetValue();
        }
    }

    @Override
    public long[] runUpdate(String query) throws MalformedQueryException {

        LOGGER.info("Executing update...");
        long t1 = System.nanoTime();
        strabon.update(query, strabon.getSailRepoConnection());
        long t2 = System.nanoTime();
        LOGGER.info("Update executed");

        long[] ret = {-1, -1, t2 - t1, -1};
        return ret;
    }

    @Override
    public void close() {

        LOGGER.info("Closing..");
        try {
            strabon.close();
            strabon = null;
            firstResult = new HashMap<>();
        } catch (Exception e) {
        }
        // TODO
//		Runtime run = Runtime.getRuntime();
//
//		Process pr = run.exec(restart_script);
//		pr.waitFor();
//
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
            LOGGER.info("Restarting Strabon (Postgres) ...");

            pr = Runtime.getRuntime().exec(restart_postgres);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                LOGGER.error("Something went wrong while stoping postgres");
            }

            Thread.sleep(5000);

            if (strabon != null) {
                try {
                    strabon.close();
                } catch (Exception e) {
                    LOGGER.error("Exception occured while restarting Strabon. ");
                    e.printStackTrace();
                } finally {
                    strabon = null;
                }
            }
            firstResult = new HashMap<>();
            strabon = new Strabon(db, user, passwd, port, host, true);
            LOGGER.info("Strabon (Postgres) restarted");
        } catch (Exception e) {
            LOGGER.error("Cannot restart Strabon");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
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
    public String translateQuery(String query, String label) {
        String translatedQuery = query;

        translatedQuery = translatedQuery.replaceAll("geof:distance", "strdf:distance");

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

        return translatedQuery;
    }
}
