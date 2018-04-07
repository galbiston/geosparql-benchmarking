package geosparql_benchmarking;

import java.io.File;
import java.util.HashMap;

public class DatasetSources {

    public static final File DATASET_FOLDER = new File("../datasets");

    public static HashMap<String, File> getDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    public static HashMap<String, File> getTestDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    public static final File DATASET_FIXED_FOLDER = new File("../datasets_fixed");

    public static HashMap<String, File> getFixedDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_FIXED_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FIXED_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_FIXED_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_FIXED_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_FIXED_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_FIXED_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    

}
