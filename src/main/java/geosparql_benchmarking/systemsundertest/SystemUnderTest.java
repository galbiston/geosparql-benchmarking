package geosparql_benchmarking.systemsundertest;

import java.io.IOException;
import java.util.HashMap;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResultHandlerException;

public interface SystemUnderTest {

    long[] runQueryWithTimeout(String query, int timeoutSecs) throws Exception;

    long[] runUpdate(String query) throws MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException, IOException;

    void initialize();

    void close();

    void clearCaches();

    void restart();

    Object getSystem();

    public String translateQuery(String query, String label);

    public HashMap<String, String> getFirstResult();
}
