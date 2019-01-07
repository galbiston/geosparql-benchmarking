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
package geosparql_benchmarking.geosparql_jena.cli;

import com.beust.jcommander.IStringConverter;
import geosparql_benchmarking.geosparql_jena.SystemType;
import java.util.Arrays;

/**
 *
 *
 */
public class SystemTypeConverter implements IStringConverter<SystemType> {

    @Override
    public SystemType convert(String systemString) {

        try {
            return SystemType.valueOf(systemString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("System Type: " + systemString + " not recognised. Expected: " + Arrays.toString(SystemType.values()));
        }

    }
}
