/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geosparql_benchmarking.strabon;

import execution.BenchmarkExecution;
import execution.cli.ExecutionParameters;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String dbName = "endpoint";
        String user = "postgres";
        String password = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"127.0.0.1"
        String resultsFolder = "strabon";

        String postgresBinPath = "\"C:\\Program Files\\PostgreSQL\\10\\bin\\";
        String postgresDataPath = "\"C:\\Program Files\\PostgreSQL\\10\\data\\\"";

        Boolean inferenceEnabled = true;
        String baseURI = null;
        String format = "NTRIPLES";
        //String format = "RDFXML"; //Use for conformance dataset.
        //Built using PGAdmin tool to create a PostGIS template.
        String databaseTemplate = "template_postgis";

        try {

            Strabon_TestSystemFactory testSystemFactory = new Strabon_TestSystemFactory(dbName, user, password, port, host, resultsFolder, inferenceEnabled, baseURI, format, postgresBinPath, postgresDataPath, databaseTemplate);

            ExecutionParameters parameters = ExecutionParameters.extract("Strabon", args);

            BenchmarkExecution.runType(testSystemFactory, parameters);

        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }

    }

}
