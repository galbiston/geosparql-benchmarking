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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 *
 */
public class StrabonSUT implements SystemUnderTest {

    static Logger logger = Logger.getLogger(StrabonSUT.class.getSimpleName());

    private Strabon strabon = null;
    private BindingSet firstBindingSet;

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
    public BindingSet getFirstBindingSet() {
        return firstBindingSet;
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
            logger.fatal("Cannot initialize strabon");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
        }
    }

    static class Executor implements Runnable {

        private final String query;
        private final Strabon strabon;
        private long[] returnValue;
        private BindingSet firstBindingSet;

        public Executor(String query, Strabon strabon, int timeoutSecs) {
            this.query = query;
            this.strabon = strabon;
            this.returnValue = new long[]{timeoutSecs + 1, timeoutSecs + 1, timeoutSecs + 1, -1};
        }

        public long[] getRetValue() {
            return returnValue;
        }

        public BindingSet getFirstBindingSet() {
            return firstBindingSet;
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

            logger.info("Evaluating query...");
            TupleQuery tupleQuery = (TupleQuery) strabon.query(query,
                    eu.earthobservatory.utils.Format.TUQU, strabon.getSailRepoConnection(), (OutputStream) System.out);

            long results = 0;

            long t1 = System.nanoTime();
            TupleQueryResult result = tupleQuery.evaluate();
            long t2 = System.nanoTime();

            if (result.hasNext()) {
                this.firstBindingSet = result.next();
                results++;
            }
            while (result.hasNext()) {
                results++;
                result.next();
            }
            long t3 = System.nanoTime();

            logger.info("Query evaluated");
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
            logger.debug("Future started");
            future.get(timeoutSecs, TimeUnit.SECONDS);
            logger.debug("Future end");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            isTimedout = true;
            logger.info("time out!");
            logger.info("Restarting Strabon...");
            this.restart();
            logger.info("Closing Strabon...");
            this.close();
        } finally {
            logger.debug("Future canceling...");
            future.cancel(true);
            logger.debug("Executor shutting down...");
            executor.shutdown();
            try {
                logger.debug("Executor waiting for termination...");
                executor.awaitTermination(timeoutSecs, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.gc();
        }

        logger.debug("RetValue: " + Arrays.toString(runnable.getRetValue()));

        if (isTimedout) {
            long tt2 = System.nanoTime();
            return new long[]{tt2 - tt1, tt2 - tt1, tt2 - tt1, -1};
        } else {
            this.firstBindingSet = runnable.getFirstBindingSet();
            return runnable.getRetValue();
        }
    }

    @Override
    public long[] runUpdate(String query) throws MalformedQueryException {

        logger.info("Executing update...");
        long t1 = System.nanoTime();
        strabon.update(query, strabon.getSailRepoConnection());
        long t2 = System.nanoTime();
        logger.info("Update executed");

        long[] ret = {-1, -1, t2 - t1, -1};
        return ret;
    }

    @Override
    public void close() {

        logger.info("Closing..");
        try {
            strabon.close();
            strabon = null;
            firstBindingSet = null;
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
            logger.fatal("Cannot clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
        }
        logger.info("Closed (caches not cleared)");
    }

    @Override
    public void restart() {

        String[] restart_postgres = {"/bin/sh", "-c", "service postgresql restart"};

        Process pr;

        try {
            logger.info("Restarting Strabon (Postgres) ...");

            pr = Runtime.getRuntime().exec(restart_postgres);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                logger.error("Something went wrong while stoping postgres");
            }

            Thread.sleep(5000);

            if (strabon != null) {
                try {
                    strabon.close();
                } catch (Exception e) {
                    logger.error("Exception occured while restarting Strabon. ");
                    e.printStackTrace();
                } finally {
                    strabon = null;
                }
            }
            firstBindingSet = null;
            strabon = new Strabon(db, user, passwd, port, host, true);
            logger.info("Strabon (Postgres) restarted");
        } catch (Exception e) {
            logger.fatal("Cannot restart Strabon");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
        }
    }

    @Override
    public void clearCaches() {

        String[] stop_postgres = {"/bin/sh", "-c", "service postgresql stop"};
        String[] clear_caches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
        String[] start_postgres = {"/bin/sh", "-c", "service postgresql start"};

        Process pr;

        try {
            logger.info("Clearing caches...");

            pr = Runtime.getRuntime().exec(stop_postgres);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                logger.error("Something went wrong while stoping postgres");
            }
//			System.in.read();

            pr = Runtime.getRuntime().exec(clear_caches);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                logger.error("Something went wrong while clearing caches");
            }
//			System.in.read();

            pr = Runtime.getRuntime().exec(start_postgres);
            pr.waitFor();
//			System.out.println(pr.exitValue());
            if (pr.exitValue() != 0) {
                logger.error("Something went wrong while clearing caches");
            }

            Thread.sleep(5000);
            logger.info("Caches cleared");
        } catch (IOException | InterruptedException e) {
            logger.fatal("Cannot clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
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
        }

        return translatedQuery;
    }
}
