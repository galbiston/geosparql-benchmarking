/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_setup;

import execution.BenchmarkExecution;
import execution.TestSystem;
import execution_results.QueryResult;
import execution_results.VarValue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Geographica uses randomly selected and generated data that are re-used
 * between macro queries of the same type. These methods generate this data and
 * store in files for consistency between queries and all them to be reproduced.
 *
 */
public class DataGeneration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final void storeAllGeonames(TestSystem testSystem, File outputFile) {
        double xHalfWidth = 0.03;
        double yHalfHeight = 0.02;
        storeAllGeonames(xHalfWidth, yHalfHeight, testSystem, outputFile);
    }

    public static final void storeAllGeonames(double xHalfWidth, double yHalfHeight, TestSystem testSystem, File outputFile) {

        try {
            String queryString = "PREFIX dataset: <http://geographica.di.uoa.gr/dataset/>\n"
                    + "PREFIX geonames: <http://www.geonames.org/ontology#>\n"
                    + ""
                    + "SELECT ?name ?wkt\n"
                    + "WHERE { \n"
                    + "	GRAPH dataset:geonames { \n"
                    + "     ?f geonames:name ?name.\n"
                    + "     ?f geonames:hasGeometry ?geo.\n"
                    + "     ?geo geonames:asWKT ?wkt.\n"
                    + "	}\n"
                    + "}";
            LOGGER.info("Retrieving Geonames and Points: Started");
            QueryResult queryResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofSeconds(3600));
            List<List<VarValue>> results = queryResult.getResults();
            LOGGER.info("Retrieving Geonames and Points: Completed");
            LOGGER.info("Writing Geonames: Started - {}", outputFile);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                for (List<VarValue> result : results) {

                    String label = result.get(0).getValue();
                    String geometry = result.get(1).getValue();
                    String uri = getURI(geometry);
                    double xCoord = getXcoord(geometry);
                    double yCoord = getYcoord(geometry);
                    String box = buildBox(xCoord, yCoord, xHalfWidth, yHalfHeight);
                    writer.write(label + "\t" + uri + box);
                    writer.newLine();
                }
            }
            LOGGER.info("Writing Geonames: Completed - {}", outputFile);

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }
    }

    private static double getXcoord(String geometry) {

        int start = geometry.indexOf("(") + 1;
        int mid = geometry.indexOf(" ", start);
        String value = geometry.substring(start, mid);
        return Double.parseDouble(value);
    }

    private static double getYcoord(String geometry) {
        int start = geometry.indexOf("(") + 1;
        int mid = geometry.indexOf(" ", start);
        int end = geometry.indexOf(")", mid);

        String value = geometry.substring(mid, end);
        return Double.parseDouble(value);
    }

    private static String getURI(String geometry) {
        int start = geometry.indexOf("<");
        int end = geometry.indexOf(">");

        if (start == -1) {
            return "";
        }
        return geometry.substring(start, end + 1) + " ";
    }

    private static String buildBox(double xCoord, double yCoord, double xHalfWidth, double yHalfHeight) {

        BigDecimal xMin = new BigDecimal(xCoord - xHalfWidth).setScale(3, RoundingMode.HALF_UP);
        BigDecimal xMax = new BigDecimal(xCoord + xHalfWidth).setScale(3, RoundingMode.HALF_UP);
        BigDecimal yMin = new BigDecimal(yCoord - yHalfHeight).setScale(3, RoundingMode.HALF_UP);
        BigDecimal yMax = new BigDecimal(yCoord + yHalfHeight).setScale(3, RoundingMode.HALF_UP);

        return "POLYGON((" + xMin.toPlainString() + " " + yMin.toPlainString() + ", " + xMin.toPlainString() + " " + yMax.toPlainString() + ", " + xMax.toPlainString() + " " + yMax.toPlainString() + ", " + xMax.toPlainString() + " " + yMin.toPlainString() + ", " + xMin.toPlainString() + " " + yMin.toPlainString() + "))";

    }

    public static final void generateWKTPoints(double xMin, double xMax, double yMin, double yMax, String outputSrsURI, int iterations, File outputFile) {

        Random rand = new Random();
        LOGGER.info("Generating Points: Started");
        List<String> points = new ArrayList<>(iterations);
        double xDiff = xMax - xMin;
        double yDiff = yMax - yMin;
        for (int i = 0; i < iterations; i++) {

            BigDecimal xCoord = new BigDecimal(xMin + (xDiff * rand.nextDouble())).setScale(3, RoundingMode.HALF_UP);
            BigDecimal yCoord = new BigDecimal(yMin + (yDiff * rand.nextDouble())).setScale(3, RoundingMode.HALF_UP);
            String point = i + "\t<" + outputSrsURI + "> POINT(" + xCoord.toPlainString() + " " + yCoord.toPlainString() + ")";
            points.add(point);
        }
        LOGGER.info("Generating Points: Completed");

        LOGGER.info("Writing Points: Started - {}", outputFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String point : points) {
                writer.write(point);
                writer.newLine();
            }
        } catch (IOException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }
        LOGGER.info("Writing Points: Completed - {}", outputFile);

    }

    public static final void generateGeographicaPoint(int iterations, File outputFile) {
        //Geographica value using in MacroReverseGeocodingQueriesSet
        double xMin = 20.7861328125;
        double xMax = 22.9833984375;
        double yMin = 37.705078125;
        double yMax = 39.990234375;

        String outputSrsURI = "http://www.opengis.net/def/crs/EPSG/4326";
        DataGeneration.generateWKTPoints(xMin, xMax, yMin, yMax, outputSrsURI, iterations, outputFile);

    }

}
