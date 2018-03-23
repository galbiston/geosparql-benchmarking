package geosparql_benchmarking;

import eu.earthobservatory.runtime.postgis.Strabon;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        HashMap<String, File> datasetMap = getDatasets();
        loadGeosparqlJena(GEOSPARQL_JENA_TDB_FOLDER, datasetMap);
        //loadStrabon();

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
    }
    private static final File DATASET_FOLDER = new File("datasets");

    private static HashMap<String, File> getDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");

    private static void loadGeosparqlJena(File datasetFolder, HashMap<String, File> datasetMap) {
        LOGGER.info("Geosparql Jena Loading: Started");
        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());

        for (Entry<String, File> entry : datasetMap.entrySet()) {
            try {
                dataset.begin(ReadWrite.WRITE);
                String sourceRDFFile = entry.getValue().getAbsolutePath();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graph);
                Model model = RDFDataMgr.loadModel(sourceRDFFile);
                dataset.addNamedModel(graph, model);
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graph);
                dataset.commit();

            } catch (RuntimeException ex) {
                LOGGER.error("TDB Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }
        dataset.close();
        TDBFactory.release(dataset);

        LOGGER.info("Geosparql Jena Loading: Completed");
    }

    private static void loadStrabon(HashMap<String, File> datasetMap) {
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

            String src = new File("datasets/gag.nt").getAbsolutePath();
            String baseURI = null;
            String graph = GraphURI.GADM_URI;
            String format = "NTRIPLES";
            Boolean inference = false;
            strabon.storeInRepo(src, baseURI, graph, format, inference);

            /*
            for (Entry<String, File> entry : datasetMap.entrySet()) {
                String src = entry.getValue().getAbsolutePath();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", src, graph);
                strabon.storeInRepo(src, baseURI, graph, format, inference);
                LOGGER.info("Loading: {} into {}: Completed", src, graph);
            }
             */
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
