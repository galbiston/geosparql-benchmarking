/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.parliament;

import com.bbn.parliament.jena.graph.KbGraph;
import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.bbn.parliament.jena.graph.index.IndexFactoryRegistry;
import com.bbn.parliament.jena.graph.index.IndexManager;
import com.bbn.parliament.jena.graph.index.spatial.Constants;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndex;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndexFactory;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import gr.uoa.di.rdf.Geographica.systemsundertest.SystemUnderTest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.impl.MapBindingSet;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 * @author Kostis Kyzirakos <kkyzir@di.uoa.gr>
 *
 */
public class ParliamentSUT implements SystemUnderTest {

    static Logger logger = Logger.getLogger(ParliamentSUT.class.getSimpleName());

    static QueryExecution qexec = null;
    DataSource dataSource = null;
    KbGraphStore graphStore = null;
    SpatialIndexFactory factory = null;
    KbGraph graph = null;
    SpatialIndex index = null;

    BindingSet firstBindingSet;

    public ParliamentSUT() {
    }

    @Override
    public BindingSet getFirstBindingSet() {
        return firstBindingSet;
    }

    @Override
    public void initialize() {
        if (!java.lang.System.getProperty("java.library.path").contains("parliament-dependencies")) {
            logger.warn("Do not forget to add the folder Geographica/runtime/src/main/resources/parliament-dependencies/linux-32 or 64 to the variable java.library.path.");
            logger.warn("You will possible get some exceptions...");
        }

        logger.info("Initializing Parliament...");
        // create spatial index factory and configure for GeoSPARQL. This is used
        // by the GraphStore whenever a new named graph is created.
        factory = new SpatialIndexFactory();
        Properties properties = new Properties();
        properties.setProperty(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);
        properties.setProperty(Constants.GEOSPARQL_ENABLED, Boolean.TRUE.toString());
        factory.configure(properties);

        // register factory
        IndexFactoryRegistry.getInstance().register(factory);

        // create a Parliament graph and graph store
        graph = KbGraphFactory.createDefaultGraph();
        graphStore = new KbGraphStore(graph);
        graphStore.initialize();

        // DO NOT RE-INDEX THE GRAPH!!!!!
        boolean firstTime = false;
        if (firstTime) {
            // create spatial index from factory
            index = factory.createIndex(graph, null);

            // register index with IndexManager
            IndexManager.getInstance().register(graph, null, factory, index);

            // the following tells the graph store that the graph is now an
            // indexing graph. This is necessary so that the next time Parliament
            // loads, the index is read in automatically.
            graphStore.setIndexingEnabled(KbGraphStore.DEFAULT_GRAPH_NODE, true);
        }

        // create a new datasource (that will be used later on to append named graphs)
        dataSource = DatasetFactory.create(graphStore);

        logger.info("Parliament initialized");
    }

    static class Executor implements Runnable {

        private final String queryString;
        private final DataSource dataSource;
        private BindingSet firstBindingSet = null;
        private long[] returnValue;

        public Executor(String queryString, DataSource dataSource, int timeoutSecs) {
            this.queryString = queryString;
            this.dataSource = dataSource;
            this.returnValue = new long[]{timeoutSecs + 1, timeoutSecs + 1, timeoutSecs + 1, -1};
            logger.debug("RetVal initialized: " + this.returnValue[0] + " " + this.returnValue[1] + " " + this.returnValue[2] + " " + this.returnValue[3]);
        }

        public long[] getRetValue() {
            return returnValue;
        }

        public Object getFirstBindingSet() {
            return firstBindingSet;
        } // returns BindingSet

        @Override
        public void run() {
            runQuery();
        }

        public void runQuery() {

            logger.info("Evaluating query");
            long t1 = System.nanoTime();
            qexec = QueryExecutionFactory.create(queryString, dataSource);
            ResultSet rs = qexec.execSelect();
            long t2 = System.nanoTime();
            int results = 0;

            if (rs.hasNext()) {
                QuerySolution querySolution = rs.next();
                ValueFactory valueFactory = new ValueFactoryImpl();
                Iterator<String> varNames = querySolution.varNames();
                Value value = null;
                MapBindingSet bindingSet = new MapBindingSet();
                while (varNames.hasNext()) {
                    String varName = varNames.next();
                    RDFNode solution = querySolution.get(varName);
                    if (solution.isLiteral()) {
                        Literal literal = solution.asLiteral();
                        String valueStr = literal.getString();
                        String datatypeStr = literal.getDatatypeURI();
                        if (datatypeStr != null) {
                            URI datatype = valueFactory.createURI(datatypeStr);
                            value = valueFactory.createLiteral(valueStr, datatype);
                        } else {
                            value = valueFactory.createLiteral(valueStr);
                        }
                    } else if (solution.isResource()) {
                        Resource resource = solution.asResource();
                        value = valueFactory.createURI(resource.getURI());
                    } else {
                        logger.error("Resource not recognized");
                    }
                    bindingSet.addBinding(varName, value);
                }
                this.firstBindingSet = bindingSet;
                results++;
            }
            while (rs.hasNext()) {
                rs.next();
                results++;
            }
            long t3 = System.nanoTime();
            logger.info("Query evaluated at " + (t3 - t1));
            this.returnValue = new long[]{t2 - t1, t3 - t2, t3 - t1, results};
        }
    }

    @Override
    public long[] runQueryWithTimeout(String query, int timeoutSecs) {

        //maintains a thread for executing the doWork method
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        //set the executor thread working
        Executor runnable = new Executor(query, dataSource, timeoutSecs);

        final Future<?> future = executor.submit(runnable);
        boolean isTimedout = false;
        //check the outcome of the executor thread and limit the time allowed for it to complete
        long tt1 = System.nanoTime();
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            isTimedout = true;
            logger.info("time out!");
        } finally {
            qexec.abort();
            qexec.close();
            logger.info("Closing Parliament...");
            future.cancel(true);
            logger.debug("Future canceled");
            executor.shutdown();
            logger.debug("Executor shutdown");
            try {
                //executor.awaitTermination(timeoutSecs, TimeUnit.SECONDS);
                executor.awaitTermination(1, TimeUnit.SECONDS);
                logger.debug("Executor terminated");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.gc();
            //logger.debug("Garbage collected");
            //this.clearCaches();
            //logger.debug("Caches are cleared");
            //this.close();
            //logger.debug("Parliament is closed");
            //this.initialize();
            //logger.debug("Parliament is initialized");
        }

        logger.debug("RetValue: " + Arrays.toString(runnable.getRetValue()));
        if (isTimedout) {
            long tt2 = System.nanoTime();
            return new long[]{tt2 - tt1, tt2 - tt1, tt2 - tt1, -1};
        } else {
            this.firstBindingSet = (BindingSet) runnable.getFirstBindingSet();
            return runnable.getRetValue();
        }
    }

    @Override
    public long[] runUpdate(String update) {

        logger.info("Executing update...");

        long t1 = System.nanoTime();

        UpdateRequest u = UpdateFactory.create(update, Syntax.syntaxARQ);
        UpdateProcessor updateProcessor = UpdateExecutionFactory.create(u, graphStore);
        updateProcessor.execute();

        long t2 = System.nanoTime();

        long[] ret = {-1, -1, t2 - t1, -1};
        logger.info("Update executed...");
        return ret;
    }

    @Override
    public void close() {
        try {
            IndexManager.getInstance().unregister(graph, null, index);
            IndexFactoryRegistry.getInstance().unregister(factory);
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
            dataSource = null;

            if (graphStore != null) {
                graphStore.close();
            }
            graphStore = null;

            if (graph != null) {
                graph = null;
            }
            if (index != null) {
                index = null;
            }
            if (factory != null) {
                factory = null;
            }

            firstBindingSet = null;
        }

        System.gc();
        logger.info("Parliament closed");
    }

    @Override
    public void restart() {
//		String[] restart_parliament = {"/bin/sh", "-c", "cd /home/benchmark/parliament/parliament-data && ./reset-kb.sh"};
//		Process pr;

        try {
            logger.info("No need to restart parliament");
//			logger.info("Restarting Parliament...");

//			pr = Runtime.getRuntime().exec(restart_parliament);
//			pr.waitFor();
//			if ( pr.exitValue() != 0) {
//				logger.error("Something went wrong while restarting Parliament");
//			}
//
//			Thread.sleep(5000);
//			logger.info("Parliament restarted");
            //initialize(); // Parliament will be initializd out of clearCaches()
            firstBindingSet = null;
        } catch (Exception e) {
            logger.fatal("Cannont clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
        }
    }

    @Override
    public void clearCaches() {
        String[] clear_caches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
        Process pr;

        try {
            logger.info("Clearing caches...");
            //close(); // clearCaches should be called after Parliament is closed

            pr = Runtime.getRuntime().exec(clear_caches);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                logger.error("Something went wrong while clearing caches");
            }

            Thread.sleep(5000);
            logger.info("Caches cleared");

            //initialize(); // Parliament will be initializd out of clearCaches()
        } catch (IOException | InterruptedException e) {
            logger.fatal("Cannont clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.fatal(stacktrace);
        }
    }

    @Override
    public Object getSystem() {
        return this.dataSource;
    }

    @Override
    public String translateQuery(String query, String label) {
        String translatedQuery = query;
        translatedQuery = translatedQuery.replace("PREFIX geo: <http://www.opengis.net/ont/geosparql#>", "PREFIX geo: <http://www.opengis.net/ont/sf#>");
        translatedQuery = translatedQuery.replace("http://www.opengis.net/ont/geosparql#wktLiteral", "http://www.opengis.net/ont/sf#wktLiteral");
        translatedQuery = translatedQuery.replace("geo:wktLiteral", "<http://www.opengis.net/ont/sf#wktLiteral>");

        if (label.equals("Area_CLC")) {
            translatedQuery = null;
        } else if (label.equals("Find_Closest_Populated_Place")
                || label.equals("Find_Closest_Motorway")) {
            translatedQuery = translatedQuery.replace("strdf:distance", "geof:distance");
        }
        return translatedQuery;
    }
}
