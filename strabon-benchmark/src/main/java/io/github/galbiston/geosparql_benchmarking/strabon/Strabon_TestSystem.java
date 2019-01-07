/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.galbiston.geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import io.github.galbiston.execution.QueryTask;
import io.github.galbiston.execution.TestSystem;
import io.github.galbiston.execution_results.QueryResult;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import org.apache.commons.lang.StringUtils;
import org.openrdf.query.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Strabon_TestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Strabon strabon;

    private final String dbName;
    private final String user;
    private final String password;
    private final Integer port;
    private final String host;
    private final String postgresIsReadyPath;
    private final String postgresPG_CTLPath;
    private final String postgresDataPath;

    /**
     * StrabonTestSystem must be closed once operations finished so that Strabon
     * connections can be closed.<br>
     * Otherwise execution thread will remain open.<br>
     * All TestSystems implement AutoCloseable so try-with-resources pattern can
     * be used.<br>
     *
     * @param dbName
     * @param user
     * @param password
     * @param port
     * @param host
     * @param postgresIsReadyPath
     * @param postgresPG_CTLPath
     * @param postgresDataPath
     * @throws Exception
     */
    public Strabon_TestSystem(String dbName, String user, String password, Integer port, String host, String postgresIsReadyPath, String postgresPG_CTLPath, String postgresDataPath) throws Exception {
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.port = port;
        this.host = host;
        this.postgresDataPath = postgresDataPath;
        this.postgresIsReadyPath = postgresIsReadyPath;
        this.postgresPG_CTLPath = postgresPG_CTLPath;

        restartPostgresService();
        strabon = new Strabon(dbName, user, password, port, host, true);
    }

    //Restart PostgreSQL and clear caches where possible.
    //Postgres Environment variables: create PGDATA -> 'path/PostreSQL/version/data' and add to PATH -> 'path/PostgreSQL/version/bin'.
    //Windows process did not recognise environment variables so absolute paths (with "" if path contains a space) is used.
    //Windows requires the PostrgreSQL service to be stopped if running (default installation option): net stop POSTGRESQL_SERVICE
    //POSTRGRESQL_SERVICE can be found in Task Manager:Services - e.g. postgresql-x64-10
    //Postgres commands run directly in PowerShell for testing need '& ' at the start.
    private void restartPostgresService() {

        try {
            stopPostgres();
            clearCache();
            startPostgres();
        } catch (IOException | InterruptedException ex) {
            LOGGER.error("Strabon Restart Error: {}", ex.getMessage());
        }
    }

    public void stopPostgres() throws IOException, InterruptedException {
        int readyResult = checkPostgresReady();
        if (readyResult == 0) {
            //Stop Postgresql
            String[] postgresStop = {postgresPG_CTLPath, "stop", "-s", "-w", "-m", "fast"};
            Process pr = Runtime.getRuntime().exec(postgresStop);
            int stopResult = pr.waitFor();

            if (stopResult > 0) {
                String stopCommand = StringUtils.join(postgresStop, " ");
                LOGGER.error("PostgreSQL failed to stop: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres stop command: {}", stopResult, stopCommand);
            } else {
                LOGGER.debug("PostgreSQL stopped");
            }
        }
    }

    public int checkPostgresReady() throws IOException, InterruptedException {
        String[] postgresReady = {postgresIsReadyPath, "-h", host, "-p", port.toString()};
        Process pr = Runtime.getRuntime().exec(postgresReady);
        int readyResult = pr.waitFor();

        String isReadyCommand = StringUtils.join(postgresReady, " ");
        String readyMessage;
        switch (readyResult) {
            case 0:
                readyMessage = "0: PostgreSQL server is already running and accepting connections.";
                break;
            case 1:
                readyMessage = "1: PostgreSQL server is already running but rejected connection. Possibly due to starting up.";
                break;
            case 2:
                readyMessage = "2: PostgreSQL server did not respond so assumed to be not running.";
                break;
            case 3:
                readyMessage = "3: No attempt made to connect due to invalid parameters. " + isReadyCommand;
                break;
            default:
                readyMessage = readyResult + ": Unknown PostgreSQL result. Refer to documentation for version at: https://www.postgresql.org/docs/10/static/app-pg-isready.html";
                break;
        }

        if (readyResult < 3) {
            LOGGER.debug("PostgreSQL Ready Result: {}", readyMessage);
        } else {
            LOGGER.warn("PostgreSQL Ready Result: {}", readyMessage);
        }
        return readyResult;
    }

    protected void clearCache() throws IOException, InterruptedException {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("nix") | osName.contains("nux") | osName.contains("aux")) {
            //No documentation found to clear other OS caches. Mention of a 'purge' command for OSX.
            String[] dropCaches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
            Process pr = Runtime.getRuntime().exec(dropCaches);
            int cacheDropResult = pr.waitFor();
            if (cacheDropResult > 0) {
                String dropCacheCommand = StringUtils.join(dropCaches, " ");
                LOGGER.error("Dropping caches failed: Exit Value - {}. Drop cache command: {}", pr.exitValue(), dropCacheCommand);
            }
        }
    }

    public void startPostgres() throws IOException, InterruptedException {
        String[] postgresStart;
        if (postgresDataPath.isEmpty()) {
            postgresStart = new String[]{postgresPG_CTLPath, "start", "-w", "-o", "\"-h " + host + "\"", "-o", "\"-p " + port + "\""};
        } else {
            postgresStart = new String[]{postgresPG_CTLPath, "start", "-w", "-o", "\"-h " + host + "\"", "-o", "\"-p " + port + "\"", "-D", postgresDataPath};
        }

        Process pr = Runtime.getRuntime().exec(postgresStart);
        int startResult = pr.waitFor();
        if (startResult > 0) {
            String startCommand = StringUtils.join(postgresStart, " ");
            LOGGER.error("PostgreSQL failed to start: Exit Value - {}. Absolute path to PostgreSQL bin and data folders may be required. Postgres start command: {}", startResult, startCommand);
            LOGGER.error("If PostgreSQL did not stop or start correctly and the benchmark did not run then try executing again as the issues may clear on second attempt.");
        } else {
            LOGGER.debug("Postgres started");
        }
    }

    public Strabon getStrabon() {
        return strabon;
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

    public String getPostgresIsReadyPath() {
        return postgresIsReadyPath;
    }

    public String getPostgresPG_CTLPath() {
        return postgresPG_CTLPath;
    }

    public String getPostgresDataPath() {
        return postgresDataPath;
    }

    @Override
    public QueryTask getQueryTask(String query) {
        return new Strabon_QueryTask(query, strabon);
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

        try {
            strabon.close();
            stopPostgres();
            System.gc();
            Thread.sleep(5000); //Sleep for 5s to allow any Operating System clearing.
            LOGGER.debug("Strabon Closed");
        } catch (IOException | InterruptedException | RuntimeException ex) {
            LOGGER.error("Exception closing Strabon: {}", ex.getMessage());
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

}
