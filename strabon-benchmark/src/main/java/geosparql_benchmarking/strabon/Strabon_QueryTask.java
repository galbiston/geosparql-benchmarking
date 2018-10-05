/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import execution.QueryTask;
import execution_results.VarValue;
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
