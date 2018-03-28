package geosparql_benchmarking;

import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.hp.hpl.jena.graph.Node;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException {

        HashMap<String, File> datasetMap = getDatasets();
        Boolean inferenceEnabled = true;
        //RunGeosparqlJena.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        //RunParliament.loadDataset(datasetMap);
        //RunStrabon.loadDataset(datasetMap, inferenceEnabled);

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
        Integer runtime = 120;
        Integer timeout = 3600;
        List<String> queryList = getQueryList();

        //Run the experiments using the arguements.
        //rdfsGeosparqlJenaTest();
        //exportGeosparqlJenaTest();
        rdfsParliamentTest();
        exportParliamentTest();

        //Benchmark
        //RunGeosparqlJena.runBenchmark(GEOSPARQL_JENA_RESULTS, runtime, timeout, queryList);
        //RunParliament.runBenchmark(PARLIAMENT_RESULTS, runtime, timeout, queryList);
        //RunStrabon.runBenchmark(STRABON_RESULTS, runtime, timeout, queryList);
    }
    private static final File DATASET_FOLDER = new File("datasets");

    private static void createResultsFolders() {
        RESULTS_FOLDER.mkdir();
        GEOSPARQL_JENA_RESULTS.mkdir();
        PARLIAMENT_RESULTS.mkdir();
        STRABON_RESULTS.mkdir();
    }

    private static List<String> getQueryList() {
        List<String> queryList = new ArrayList<>();
        queryList.add("MicroNonTopological");
        return queryList;
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

    private static HashMap<String, File> getTestDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    //The Jena TDB Folder and Graph URIs need to be stated in the Assembly File.
    public static final File GEOSPARQL_JENA_TDB_ASSEMBLY_FILE = new File(Main.class.getClassLoader().getResource("geosparql-assemble.ttl").getPath());
    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");

    public static final File RESULTS_FOLDER = new File("results");
    public static final File GEOSPARQL_JENA_RESULTS = new File(RESULTS_FOLDER, "geosparql_jena");
    public static final File PARLIAMENT_RESULTS = new File(RESULTS_FOLDER, "parliament");
    public static final File STRABON_RESULTS = new File(RESULTS_FOLDER, "strabon");

    private static void rdfsGeosparqlJenaTest() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        Model unionModel = dataset.getUnionModel();
        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }

    }

    private static void exportGeosparqlJenaTest() {
        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        Model model = dataset.getNamedModel(GraphURI.LGD_URI);
        try (FileOutputStream out = new FileOutputStream(new File("lgd-jena.ttl"))) {
            RDFDataMgr.write(out, model, Lang.TTL);
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
    }

    private static void rdfsParliamentTest() {

        KbGraphStore graphStore = new KbGraphStore(KbGraphFactory.createDefaultGraph());
        graphStore.initialize();
        com.hp.hpl.jena.query.DataSource dataSource = com.hp.hpl.jena.query.DatasetFactory.create(graphStore);
        com.hp.hpl.jena.rdf.model.Model unionModel = com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(graphStore.getMasterGraph());
        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://www.opengis.net/ont/sf#>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try {
            com.hp.hpl.jena.query.QueryExecution qe = com.hp.hpl.jena.query.QueryExecutionFactory.create(queryString, dataSource);
            com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();
            com.hp.hpl.jena.query.ResultSetFormatter.outputAsCSV(rs);
            qe.close();
        } catch (Exception ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
        graphStore.close();
    }

    private static void exportParliamentTest() {

        KbGraphStore graphStore = new KbGraphStore(KbGraphFactory.createDefaultGraph());
        graphStore.initialize();
        Node graphNode = Node.createURI(GraphURI.LGD_URI);
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(graphStore.getGraph(graphNode));

        try (FileOutputStream out = new FileOutputStream(new File("lgd-parliament.ttl"))) {
            model.write(out, "TTL");
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
        graphStore.close();
    }

}
