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
package io.github.galbiston.geosparql_benchmarking.parliament;

import io.github.galbiston.execution.BenchmarkExecution;
import io.github.galbiston.execution.cli.ExecutionParameters;
import java.io.File;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String PARLIAMENT_RESULTS_FOLDER_NAME = "parliament";
    public static final File PARLIAMENT_KNOWLEDGE_BASE_FOLDER = new File("parliament_kb");

    /**
     * @param args the command line arguments<br>
     * The Parliament DLL folder needs to added to the PATH and the
     * ParliamentConfig.txt filepath set to the PARLIAMENT_CONFIG_PATH
     * environment variables so that they are available on the java.library.path
     * property.
     */
    public static void main(String[] args) {

        try {
            ExecutionParameters parameters = ExecutionParameters.extract("Parliament", args);

            Parliament_TestSystemFactory testSystemFactory = new Parliament_TestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
            BenchmarkExecution.runType(testSystemFactory, parameters);

        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }

    }

}
