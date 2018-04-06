package geosparql_benchmarking.experiments;

import java.io.Closeable;
import java.time.Duration;

public interface TestSystem extends Closeable {

    public String getName();

    public QueryResult runQueryWithTimeout(String query, Duration timeout) throws Exception;

    public QueryResult runUpdate(String query) throws Exception;

    public String translateQuery(String query);

}
