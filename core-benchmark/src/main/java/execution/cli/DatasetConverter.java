/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution.cli;

import com.beust.jcommander.IStringConverter;
import data_setup.GraphURI;
import execution.DatasetInfo;
import java.io.File;
import java.util.TreeMap;

/**
 *
 *
 */
public class DatasetConverter implements IStringConverter<DatasetInfo> {

    @Override
    public DatasetInfo convert(String datasetName) {

        switch (datasetName.toLowerCase()) {
            case "greekgrid":
                return DatasetInfo.GREEK_GRID;
            case "wgs84":
                return DatasetInfo.GREEK_GRID;
            case "wgs84_legacy":
                return DatasetInfo.GREEK_GRID;
            case "crs84":
                return DatasetInfo.GREEK_GRID;
            case "conformance":
                return DatasetInfo.GREEK_GRID;
            default:
                File fileArg = new File(datasetName);
                if (fileArg.exists()) {
                    File[] files;
                    if (fileArg.isDirectory()) {
                        files = fileArg.listFiles();
                    } else {
                        files = new File[]{fileArg};
                    }
                    TreeMap<String, File> datasetMap = new TreeMap<>();
                    for (File file : files) {
                        datasetMap.put(GraphURI.USER + file.getName(), file);
                    }

                    return new DatasetInfo("User: " + fileArg.getName(), datasetMap);

                } else {
                    throw new IllegalArgumentException("Unknown Data Set: " + datasetName + ". Expected 'GreekGrid', 'WGS84', 'WGS84_Legacy', 'CRS84' or a file/folder path to load.");
                }

        }

    }
}
