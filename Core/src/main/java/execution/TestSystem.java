package execution;

import execution_results.QueryResult;
import java.io.Closeable;
import java.time.Duration;

public interface TestSystem extends Closeable {

    public QueryResult runQueryWithTimeout(String query, Duration timeout) throws Exception;

    public QueryResult runUpdate(String query) throws Exception;

    public String translateQuery(String query);
       

}
