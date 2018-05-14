/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_setup;

import java.io.File;
import java.util.TreeMap;

/**
 *
 *
 */
public class Dataset_WGS84 {

    public static final File FOLDER = new File("../datasets_WGS84");

    public static TreeMap<String, File> getAll() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(FOLDER, "dbpedia.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getLinkedGeodata() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getGeonames() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GEONAMES_URI, new File(FOLDER, "geonames.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getSynthetic() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.DEFAULT, new File(FOLDER, "generator_512.nt"));
        return datasetMap;
    }

}