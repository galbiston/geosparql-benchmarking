/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.queries;

import gr.uoa.di.rdf.Geographica.generators.SyntheticGenerator;

import java.io.IOException;

import org.apache.log4j.Logger;
import geosparql_benchmarking.experiments.TestSystem;

/**
 * @author Kostis Kyzirakos <kkyzri@di.uoa.gr>
 */
public class SyntheticOnlyPointsQueriesSet extends QueriesSet {

	static Logger logger = Logger.getLogger(SyntheticOnlyPointsQueriesSet.class.getSimpleName());
	int N;
	private SyntheticGenerator generator;
	
	public SyntheticOnlyPointsQueriesSet(TestSystem sut, int N) throws IOException {
		super(sut);
		queriesN = 16; // IMPORTANT: Add/remove queries in getQuery implies changing queriesN
		this.N = N;
		this.generator = new SyntheticGenerator("", N);
	}
	
	@Override
	public QueryStruct getQuery(int queryIndex, int repetition) {		
		String query = null, label = null;
		
		String[][][] queries = this.generator.generatePointQueries();
		double[] selectivities = this.generator.returnSelectivities();
		
		String[] selections = queries[0][0];
		assert selections.length == selectivities.length*2;
				
		String[] joins = queries[0][1];
		assert joins.length == 4;
		assert queriesN == selections.length + joins.length;
		
		
		// IMPORTANT: Add/remove queries in getQuery implies changing queriesN and changing case numbers		
		if (queryIndex >= 0 && queryIndex < selectivities.length*2) {
			label = "Synthetic_Selection_Distance_" + ((queryIndex%2 == 0) ? "1" : this.generator.returnMaxTagValue().toString()) + "_" + selectivities[queryIndex/2];
			query = selections[queryIndex];
		} 
		
		else if (queryIndex == selectivities.length*2) {
			label = "Synthetic_Join_Distance_1_1"; query = joins[0]; 
		} else if (queryIndex == selectivities.length*2 + 1) {
			label = "Synthetic_Join_Distance_1_" + this.generator.returnMaxTagValue() + ""; query = joins[1]; 
		} else if (queryIndex == selectivities.length*2 + 2) {
			label = "Synthetic_Join_Distance_" + this.generator.returnMaxTagValue() + "_1"; query = joins[2]; 
		} else if (queryIndex == selectivities.length*2 + 3) {
			label = "Synthetic_Join_Distance_" + this.generator.returnMaxTagValue() + "_" + this.generator.returnMaxTagValue() + ""; query = joins[3]; 
		}
		
		else {
			logger.error("No such query number exists:" + queryIndex);
		}
	
		String translatedQuery = sut.translateQuery(query, label);
		return new QueryStruct(translatedQuery, label);
	}

}
