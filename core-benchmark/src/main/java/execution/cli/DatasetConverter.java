/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
