/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conformance_test.query_rewrite_extension.rcc8;

import conformance_test.query_rewrite_extension.QueryRewriteTestMethods;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 *
 *
 * A.6.3.1 /conf/query-rewrite-extension/rcc8-query-rewrite
 *
 * Requirement: /req/query-rewrite-extension/rcc8-query-rewrite Basic graph
 * pattern matching shall use the semantics defined by the RIF Core Entailment
 * Regime [W3C SPARQL Entailment] for the RIF rules [W3C RIF Core] geor:rcc8eq,
 * geor:rcc8dc, geor:rcc8ec, geor:rcc8po, geor:rcc8tppi, geor:rcc8tpp,
 * geor:rcc8ntpp, geor:rcc8ntppi.
 *
 * a.) Test purpose: check conformance with this requirement
 *
 * b.) Test method: Verify that queries involving the following query
 * transformation rules return the correct result for a test dataset when using
 * the specified serialization and version: geor:rcc8eq, geor:rcc8dc,
 * geor:rcc8ec, geor:rcc8po, geor:rcc8tppi, geor:rcc8tpp, geor:rcc8ntpp,
 * geor:rcc8ntppi.
 *
 * c.) Reference: Clause 11.4 Req 30
 *
 * d.) Test Type: Capabilities
 */
public class Rcc8tppTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    /**
     * Every point of a is a point of b, and the interiors of the two geometries
     * have at least one point in common.
     */
    @Test
    public void rcc8tppBothBoundTest() {

        System.out.println("rcc8tpp Both Bound");
        String expResult = "http://example.org/Geometry#PolygonH";
        String result = QueryRewriteTestMethods.runBothBoundQuery("http://example.org/Geometry#PolygonK", "geo:rcc8tpp", "http://example.org/Geometry#PolygonH");

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Every point of a is a point of b, and the interiors of the two geometries
     * have at least one point in common.
     */
    @Test
    public void rcc8tppUnboundSubjectTest() {

        System.out.println("rcc8tpp Unbound Subject");
        List<String> expResult = new ArrayList<>();
        expResult.add("http://example.org/Feature#K");
        expResult.add("http://example.org/Geometry#PolygonK");

        List<String> result = QueryRewriteTestMethods.runUnboundSubjectQuery("geo:rcc8tpp", "http://example.org/Geometry#PolygonH");

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Every point of a is a point of b, and the interiors of the two geometries
     * have at least one point in common.
     */
    @Test
    public void rcc8tppUnboundObjectTest() {

        System.out.println("rcc8tpp Unbound Object");
        List<String> expResult = new ArrayList<>();
        expResult.add("http://example.org/Feature#H");
        expResult.add("http://example.org/Geometry#PolygonH");

        List<String> result = QueryRewriteTestMethods.runUnboundObjectQuery("http://example.org/Geometry#PolygonK", "geo:rcc8tpp");

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Every point of a is a point of b, and the interiors of the two geometries
     * have at least one point in common.
     */
    @Ignore
    @Test
    public void rcc8tppBothUnboundTest() {

        System.out.println("rcc8tpp Both Unbound");
        List<String> expResult = new ArrayList<>();

        List<String> result = QueryRewriteTestMethods.runBothUnboundQuery("geo:rcc8tpp");

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Every point of a is a point of b, and the interiors of the two geometries
     * have at least one point in common.
     */
    @Test
    public void rcc8tppAssertTest() {

        System.out.println("rcc8tpp Assert Test");

        Boolean expResult = true;
        Boolean result = QueryRewriteTestMethods.runAssertQuery("geo:rcc8tpp");

        //System.out.println("Exp: " + expResult);
        //System.out.println("Res: " + result);
        assertEquals(expResult, result);
    }

}
