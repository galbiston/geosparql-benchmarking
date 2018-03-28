package gr.uoa.di.rdf.Geographica.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunStrabon extends RunSystemUnderTest {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected void addOptions() {
        super.addOptions();

        options.addOption("h", "host", true, "Server");
        options.addOption("db", "database", true, "Database");
        options.addOption("p", "port", true, "Port");
        options.addOption("u", "username", true, "Username");
        options.addOption("P", "password", true, "Password");
    }

    @Override
    protected void logOptions() {
        super.logOptions();

        LOGGER.info("Excluded options");
        LOGGER.info("Server:\t{}", cmd.getOptionValue("host"));
        LOGGER.info("Database:\t{}", cmd.getOptionValue("database"));
        LOGGER.info("Port:\t{}", cmd.getOptionValue("port"));
        LOGGER.info("Username:\t{}", cmd.getOptionValue("username"));
        LOGGER.info("Password:\t{}", cmd.getOptionValue("password"));
    }

    @Override
    protected void initSystemUnderTest() throws Exception {
        String host = (cmd.getOptionValue("host") != null ? cmd.getOptionValue("host") : "localhost");
        String db = cmd.getOptionValue("database");
        int port = (cmd.getOptionValue("port") != null ? Integer.parseInt(cmd.getOptionValue("port")) : 1521);
        String user = cmd.getOptionValue("username");
        String password = cmd.getOptionValue("password");

        sut = new StrabonSUT(db, user, password, port, host);
    }

    public static void main(String[] args) throws Exception {
        RunSystemUnderTest runStrabon = new RunStrabon();

        runStrabon.run(args);
    }

    public static void loadStrabon(HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
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

            for (Entry<String, File> entry : datasetMap.entrySet()) {
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

    public static void runBenchmark(File resultsFolder, Integer runtime, Integer timeout, List<String> queryList) {

        for (String query : queryList) {
            try {
                LOGGER.info("Strabon Benchmark - {}: Started", query);
                String[] experimentArgs = {"--logpath", resultsFolder.getAbsolutePath(), "--runtime", runtime.toString(), "--timeout", timeout.toString(), "run", query};
                RunStrabon.main(experimentArgs);
                LOGGER.info("Strabon Benchmark - {}: Completed", query);
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex);
            }
        }

    }

}
