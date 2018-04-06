package geosparql_benchmarking.experiments;

import java.time.Duration;

public interface TestSystem {

    public String getName();

    public QueryResult runQueryWithTimeout(String query, Duration timeout) throws Exception;

    public QueryResult runUpdate(String query) throws Exception;

    public void initialize();

    public void close();

    public String translateQuery(String query);

}
