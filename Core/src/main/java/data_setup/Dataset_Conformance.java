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
public class Dataset_Conformance {

    public static final File FOLDER = new File("../geosparql_conformance_dataset");

    public static TreeMap<String, File> getConformanceData() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.CONFORMANCE_URI, new File(FOLDER, "geosparql_conformance.rdf"));
        return datasetMap;
    }

}
