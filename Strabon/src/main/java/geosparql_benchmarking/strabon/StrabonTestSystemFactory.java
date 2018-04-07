/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import static geosparql_benchmarking.experiments.BenchmarkExecution.RESULTS_FOLDER;
import geosparql_benchmarking.experiments.TestSystem;
import geosparql_benchmarking.experiments.TestSystemFactory;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class StrabonTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String db;
    private final String user;
    private final String password;
    private final Integer port;
    private final String host;
    private final File resultsFolder;

    public StrabonTestSystemFactory(String db, String user, String password, Integer port, String host, String resultsFolder) {
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        this.host = host;
        this.resultsFolder = new File(RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
    }

    public String getDb() {
        return db;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    @Override
    public TestSystem getTestSystem() {
        try {
            return new StrabonTestSystem(db, user, password, port, host);
        } catch (Exception ex) {
            LOGGER.error("Strabon Exception: {}", ex.getMessage());
            throw new AssertionError("Strabon failed to initialise.");
        }
    }

    @Override
    public String getTestSystemName() {
        return "Strabon";
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    public static void loadDataset(HashMap<String, File> datasetMap, String baseURI, String format, Boolean inferenceEnabled, StrabonTestSystemFactory testSystemFactory) {
        LOGGER.info("Strabon Loading: Started");
        Strabon strabon = null;
        try {
            String db = testSystemFactory.getDb();
            String user = testSystemFactory.getUser(); //String user = "postgres";
            String password = testSystemFactory.getPassword(); //String passwd = "postgres";
            Integer port = testSystemFactory.getPort();
            String host = testSystemFactory.getHost(); //"localhost"; //"127.0.0.1"
            Boolean checkForLockTable = true;
            strabon = new Strabon(db, user, password, port, host, checkForLockTable);

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
