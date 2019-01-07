/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution.cli;

import execution.QueryCase;
import execution.QueryLoader;
import java.io.File;
import java.util.List;
import queries.geographica.MacroBenchmark;
import queries.geographica.MicroBenchmark;
import queries.geosparql.GeosparqlBenchmark;

/**
 *
 *
 */
public class QueryCaseLoader {

    public static final List<QueryCase> load(String queryCaseName, Integer iterations) {

        String arg = queryCaseName.toLowerCase();
        if (arg.startsWith("micro")) {
            return MicroBenchmark.loadQueryCases(arg);
        } else if (arg.startsWith("macro")) {
            return MacroBenchmark.loadQueryCases(arg, iterations);
        } else if (arg.startsWith("geosparql")) {
            return GeosparqlBenchmark.loadQueryCases(arg);
        } else {
            File fileArg = new File(queryCaseName);
            if (fileArg.exists()) {
                if (fileArg.isDirectory()) {
                    return QueryLoader.readFolder(fileArg);
                } else {
                    return QueryLoader.readQuery(fileArg.getAbsolutePath());
                }
            } else {
                throw new IllegalArgumentException("Unknown Query Case Set: " + queryCaseName + ". Expected to start with 'micro', 'macro', 'geosparql' or a file/folder path to load.");
            }
        }

    }
}
