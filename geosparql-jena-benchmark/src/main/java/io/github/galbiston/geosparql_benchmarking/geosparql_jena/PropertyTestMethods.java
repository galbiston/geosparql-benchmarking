/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.galbiston.geosparql_benchmarking.geosparql_jena;

import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import io.github.galbiston.geosparql_jena.configuration.GeoSPARQLOperations;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.InfModel;

/**
 *
 *
 */
public class PropertyTestMethods {

//    private static final InfModel SAMPLE_DATA_MODEL = TestQuerySupport.getSampleData_WKT();

    private static final String BOTH_BOUND_QUERY_FILE = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n" +
"PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
"\n" +
"SELECT ?geom\n" +
"WHERE{\n" +
"    <#subject#> #property# <#object#> .\n" +
"    BIND(<#object#> AS ?geom) .\n" +
"}ORDER by ?geom";
            //"BothBoundPropertyQuery.spl";
    //drew private static final String BOTH_BOUND_QUERY = QueryLoader.readResource(BOTH_BOUND_QUERY_FILE);

    private static final String UNBOUND_QUERY_FILE = "UnboundObjectPropertyQuery.spl";
    //drew private static final String UNBOUND_QUERY = QueryLoader.readResource(UNBOUND_QUERY_FILE);

    private static final String SUBJECT_REPLACEMENT = "#subject#";
    private static final String PROPERTY_NAME_REPLACEMENT = "#property#";
    private static final String OBJECT_REPLACEMENT = "#object#";

    public static final String runBoundQuery(String subject, String propertyName, String object) {
        String queryString = BOTH_BOUND_QUERY_FILE.replace(SUBJECT_REPLACEMENT, subject).replace(PROPERTY_NAME_REPLACEMENT, propertyName).replace(OBJECT_REPLACEMENT, object);
        queryString = StringEscapeUtils.escapeHtml4(queryString);
        System.out.println(queryString);
        return null; 
    }

    public static final List<String> runUnboundQuery(String source, String propertyName) {
        String queryString = UNBOUND_QUERY_FILE.replace(SUBJECT_REPLACEMENT, source).replace(PROPERTY_NAME_REPLACEMENT, propertyName);
        queryString = StringEscapeUtils.escapeHtml4(queryString);
        System.out.println(queryString);
        return null;   
    }

    public static void docreate() {
        System.out.println("sfContains Bound Positive");
        String expResult = "http://example.org/Geometry#PointA";
        String result = runBoundQuery("http://example.org/Geometry#PolygonH", "geo:sfContains", "http://example.org/Geometry#PointA");

        //System.out.println("Exp: " + expResult);

    }
}
