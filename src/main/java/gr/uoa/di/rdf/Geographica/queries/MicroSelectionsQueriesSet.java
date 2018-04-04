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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import geosparql_benchmarking.experiments.TestSystem;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 */
public class MicroSelectionsQueriesSet extends QueriesSet {

	static Logger logger = Logger.getLogger(MicroSelectionsQueriesSet.class.getSimpleName());
	
	// Template to create queries
	private final String queryTemplate = prefixes 
			+ "\n select ?s1 ?o1 where { \n"
			+ "	GRAPH <GRAPH1> {?s1 ASWKT1 ?o1} \n"
			+ "  FILTER(geof:FUNCTION(?o1, GIVEN_SPATIAL_LITERAL)). " 
			+ "}  \n" 
	;

	private String givenPolygonFile = "givenPolygon.txt";
	private String givenLinesFile = "givenLine.txt";
	private String givenPolygon;
	private String givenLine, givenLine2, givenLine3;
	private String givenPoint;
	private String givenRadius;
	
	public MicroSelectionsQueriesSet(TestSystem sut) throws IOException {
		super(sut);
		queriesN = 11; // IMPORTANT: Add/remove queries in getQuery implies changing queriesN
		
		String spatialDatatype = "<http://www.opengis.net/ont/geosparql#wktLiteral>";
		givenPoint = "\"POINT(23.71622 37.97945)\"^^"+spatialDatatype;
		givenRadius = "3000";
		
		InputStream is = getClass().getResourceAsStream("/"+givenPolygonFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		givenPolygon = in.readLine();
		givenPolygon = "\""+givenPolygon+"\"^^"+spatialDatatype;
		in.close();
		in = null;
		is.close();
		is = null;
		is = getClass().getResourceAsStream("/"+givenLinesFile);
		in = new BufferedReader(new InputStreamReader(is));
		// <http://linkedgeodata.org/geometry/way168092715>
		givenLine = in.readLine();
		givenLine = "\""+givenLine+"\"^^"+spatialDatatype;
		// <http://linkedgeodata.org/geometry/way31642973>
		givenLine2 = in.readLine();
		givenLine2 = "\""+givenLine2+"\"^^"+spatialDatatype;
		// <http://linkedgeodata.org/geometry/way45476887>
		givenLine3 = in.readLine();
		givenLine3 = "\""+givenLine3+"\"^^"+spatialDatatype;
		in.close();
		in = null;
		is.close();
		is = null;
	}
	
	@Override
	public QueryStruct getQuery(int queryIndex, int repetition) {
		
		String query = null, label = null;
		
		// IMPORTANT: Add/remove queries in getQuery implies changing queriesN and changing case numbers
		switch (queryIndex) {

		// -- Equals -- //
		case 0:
			// Line = GivenLine
			label = "Equals_LGD_GivenLine"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.LGD_URI);
			query = query.replace("ASWKT1", lgd_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenLine);
			query = query.replace("FUNCTION", "sfEquals");
			break;
	
		case 1:
			// Polygon = GivenPolygon
			label = "Equals_GADM_GivenPolygon"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.GADM_URI);
			query = query.replace("ASWKT1", gadm_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfEquals");
			break;
	
		// -- Intersects -- //
		case 2:
			// Line & GivenPolygon
			label = "Intersects_LGD_GivenPolygon"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.LGD_URI);
			query = query.replace("ASWKT1", lgd_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfIntersects");
			break;
	
		case 3:
			// Polygon & GivenLine
			label = "Intersects_CLC_GivenLine"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenLine2);
			query = query.replace("FUNCTION", "sfIntersects");
			break;
	
		// -- Overlaps -- //
		case 4:
			label = "Overlaps_CLC_GivenPolygon"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.CLC_URI);
			query = query.replace("ASWKT1", clc_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfOverlaps");
			break;
	

		// -- Crosses -- //
		case 5:
			label = "Crosses_LGD_GivenLine"; 
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.LGD_URI);
			query = query.replace("ASWKT1", lgd_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenLine3);
			query = query.replace("FUNCTION", "sfCrosses");
			break;
			
		// -- Within -- //
		case 6:
			label = "Within_GeoNames_GivenPolygon";// times = 345699520 + 840787868 = 1186487388, 136
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
			query = query.replace("ASWKT1", geonames_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfWithin");
			break;
	
		// -- Within buffer -- //
		case 7:
			label = "Intersects_GeoNames_Point_Buffer";
			query = prefixes + "\n select ?s1 where { \n"
					+ "	GRAPH <" + GraphURI.GEONAMES_URI	+ "> {?s1 "+geonames_asWKT+" ?o1} \n"
					+ " FILTER(geof:sfWithin(?o1, geof:buffer("
					+ givenPoint + ", " + givenRadius + ", <http://www.opengis.net/def/uom/OGC/1.0/metre>"
					+ "))).  \n" 
					+ "} "
					;
			break;
	
		// -- Within distance -- //
		case 8:
			label = "Intersects_GeoNames_Point_Distance";
			query = prefixes + "\n select ?s1 where { \n" 
					+ "	GRAPH <" + GraphURI.GEONAMES_URI	+ "> {?s1 "+geonames_asWKT+" ?o1} \n" 
					+ "  FILTER(geof:distance(?o1, "+ givenPoint + ", <http://www.opengis.net/def/uom/OGC/1.0/metre>) <= " + givenRadius + ").  \n" 
					+ "} "
					;
			break;
		case 9:
			// -- Disjoint -- //
			// Point != GivenPolygon
			label = "Disjoint_GeoNames_MaxPolygon";
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.GEONAMES_URI);
			query = query.replace("ASWKT1", geonames_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfDisjoint");
			break;
		case 10:
			// Line != GivenPolygon
			label = "Disjoint_LGD_MaxPolygon";
			query = queryTemplate;
			query = query.replace("GRAPH1", GraphURI.LGD_URI);
			query = query.replace("ASWKT1", lgd_asWKT);
			query = query.replace("GIVEN_SPATIAL_LITERAL", givenPolygon);
			query = query.replace("FUNCTION", "sfDisjoint");
			break;
			
		default:
			logger.error("No such query number exists:"+queryIndex);
		}
	
		String translatedQuery = sut.translateQuery(query, label);
		return new QueryStruct(translatedQuery, label);
	}

}
