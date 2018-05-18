package execution;

import execution_results.QueryResult;
import java.io.Closeable;

public interface TestSystem extends Closeable {

    public QueryResult runUpdate(String query) throws Exception;

    public String translateQuery(String query);

    public QueryTask getQueryTask(String query);

}
