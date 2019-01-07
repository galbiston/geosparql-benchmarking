/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
