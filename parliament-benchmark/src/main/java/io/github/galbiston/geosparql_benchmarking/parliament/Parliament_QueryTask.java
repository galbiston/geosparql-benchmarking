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
package io.github.galbiston.geosparql_benchmarking.parliament;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import io.github.galbiston.geosparql_benchmarking.execution.QueryTask;
import io.github.galbiston.geosparql_benchmarking.execution_results.VarValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class Parliament_QueryTask extends QueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String queryString;
    private final Dataset dataset;
    private QueryExecution qexec;
    private ResultSet rs;

    public Parliament_QueryTask(String queryString, Dataset dataset) {
        this.queryString = queryString;
        this.dataset = dataset;
    }

    @Override
    protected void prepareQuery() {
        qexec = QueryExecutionFactory.create(queryString, dataset);
        rs = qexec.execSelect();
    }

    @Override
    protected void executeQuery() {
        while (rs.hasNext()) {
            QuerySolution querySolution = rs.next();
            Iterator<String> varNames = querySolution.varNames();
            List<VarValue> result = new ArrayList<>();
            while (varNames.hasNext()) {
                String varName = varNames.next();
                String valueStr;
                RDFNode solution = querySolution.get(varName);
                if (solution.isLiteral()) {
                    Literal literal = solution.asLiteral();
                    valueStr = literal.getLexicalForm();
                } else if (solution.isResource()) {
                    Resource resource = solution.asResource();
                    valueStr = resource.getURI();
                } else {
                    Node anon = solution.asNode();
                    valueStr = anon.getBlankNodeLabel();
                    LOGGER.error("Anon Node result: " + valueStr);
                }
                VarValue varValue = new VarValue(varName, valueStr);
                result.add(varValue);
            }
            results.add(result);
        }
    }

    @Override
    protected void endQuery() {
        qexec.close();
    }
}
