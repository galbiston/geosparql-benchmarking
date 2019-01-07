/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution.cli;

import com.beust.jcommander.IStringConverter;
import execution.BenchmarkType;
import java.util.Arrays;

/**
 *
 *
 */
public class BenchmarkTypeConverter implements IStringConverter<BenchmarkType> {

    @Override
    public BenchmarkType convert(String benchmarkString) {

        try {
            return BenchmarkType.valueOf(benchmarkString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Benchmark Type: " + benchmarkString + " not recognised. Expected: " + Arrays.toString(BenchmarkType.values()));
        }

    }
}
