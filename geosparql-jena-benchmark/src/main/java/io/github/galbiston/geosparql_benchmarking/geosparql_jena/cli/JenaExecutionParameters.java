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
package io.github.galbiston.geosparql_benchmarking.geosparql_jena.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.github.galbiston.geosparql_benchmarking.execution.cli.ExecutionParameters;
import io.github.galbiston.geosparql_benchmarking.geosparql_jena.SystemType;

/**
 *
 *
 */
public class JenaExecutionParameters extends ExecutionParameters {

    //9) Query Cases
    @Parameter(names = {"--systemType", "-st"}, description = "System Type for Apache Jena benchmarking. Expected: TDB, TDB_UNION, MEMORY, MEMORY_UNION, NO_INDEX.", converter = SystemTypeConverter.class, order = 8)
    private SystemType systemType = SystemType.MEMORY;

    public SystemType getSystemType() {
        return systemType;
    }

    public String toSummary() {
        return systemType + ", " + super.toSummary();
    }

    @Override
    public String toString() {
        return "JenaExecutionParameters{" + toSummary() + '}';
    }

    public static JenaExecutionParameters extract(String benchmarkName, String[] args) {

        JenaExecutionParameters executionParameters = new JenaExecutionParameters();

        JCommander jCommander = JCommander.newBuilder()
                .addObject(executionParameters)
                .build();

        jCommander.setProgramName(benchmarkName);
        jCommander.parse(args);
        executionParameters.finish();

        if (executionParameters.isHelp()) {
            jCommander.usage();
        }

        return executionParameters;
    }

}
