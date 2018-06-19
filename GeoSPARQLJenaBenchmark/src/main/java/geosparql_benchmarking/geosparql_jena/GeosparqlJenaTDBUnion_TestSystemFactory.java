/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import execution.TestSystem;
import implementation.index.IndexConfiguration.IndexOption;
import java.io.File;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Applies the global setting for TDB to create a union of all named graphs as
 * the default graph.<br>
 * Requires queries that do not use named graphs.
 */
public class GeosparqlJenaTDBUnion_TestSystemFactory extends GeosparqlJenaTDB_TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "GeoSparqlJenaTDB_Union";

    public GeosparqlJenaTDBUnion_TestSystemFactory(File datasetFolder, String resultsFolder, Boolean inferenceEnabled) {
        super(datasetFolder, resultsFolder, inferenceEnabled, true);
    }

    @Override
    public TestSystem getTestSystem() {
        return new GeosparqlJenaTestSystem(datasetFolder, IndexOption.NONE);
    }

    @Override
    public String getTestSystemName() {
        return TEST_SYSTEM_NAME;
    }

}
