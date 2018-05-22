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
public class Dataset_Compliance {

    public static final File FOLDER = new File("../compliance_dataset");

    public static TreeMap<String, File> getComplianceData() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.COMPLIANCE_URI, new File(FOLDER, "compliance.rdf"));
        return datasetMap;
    }

}
