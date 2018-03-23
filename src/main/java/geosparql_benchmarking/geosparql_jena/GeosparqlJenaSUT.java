/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import gr.uoa.di.rdf.Geographica.systemsundertest.SystemUnderTest;
import implementation.GeoSPARQLModel;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.impl.MapBindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class GeosparqlJenaSUT implements SystemUnderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeosparqlJenaSUT.class);

    private BindingSet firstBindingSet;
    private Dataset dataset;
    private final File datasetFolder;

    public GeosparqlJenaSUT(File datasetFolder) {
        this.firstBindingSet = null;
        this.datasetFolder = datasetFolder;
        this.dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
    }

    @Override
    public long[] runQueryWithTimeout(String query, int timeoutSecs) throws Exception {
        //maintains a thread for executing the doWork method
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        //set the executor thread working
        Executor runnable = new Executor(query, dataset, timeoutSecs);

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
            LOGGER.info("Restarting GeoSPARQL Jena...");
            this.restart();
            LOGGER.info("Closing GeoSPRAQL Jena...");
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
            this.firstBindingSet = runnable.getFirstBindingSet();
            return runnable.getRetValue();
        }
    }

    @Override
    public long[] runUpdate(String query) {
        LOGGER.info("Executing update...");
        long t1 = System.nanoTime();
        UpdateRequest updateRequest = UpdateFactory.create(query);
        UpdateAction.execute(updateRequest, dataset);
        long t2 = System.nanoTime();
        LOGGER.info("Update executed");

        long[] ret = {-1, -1, t2 - t1, -1};
        return ret;
    }

    @Override
    public void initialize() {
        GeoSPARQLModel.loadFunctions();
        //TODO May need to apply RDFS inference. May need to apply at Assembly file level.
    }

    @Override
    public void close() {
        TDBFactory.release(dataset);
        firstBindingSet = null;
    }

    @Override
    public void clearCaches() {
        //No caches to clear but release resources and reconnect.
        TDBFactory.release(dataset);
        dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        firstBindingSet = null;
    }

    @Override
    public void restart() {
        //No restarting but release resources and reconnect.
        TDBFactory.release(dataset);
        dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        firstBindingSet = null;
    }

    @Override
    public Object getSystem() {
        return dataset;
    }

    @Override
    public String translateQuery(String query, String label) {
        //No query translation.
        return query;
    }

    @Override
    public BindingSet getFirstBindingSet() {
        return firstBindingSet;
    }

    static class Executor implements Runnable {

        private final String queryString;
        private final Dataset dataset;
        private BindingSet firstBindingSet = null;
        private long[] returnValue;

        public Executor(String queryString, Dataset dataset, int timeoutSecs) {
            this.queryString = queryString;
            this.dataset = dataset;
            this.returnValue = new long[]{timeoutSecs + 1, timeoutSecs + 1, timeoutSecs + 1, -1};
            LOGGER.debug("RetVal initialized: " + this.returnValue[0] + " " + this.returnValue[1] + " " + this.returnValue[2] + " " + this.returnValue[3]);
        }

        public long[] getRetValue() {
            return returnValue;
        }

        public BindingSet getFirstBindingSet() {
            return firstBindingSet;
        }

        @Override
        public void run() {
            runQuery();
        }

        public void runQuery() {

            LOGGER.info("Evaluating query");
            dataset.begin(ReadWrite.READ);
            long t1 = System.nanoTime();
            long t2 = System.nanoTime();
            int results = 0;

            try (QueryExecution qexec = QueryExecutionFactory.create(queryString, dataset)) {
                ResultSet rs = qexec.execSelect();

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
                            LOGGER.error("Resource not recognized");
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
            } finally {
                dataset.end();
            }

            long t3 = System.nanoTime();
            LOGGER.info("Query evaluated at " + (t3 - t1));
            this.returnValue = new long[]{t2 - t1, t3 - t2, t3 - t1, results};
        }
    }

}
