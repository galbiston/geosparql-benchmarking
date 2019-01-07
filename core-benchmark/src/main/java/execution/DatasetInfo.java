/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import data_setup.Dataset_CRS84;
import data_setup.Dataset_Conformance;
import data_setup.Dataset_Greek_Grid;
import data_setup.Dataset_WGS84;
import data_setup.Dataset_WGS84_Legacy;
import java.io.File;
import java.util.TreeMap;

/**
 *
 *
 */
public class DatasetInfo {

    private String datasetName;
    private TreeMap<String, File> datasetMap;

    public DatasetInfo(String datasetName, TreeMap<String, File> datasetMap) {
        this.datasetName = datasetName;
        this.datasetMap = datasetMap;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public TreeMap<String, File> getDatasetMap() {
        return datasetMap;
    }

    @Override
    public String toString() {
        return "DatasetInfo{" + "datasetName=" + datasetName + ", datasetMap=" + datasetMap + '}';
    }

    public static final DatasetInfo WGS_84 = new DatasetInfo("WGS84", Dataset_WGS84.getAll());
    public static final DatasetInfo GREEK_GRID = new DatasetInfo("GreekGrid", Dataset_Greek_Grid.getAll());
    public static final DatasetInfo WGS_84_LEGACY = new DatasetInfo("WG84 Legacy", Dataset_WGS84_Legacy.getAll());
    public static final DatasetInfo CRS_84 = new DatasetInfo("CRS84", Dataset_CRS84.getAll());
    public static final DatasetInfo CONFORMANCE = new DatasetInfo("Conformance", Dataset_Conformance.getConformanceData());

}
