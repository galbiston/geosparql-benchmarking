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
package io.github.galbiston.geosparql_benchmarking.data_setup;

import java.time.Duration;

/**
 *
 *
 */
public class BenchmarkParameters {

    public static final Integer ITERATIONS = 10; //1;
    public static final Duration TIMEOUT = Duration.ofSeconds(3600);

    /**
     * Set to zero for no query result output.
     */
    public static final Integer RESULT_LINE_LIMIT_ZERO = 0;

    /**
     * 5000 line limit per query result output file.
     */
    public static final Integer RESULT_LINE_LIMIT_5000 = 5000;

}
