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
package io.github.galbiston.geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import io.github.galbiston.geosparql_benchmarking.data_setup.GraphURI;
import io.github.galbiston.geosparql_benchmarking.execution.BenchmarkExecution;
import io.github.galbiston.geosparql_benchmarking.execution.TestSystem;
import io.github.galbiston.geosparql_benchmarking.execution.TestSystemFactory;
import io.github.galbiston.geosparql_benchmarking.execution_results.QueryResult;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.TreeMap;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class Main_Additional {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    private static void rdfsStrabonTest(Strabon_TestSystemFactory testSystemFactory) {

        //String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        //Strabon doesn't seem to apply RDFS inferencing even though it has a paraemter when data loading.
        //Geographica benchmarking paper (page 10) and running this query show it doesn't.
        try (Strabon_TestSystem strabonTestSystem = testSystemFactory.getStrabonTestSystem()) {
            Strabon strabon = strabonTestSystem.getStrabon();
            TupleQuery tupleQuery = (TupleQuery) strabon.query(queryString, Format.TUQU, strabon.getSailRepoConnection(), System.out);
            SPARQLResultsCSVWriter csvWriter = new SPARQLResultsCSVWriter(System.out);
            tupleQuery.evaluate(csvWriter);

        } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest(TestSystemFactory testSystemFactory) {

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT (23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(37.98 23.71)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTestA(TestSystemFactory testSystemFactory) {

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT (23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT(23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest2(TestSystemFactory testSystemFactory) {

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(0 0, 2 0, 5 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(5 0, 0 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest3(TestSystemFactory testSystemFactory) {

        //This query returns everything when it should just return LineStringD and LineStringD1.
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "<http://example.org/Geometry#LineStringD> geo:asWKT ?first ."
                + "?res geo:asWKT ?second ."
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}"
                + "}";
        /*
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "PREFIX geom: <http://example.org/Geometry#>"
                + "PREFIX sf: <http://www.opengis.net/ont/sf#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "?line a sf:LineString ."
                + "?line geo:asWKT ?first ."
                + "?res a sf:LineString ."
                + "?res geo:asWKT ?second ."
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}"
                + "}";
         */
        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest4(TestSystemFactory testSystemFactory) {

        //This query returns everything when it should just return LineStringD and LineStringD1.
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "?res geo:asWKT ?first ."
                + "}"
                + "GRAPH <http://example.org/dataset#conformance-equals>{"
                + "?secondGeo geo:asWKT ?second ."
                + "}"
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

}
