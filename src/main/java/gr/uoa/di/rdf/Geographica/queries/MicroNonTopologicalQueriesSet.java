/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.queries;

import geosparql_benchmarking.GraphURI;

import org.apache.log4j.Logger;
import geosparql_benchmarking.experiments.TestSystem;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 */
public class MicroNonTopologicalQueriesSet extends QueriesSet {
	
	static Logger logger = Logger.getLogger(MicroNonTopologicalQueriesSet.class.getSimpleName());
	
	private String queryGeosparqlTemplate = prefixes 
			+ " \n select (geof:FUNCTION(?o1) as ?ret) where { \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "} "
			;
	
	private String queryStsparqlTemplate = prefixes 
			+ " \n select (strdf:FUNCTION(?o1) as ?ret) where { \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "} "
			;

//	private String queryUseekmTemplate = prefixes 
//			+ " \n select (ext:FUNCTION(?o1) as ?ret) where { \n"
//			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
//			+ "} "
//			;
	
	private String queryBufferTemplate;
	
	public MicroNonTopologicalQueriesSet(TestSystem sut) {
		super(sut);
		
		queriesN = 6; // IMPORTANT: Add/remove queries in getQuery implies changing queriesN
			
		queryBufferTemplate = prefixes 
				+ " \n select (geof:buffer(?o1,4, <http://www.opengis.net/def/uom/OGC/1.0/metre>) as ?ret) where {"
				+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1}"
				+ "} "
				;
	}
	
	@Override
	public QueryStruct getQuery(int queryIndex, int repetition) {
		
		String query = null, label = null;
		
		// IMPORTANT: Add/remove queries in getQuery implies changing queriesN and changing case numbers
		switch (queryIndex) {
		case 0:		
			// Q3 Boundary of many simple Polygons
			label = "Boundary_CLC"; 
			query = queryGeosparqlTemplate;
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			query = query.replace("FUNCTION", "boundary");
			break;

		case 1:
			// Q5 Envelope many "simple" Polygons
			label = "Envelope_CLC"; 
			query = queryGeosparqlTemplate;
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			query = query.replace("FUNCTION", "envelope");
			break;

		case 2:
			// Q8 ConvexHull of many "simple" Polygons
			label = "ConvexHull_CLC"; 	
			query = queryGeosparqlTemplate;
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			query = query.replace("FUNCTION", "convexHull");
			break;

		case 3:
			// -- Buffer -- //
			// Q10 Buffer of Points
			label = "Buffer_GeoNames_2"; 
			query = queryBufferTemplate;
			query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
			query = query.replace("ASWKT1", geonames_asWKT);
			break;
		
		case 4:
			// Q11 Buffer of Lines
			label = "Buffer_LGD_2"; 
			query = queryBufferTemplate;
			query = query.replace("GRAPH1", GraphURI.LGD_URI);
			query = query.replace("ASWKT1", lgd_asWKT);
			break;

		case 5:
			// Q12 Area of Polygons
			label = "Area_CLC"; 
			query = queryStsparqlTemplate;
			query = query.replace("FUNCTION", "area");
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			break;
			
			
		default:
			logger.error("No such query number exists:"+queryIndex);
		}
	
		String translatedQuery = sut.translateQuery(query, label);
		return new QueryStruct(translatedQuery, label);
	}
	
}
