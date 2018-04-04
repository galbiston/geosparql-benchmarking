package geosparql_benchmarking.experiments;

import java.io.IOException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandlerException;

public interface TestSystem {

    public String getName();

    public QueryResult runQueryWithTimeout(String query, int timeoutSecs) throws Exception;

    public QueryResult runUpdate(String query) throws MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException, IOException;

    public void initialize();

    public void close();

    public void clearCaches();

    public void restart();

    public String translateQuery(String query, String label);

}
