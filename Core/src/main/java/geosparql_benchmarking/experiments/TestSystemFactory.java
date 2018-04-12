/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.io.File;
import java.util.TreeMap;

/**
 *
 * @author Gerg
 */
public interface TestSystemFactory {

    public TestSystem getTestSystem();

    public String getTestSystemName();

    public File getResultsFolder();

    /**
     * Loads the dataset into the target location. No clearing of the dataset is
     * done before this and may be recommended.
     *
     * @param datasetMap
     * @param iteration
     * @return
     */
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration);

    /**
     * Clears the entire contents of the dataset.
     *
     * @return
     */
    public Boolean clearDataset();

    /**
     * Clear the entire contents of the dataset before loading fresh data.
     *
     * @param datasetMap
     * @return
     */
    public Boolean clearLoadDataset(TreeMap<String, File> datasetMap);
}
