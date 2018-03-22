/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.queries;

import gr.uoa.di.rdf.Geographica.systemsundertest.SystemUnderTest;

import org.apache.log4j.Logger;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 */
public class MicroAggregationQueriesSet  extends QueriesSet {	
	
	static Logger logger = Logger.getLogger(MicroAggregationQueriesSet.class.getSimpleName());
	
	private String queryTemplate = prefixes 
			+ "\n select (strdf:FUNCTION(?o1) as ?ret) where {  \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "}";
	
	public MicroAggregationQueriesSet(SystemUnderTest sut) {
		super(sut);
		queriesN = 4; // IMPORTANT: Add/remove queries in getQuery implies changing queriesN
	}
	
	@Override
	public int getQueriesN() { return queriesN; }
	
	@Override
	public QueryStruct getQuery(int queryIndex, int repetition) {
		
		String query = null, label = null;
		
		// IMPORTANT: Add/remove queries in getQuery implies changing queriesN and changing case numbers
		switch (queryIndex) {
			// -- Extension -- //	
			case 0:
				// Q2 Extension of Lines
				label = "Extent_LGD"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("FUNCTION", "extent");
				break;

			case 1:
				// Q4 Extension of many simple Polygons
				label = "Extent_CLC"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", CLC_URI);
				query = query.replace("ASWKT1", clc_asWKT);
				query = query.replace("FUNCTION", "extent");
				break;
				
			case 2:
				// Q2 Union of Lines
				label = "Union_LGD"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", LGD_URI);
				query = query.replace("ASWKT1", lgd_asWKT);
				query = query.replace("FUNCTION", "union");
				break;

			case 3:
				// Q1 Union of many simple Polygons
				label = "Union_CLC"; 
				query = queryTemplate;
				query = query.replace("GRAPH1", CLC_URI);
				query = query.replace("ASWKT1", clc_asWKT);
				query = query.replace("FUNCTION", "union");
				break;
				
			default:
				logger.error("No such query number exists:"+queryIndex);
				
		}
		
		String translatedQuery = sut.translateQuery(query, label);
		return new QueryStruct(translatedQuery, label);
	}
	
}
