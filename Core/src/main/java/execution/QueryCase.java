/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

/**
 *
 *
 */
public class QueryCase {

    private final String queryName;
    private final String queryType;
    private final String queryString;

    public QueryCase(String queryName, String queryType, String queryString) {
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryString = queryString;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryString() {
        return queryString;
    }

    @Override
    public String toString() {
        return "QueryCase{" + "queryName=" + queryName + ", queryType=" + queryType + ", queryString=" + queryString + '}';
    }

}
