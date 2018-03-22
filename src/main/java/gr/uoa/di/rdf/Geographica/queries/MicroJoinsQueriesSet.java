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
import gr.uoa.di.rdf.Geographica.systemsundertest.SystemUnderTest;

import org.apache.log4j.Logger;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 */
public class MicroJoinsQueriesSet extends QueriesSet {

	static Logger logger = Logger.getLogger(MicroJoinsQueriesSet.class.getSimpleName());
		
	public  MicroJoinsQueriesSet(SystemUnderTest sut) {
		super(sut);
		queriesN = 11; // IMPORTANT: Add/remove queries in getQuery implies changing queriesN
	}

	@Override
	public int getQueriesN() { return queriesN; }
	
	private String queryTemplate = prefixes 
			+ "\n select ?s1 ?s2 where { \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "	GRAPH <GRAPH2> {?s2 ASWKT2 ?o2} \n"
			+ "  FILTER(geof:FUNCTION(?o1, ?o2)).  \n"
			+ "} \n"
			;
	
	private String queryTemplate2 = prefixes 
			+ "\n select ?s1 ?s2 where { \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "	GRAPH <GRAPH2> {?s2 ASWKT2 ?o2} \n"
			+ " FILTER(?s1 != ?s2).  \n"
			+ " FILTER(geof:FUNCTION(?o1, ?o2)).  \n"
			+ "} \n"
			;

	@Override
	public QueryStruct getQuery(int queryIndex, int repetition) {
	
		String query = null, label = null;
		
		// IMPORTANT: Add/remove queries in getQuery implies changing queriesN and changing case numbers
		switch (queryIndex) {
			// -- Equals -- //
			case 0:			
				// Q1 Find equal points in GeoNames & DBPedia
				label = "Equals_GeoNames_DBPedia";
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
				query = query.replace("ASWKT1", geonames_asWKT);
				query = query.replace("GRAPH2", GraphURI.DBPEDIA_URI);
				query = query.replace("ASWKT2", dbpedia_asWKT);
				query = query.replace("FUNCTION", "sfEquals");
				
				break;

			// -- Intersects -- //
			case 1:
				// Q2 POIS of GeoNames reached by road
				label = "Intersects_GeoNames_LGD"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
				query = query.replace("ASWKT1", geonames_asWKT);
				query = query.replace("GRAPH2", GraphURI.LGD_URI);
				query = query.replace("ASWKT2", lgd_asWKT);
				query = query.replace("FUNCTION", "sfIntersects");
				break;

			case 2:
				// Q5 POIS of GeoNames in an area
				label = "Intersects_GeoNames_GADM"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
				query = query.replace("ASWKT1", geonames_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfIntersects");
				break;
				
			case 3:
				// Q6 Roads of an area
				label = "Intersects_LGD_GADM"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfIntersects");
				break;
				
			// -- Within -- //
			case 4:
				// Q5 POIS of GeoNames inside an area
				label = "Within_GeoNames_GADM"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
				query = query.replace("ASWKT1", geonames_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfWithin");
				break;
	
			case 5:
				// Q8 Roads within an area
				label = "Within_LGD_GADM"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfWithin");
				break;
				
			case 6:
				// Q13 Areas contained in a country
				label = "Within_CLC_GADM";
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.CLC_URI);
				query = query.replace("ASWKT1", clc_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfWithin");
				break;
				
			// -- Crosses -- //
			case 7:
				// Q7 Roads leaving/reaching an area
				label = "Crosses_LGD_GADM"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfCrosses");
				break;
			
			case 8:
				// Q9 Intercrossing roads
				label = "Crosses_LGD_LGD"; 
				query = queryTemplate2;
				query = query.replace("GRAPH1", GraphURI.LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("GRAPH2", GraphURI.LGD_URI);
				query = query.replace("ASWKT2", lgd_asWKT);
				query = query.replace("FUNCTION", "sfCrosses");
				break;
				
				
			// -- Touches -- //
			case 9:
				// Q11 Countries with sharing borders
				label = "Touches_GADM_GADM"; 
				query = queryTemplate2;
				query = query.replace("GRAPH1", GraphURI.GADM_URI);
				query = query.replace("ASWKT1", gadm_asWKT);
				query = query.replace("GRAPH2", GraphURI.GADM_URI);
				query = query.replace("ASWKT2", gadm_asWKT);
				query = query.replace("FUNCTION", "sfTouches");
				break;
			
			// -- Overlaps -- //
			case 10:
				// Q12 Areas overlaping countries
				label = "Overlaps_GADM_CLC"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", GraphURI.GADM_URI);
				query = query.replace("ASWKT1", gadm_asWKT);
				query = query.replace("GRAPH2", GraphURI.CLC_URI);
				query = query.replace("ASWKT2", clc_asWKT);
				query = query.replace("FUNCTION", "sfOverlaps");
				break;
	
			default:
				logger.error("No such query number exists:"+queryIndex);
		}
		
		String translatedQuery = sut.translateQuery(query, label);
		return new QueryStruct(translatedQuery, label);
	}
	
}
