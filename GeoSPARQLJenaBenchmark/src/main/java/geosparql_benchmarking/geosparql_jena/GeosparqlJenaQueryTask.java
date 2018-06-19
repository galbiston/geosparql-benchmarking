/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import execution.QueryTask;
import execution_results.VarValue;
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
public class GeosparqlJenaQueryTask extends QueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String queryString;
    private final Dataset dataset;
    private final Boolean isUnionDefaultGraph;
    private QueryExecution qexec;
    private ResultSet rs;

    public GeosparqlJenaQueryTask(String queryString, Dataset dataset, Boolean isUnionDefaultGraph) {
        this.queryString = queryString;
        this.dataset = dataset;
        this.isUnionDefaultGraph = isUnionDefaultGraph;
    }

    @Override
    protected void prepareQuery() {
        dataset.begin(ReadWrite.READ);
        qexec = QueryExecutionFactory.create(queryString, dataset);
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
                if (solution.isLiteral()) {
                    Literal literal = solution.asLiteral();
                    valueStr = literal.getLexicalForm();
                } else if (solution.isResource()) {
                    Resource resource = solution.asResource();
                    valueStr = resource.getURI();
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

}
