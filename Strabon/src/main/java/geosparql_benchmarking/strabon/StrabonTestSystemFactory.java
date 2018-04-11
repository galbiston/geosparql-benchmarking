/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import static geosparql_benchmarking.experiments.BenchmarkExecution.RESULTS_FOLDER;
import geosparql_benchmarking.experiments.DatasetLoadResult;
import geosparql_benchmarking.experiments.DatasetLoadTimeResult;
import geosparql_benchmarking.experiments.TestSystem;
import geosparql_benchmarking.experiments.TestSystemFactory;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class StrabonTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String dbName;
    private final String user;
    private final String password;
    private final Integer port;
    private final String host;
    private final File resultsFolder;
    private final String postgresBinPath;
    private final String postgresDataPath;
    private final String postgresIsReadyPath;
    private final String postgresPG_CTLPath;
    private final String postgresCreateDBPath;
    private final String postgresDropDBPath;
    public static final String TEST_SYSTEM_NAME = "Strabon";

    public StrabonTestSystemFactory(String dbName, String user, String password, Integer port, String host, String resultsFolder, String postgresBinPath, String postgresDataPath) {
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.port = port;
        this.host = host;
        this.resultsFolder = new File(RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();

        //Check whether using the environment variable route.
        this.postgresBinPath = postgresBinPath;
        if (postgresBinPath.isEmpty()) {
            this.postgresIsReadyPath = "pg_isready";
            this.postgresPG_CTLPath = "pg_ctl";
            this.postgresCreateDBPath = "createDB";
            this.postgresDropDBPath = "dropDB";
        } else {
            this.postgresIsReadyPath = postgresBinPath + "pg_isready\"";
            this.postgresPG_CTLPath = postgresBinPath + "pg_ctl\"";
            this.postgresCreateDBPath = postgresBinPath + "createDB\"";
            this.postgresDropDBPath = postgresBinPath + "dropDB\"";
        }
        this.postgresDataPath = postgresDataPath;
    }

    public String getDbName() {
        return dbName;
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

    public String getPostgresBinPath() {
        return postgresBinPath;
    }

    public String getPostgresDataPath() {
        return postgresDataPath;
    }

    public String getPostgresIsReadyPath() {
        return postgresIsReadyPath;
    }

    public String getPostgresPG_CTLPath() {
        return postgresPG_CTLPath;
    }

    public String getPostgresCreateDBPath() {
        return postgresCreateDBPath;
    }

    public String getPostgresDropDBPath() {
        return postgresDropDBPath;
    }

    @Override
    public TestSystem getTestSystem() {
        return getStrabonTestSystem();
    }

    public StrabonTestSystem getStrabonTestSystem() {
        try {
            return new StrabonTestSystem(dbName, user, password, port, host, postgresIsReadyPath, postgresPG_CTLPath, postgresDataPath);
        } catch (Exception ex) {
            LOGGER.error("Strabon Exception: {}", ex.getMessage());
            throw new AssertionError("Strabon failed to initialise.");
        }
    }

    @Override
    public String getTestSystemName() {
        return TEST_SYSTEM_NAME;
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    public static DatasetLoadResult loadDataset(HashMap<String, File> datasetMap, String baseURI, String format, Boolean inferenceEnabled, StrabonTestSystemFactory testSystemFactory) {
        LOGGER.info("Strabon Loading: Started");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();
        Strabon strabon = null;
        try {
            String dbName = testSystemFactory.getDbName();
            String user = testSystemFactory.getUser(); //String user = "postgres";
            String password = testSystemFactory.getPassword(); //String passwd = "postgres";
            Integer port = testSystemFactory.getPort();
            String host = testSystemFactory.getHost(); //"localhost"; //"127.0.0.1"
            Boolean checkForLockTable = true;
            strabon = new Strabon(dbName, user, password, port, host, checkForLockTable);

            for (Map.Entry<String, File> entry : datasetMap.entrySet()) {

                String src = entry.getValue().toURI().toURL().toString();
                String graphName = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", src, graphName);
                long datasetStartNanoTime = System.nanoTime();
                strabon.storeInRepo(src, baseURI, graphName, format, inferenceEnabled);
                long datasetEndNanoTime = System.nanoTime();
                DatasetLoadTimeResult datasetLoadTimeResult = new DatasetLoadTimeResult(graphName, datasetStartNanoTime, datasetEndNanoTime);
                datasetLoadTimeResults.add(datasetLoadTimeResult);
                LOGGER.info("Loading: {} into {}: Completed", src, graphName);
            }

        } catch (Exception ex) {
            LOGGER.error("Load Strabon exception: {}", ex);
            isCompleted = false;
        } finally {
            if (strabon != null) {
                strabon.close();
            }
        }
        long endNanoTime = System.nanoTime();
        LOGGER.info("Strabon Loading: Completed");
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }

    public void createPostgresDatabase(String template) throws IOException, InterruptedException {
        String[] postgresCreate = new String[]{postgresCreateDBPath, "-T", template, "-h", host, "-p", port.toString(), "-U", user, dbName};

        Process pr = Runtime.getRuntime().exec(postgresCreate);
        int createResult = pr.waitFor();
        if (createResult > 0) {
            String createCommand = StringUtils.join(postgresCreate, " ");
            LOGGER.error("PostgreSQL failed to createDB: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres start command: {}", createResult, createCommand);
        } else {
            if (Main.DEBUG_MESSAGES) {
                LOGGER.info("Postgres createdDB: {}", dbName);
            }
        }
    }

    public void dropPostgresDatabase() throws IOException, InterruptedException {

        String[] postgresCreate = new String[]{postgresDropDBPath, "-h", host, "-p", port.toString(), "-U", user, "--if-exists", dbName};

        Process pr = Runtime.getRuntime().exec(postgresCreate);
        int createResult = pr.waitFor();
        if (createResult > 0) {
            String createCommand = StringUtils.join(postgresCreate, " ");
            LOGGER.error("PostgreSQL failed to dropDB: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres start command: {}", createResult, createCommand);
        } else {
            if (Main.DEBUG_MESSAGES) {
                LOGGER.info("Postgres droppedDB: {}", dbName);
            }
        }
    }

}
