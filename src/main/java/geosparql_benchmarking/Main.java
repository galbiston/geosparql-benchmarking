package geosparql_benchmarking;

import static gr.uoa.di.rdf.Geographica.parliament.RunParliament.loadParliament;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException {

        HashMap<String, File> datasetMap = getDatasets();
        //loadGeosparqlJena(GEOSPARQL_JENA_TDB_FOLDER, datasetMap);
        loadParliament(datasetMap);
        //loadStrabon(datasetMap);

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
        //Create results directory structure.
        createResultsFolders();

        //Build experiment arguements.
        String[] experimentArgs = {"--logpath", GEOSPARQL_JENA_RESULTS.getAbsolutePath(), "--runtime", "120", "--timeout", "3600", "run", "MicroNonTopological"};

        //Run the experiments using the arguements.
        try {
            //rdfsTest();
            // RunGeosparqlJena.main(experimentArgs);
        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex);
        }
    }
    private static final File DATASET_FOLDER = new File("datasets");

    private static void createResultsFolders() {
        RESULTS_FOLDER.mkdir();
        GEOSPARQL_JENA_RESULTS.mkdir();
        PARLIAMENT_RESULTS.mkdir();
        STRABON_RESULTS.mkdir();
    }

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

    //The Jena TDB Folder and Graph URIs need to be stated in the Assembly File.
    public static final File GEOSPARQL_JENA_TDB_ASSEMBLY_FILE = new File(Main.class.getClassLoader().getResource("geosparql-assemble.ttl").getPath());
    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");

    public static final File RESULTS_FOLDER = new File("results");
    public static final File GEOSPARQL_JENA_RESULTS = new File(RESULTS_FOLDER, "geosparql_jena");
    public static final File PARLIAMENT_RESULTS = new File(RESULTS_FOLDER, "parliament");
    public static final File STRABON_RESULTS = new File(RESULTS_FOLDER, "strabon");

    private static final void rdfsTest() {
        Dataset dataset = TDBFactory.assembleDataset(GEOSPARQL_JENA_TDB_ASSEMBLY_FILE.getAbsolutePath());
//<http://www.opengis.net/ont/geosparql#asWKT>
//<http://linkedgeodata.org/ontology/asWKT>
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <http://geographica.di.uoa.gr/dataset/lgd2> { ?sub <http://www.opengis.net/ont/geosparql#asWKT> ?obj}}LIMIT 100";

        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }

    }

}
