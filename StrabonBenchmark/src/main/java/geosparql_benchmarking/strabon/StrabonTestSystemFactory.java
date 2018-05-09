/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import execution.BenchmarkExecution;
import execution_results.DatasetLoadResult;
import execution_results.DatasetLoadTimeResult;
import execution.TestSystem;
import execution.TestSystemFactory;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class StrabonTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String TEST_SYSTEM_NAME = "Strabon";

    private final String dbName;
    private final String user;
    private final String password;
    private final Integer port;
    private final String host;
    private final File resultsFolder;
    private final Boolean inferenceEnabled;
    private final String baseURI;
    private final String format;
    private final String postgresBinPath;
    private final String postgresDataPath;
    private final String postgresIsReadyPath;
    private final String postgresPG_CTLPath;
    private final String postgresCreateDBPath;
    private final String postgresDropDBPath;
    private final String databaseTemplate;

    public StrabonTestSystemFactory(String dbName, String user, String password, Integer port, String host, String resultsFolder, Boolean inferenceEnabled, String baseURI, String format, String postgresBinPath, String postgresDataPath, String databaseTemplate) {
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.port = port;
        this.host = host;
        this.inferenceEnabled = inferenceEnabled;
        this.baseURI = baseURI;
        this.format = format;

        this.resultsFolder = new File(BenchmarkExecution.RESULTS_FOLDER, resultsFolder);
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
        this.databaseTemplate = databaseTemplate;
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

    public Boolean getInferenceEnabled() {
        return inferenceEnabled;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public String getFormat() {
        return format;
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

    public String getDatabaseTemplate() {
        return databaseTemplate;
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

    @Override
    public Boolean clearDataset() {
        return clearDataset(this);
    }

    @Override
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration) {
        return loadDataset(datasetMap, this, iteration);
    }

    @Override
    public Boolean clearLoadDataset(TreeMap<String, File> datasetMap) {

        Boolean isClear = clearDataset();
        if (isClear) {
            DatasetLoadResult loadResult = loadDataset(datasetMap, 0);
            return loadResult.getIsCompleted();
        }
        return isClear;
    }

    /**
     * Removes the entire existing database and uses the database template to
     * provide a fresh database.
     *
     * @param testSystemFactory
     * @return
     */
    public static Boolean clearDataset(StrabonTestSystemFactory testSystemFactory) {
        try {
            testSystemFactory.dropPostgresDatabase();
            testSystemFactory.createPostgresDatabase();
            return true;
        } catch (IOException | InterruptedException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Loads the dataset into the target location. No clearing of the dataset is
     * done before this and may be recommended.
     *
     * @param datasetMap
     * @param testSystemFactory
     * @return
     */
    public static DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, StrabonTestSystemFactory testSystemFactory) {
        return loadDataset(datasetMap, testSystemFactory, 0);
    }

    private static DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, StrabonTestSystemFactory testSystemFactory, Integer iteration) {
        LOGGER.info("Strabon Loading: Started");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();
        Strabon strabon = null;
        try {
            String dbName = testSystemFactory.dbName;
            String user = testSystemFactory.user; //String user = "postgres";
            String password = testSystemFactory.password; //String passwd = "postgres";
            Integer port = testSystemFactory.port;
            String host = testSystemFactory.host; //"localhost"; //"127.0.0.1"
            Boolean checkForLockTable = true;
            strabon = new Strabon(dbName, user, password, port, host, checkForLockTable);

            Boolean inferenceEnabled = testSystemFactory.inferenceEnabled;
            String baseURI = testSystemFactory.baseURI;
            String format = testSystemFactory.format;

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
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, iteration, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }

    public void createPostgresDatabase() throws IOException, InterruptedException {
        createPostgresDatabase(databaseTemplate);
    }

    public void createPostgresDatabase(String templateDatabase) throws IOException, InterruptedException {
        String[] postgresCreate = new String[]{postgresCreateDBPath, "-T", templateDatabase, "-h", host, "-p", port.toString(), "-U", user, dbName};

        Process pr = Runtime.getRuntime().exec(postgresCreate);
        int createResult = pr.waitFor();
        if (createResult > 0) {
            String createCommand = StringUtils.join(postgresCreate, " ");
            LOGGER.error("PostgreSQL failed to createDB: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres start command: {}", createResult, createCommand);
        } else {
            LOGGER.debug("Postgres createdDB: {}", dbName);
        }
    }

    public void dropPostgresDatabase() throws IOException, InterruptedException {

        String[] postgresDrop = new String[]{postgresDropDBPath, "-h", host, "-p", port.toString(), "-U", user, "--if-exists", dbName};

        Process pr = Runtime.getRuntime().exec(postgresDrop);
        int dropResult = pr.waitFor();
        if (dropResult > 0) {
            String dropCommand = StringUtils.join(postgresDrop, " ");
            LOGGER.error("PostgreSQL failed to dropDB: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres start command: {}", dropResult, dropCommand);
        } else {
            LOGGER.debug("Postgres droppedDB: {}", dbName);
        }
    }

}
