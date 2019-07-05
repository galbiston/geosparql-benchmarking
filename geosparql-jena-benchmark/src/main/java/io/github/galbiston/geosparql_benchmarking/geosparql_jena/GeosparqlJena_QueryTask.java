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
package io.github.galbiston.geosparql_benchmarking.geosparql_jena;

import io.github.galbiston.geosparql_benchmarking.execution.QueryTask;
import static io.github.galbiston.geosparql_benchmarking.execution_results.DatasetLoadResult.saveQueryResult;
import io.github.galbiston.geosparql_benchmarking.execution_results.VarValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class GeosparqlJena_QueryTask extends QueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String queryString;
    private final Dataset dataset;
    private final Boolean isUnionDefaultGraph;
    private QueryExecution qexec;
    private ResultSet rs;
    private boolean queryExecutionError;
    private String queryExecutionErrorMsg;
    private boolean isConformanceTestSytem;

    public GeosparqlJena_QueryTask(String queryString, Dataset dataset, Boolean isUnionDefaultGraph, int i) {
        this.queryString = queryString;
        this.dataset = dataset;
        this.isUnionDefaultGraph = isUnionDefaultGraph;
    }

    public GeosparqlJena_QueryTask(String queryString, Dataset dataset, Boolean isUnionDefaultGraph, boolean isConformanceTestSytem) {
        this.queryString = queryString;
        this.dataset = dataset;
        this.isUnionDefaultGraph = isUnionDefaultGraph;
        this.isConformanceTestSytem = isConformanceTestSytem;
    }

    @Override
    protected void prepareQuery() {
        dataset.begin(ReadWrite.READ);
        queryExecutionError = false;
        //todo: if statement for conformance testing
        //qexec = QueryExecutionFactory.create(queryString, dataset);
        qexec = QueryExecutionFactory.create(queryString, dataset.getNamedModel("Conformance"));
        if (isUnionDefaultGraph) {
            qexec.getContext().set(TDB.symUnionDefaultGraph, true);
        }
        rs = qexec.execSelect();
    }

    @Override
    protected void executeQuery() {
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Iterator<String> varNames = qs.varNames();
            List<VarValue> result = new ArrayList<>();

            while (varNames.hasNext()) {
                String varName = varNames.next();
                String valueStr;
                RDFNode solution = qs.get(varName);
                
                //valueStr = "This is NOT a Conformance Test System";//literal.getLexicalForm();
                //if (isConformanceTestSytem) {
                //    valueStr = "YES this is a Conformance Test System";//literal.asNode().toString();
                //}
                
                if (solution.isLiteral()) {
                    Literal literal = solution.asLiteral();
                    valueStr = literal.getLexicalForm();
                    if (!isConformanceTestSytem) {
                        valueStr = literal.asNode().toString();
                    }
                } else if (solution.isResource()) {
                    Resource resource = solution.asResource();
                    valueStr = resource.getURI();;
                } else {
                    Node anon = solution.asNode();
                    valueStr = anon.getBlankNodeLabel();
                    LOGGER.error("Anon Node result: {}", valueStr);
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
        dataset.end();
    }

    public boolean isQueryExecutionError() {
        return queryExecutionError;
    }

    public void setQueryExecutionError(boolean queryExecutionError) {
        this.queryExecutionError = queryExecutionError;
    }

    public String getQueryExecutionErrorMsg() {
        return queryExecutionErrorMsg;
    }

    public void setQueryExecutionErrorMsg(String queryExecutionErrorMsg) {
        this.queryExecutionErrorMsg = queryExecutionErrorMsg;
    }

    public boolean isIsConformanceTestSytem() {
        return isConformanceTestSytem;
    }

    public void setIsConformanceTestSytem(boolean isConformanceTestSytem) {
        this.isConformanceTestSytem = isConformanceTestSytem;
    }
}
