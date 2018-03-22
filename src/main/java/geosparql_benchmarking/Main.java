package geosparql_benchmarking;

import eu.earthobservatory.runtime.postgis.Strabon;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Run GeoSPARQL Compliance Testing
        //1) Serialisation - WKT, GML, mixed
        //2) RDFS Entailment
        //3) Function coverage
        //4) Property retrieval and generation
        //5) Units of measure in function - conversion
        //6) Coordinate reference system - conversion
        //Run Geographica Benchmarks
        //1) Apache Jena Extension
        //2) Parliament
        //3) Strabon
        loadStrabon();
    }

    private static void loadStrabon() {
        Strabon strabon = null;
        try {
            String db = "endpoint";
            String user = "postgres";
            String passwd = "postgres";
            Integer port = 5432;
            String host = "localhost"; //"localhost"
            Boolean checkForLockTable = true;
            strabon = new Strabon(db, user, passwd, port, host, checkForLockTable);

            String src = new File("datasets/gag.nt").getAbsolutePath();
            String baseURI = null;
            String graph = GraphURI.GADM_URI;
            String format = "NTRIPLES";
            Boolean inference = false;
            LOGGER.info("Loading: {} into {}: Started", src, graph);
            strabon.storeInRepo(src, baseURI, graph, format, inference);
            LOGGER.info("Loading: {} into {}: Completed", src, graph);
        } catch (Exception ex) {
            LOGGER.error("Load Strabon: {}", ex.getMessage());
        } finally {
            if (strabon != null) {
                strabon.close();
            }
        }
    }
}
