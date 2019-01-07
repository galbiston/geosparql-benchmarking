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
import io.github.galbiston.execution.QueryTask;
import io.github.galbiston.execution_results.VarValue;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

/**
 *
 *
 */
public class Strabon_QueryTask extends QueryTask {

    private final String queryString;
    private final Strabon strabon;
    private TupleQueryResult tupleQueryResult;

    public Strabon_QueryTask(String queryString, Strabon strabon) {
        this.queryString = queryString;
        this.strabon = strabon;
    }

    @Override
    protected void prepareQuery() throws Exception {
        TupleQuery tupleQuery = (TupleQuery) strabon.query(queryString, Format.TUQU, strabon.getSailRepoConnection(), System.out);

        tupleQueryResult = tupleQuery.evaluate();
    }

    @Override
    protected void executeQuery() throws Exception {
        while (tupleQueryResult.hasNext()) {

            BindingSet bindingSet = tupleQueryResult.next();
            List<String> bindingNames = tupleQueryResult.getBindingNames();
            List<VarValue> result = new ArrayList<>();
            for (String binding : bindingNames) {
                Value value = bindingSet.getValue(binding);
                String valueStr = value.stringValue();
                VarValue varValue = new VarValue(binding, valueStr);
                result.add(varValue);
            }
            results.add(result);
        }
        tupleQueryResult.close();
    }

    @Override
    protected void endQuery() {

    }

}
