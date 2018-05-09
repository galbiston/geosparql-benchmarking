package data_setup;

import java.io.File;
import java.util.TreeMap;

public class DatasetSources {

    public static final File DATASET_WGS84_LEGACY_FOLDER = new File("../datasets_WGS84Legacy");

    public static TreeMap<String, File> getWGS84LegacyDatasets() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    public static TreeMap<String, File> getWGS84LegacyTestDatasets() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getWGS84LegacySyntheticDataset() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.DEFAULT, new File(DATASET_WGS84_LEGACY_FOLDER, "generator512.nt"));
        return datasetMap;
    }

    public static final File DATASET_CRS84_FOLDER = new File("../datasets_CRS84");

    public static TreeMap<String, File> getCRS84Datasets() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_CRS84_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_CRS84_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_CRS84_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_CRS84_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_CRS84_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_CRS84_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    public static TreeMap<String, File> getCRS84TestDatasets() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_CRS84_FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getCRS84SyntheticDataset() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.DEFAULT, new File(DATASET_CRS84_FOLDER, "generator512.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getWGS84LegacyDataset_Geonames() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_WGS84_LEGACY_FOLDER, "geonames.nt"));

        return datasetMap;
    }

}
