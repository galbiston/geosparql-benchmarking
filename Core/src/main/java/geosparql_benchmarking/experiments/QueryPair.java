/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

/**
 *
 *
 */
public class QueryPair {

    private final String label;
    private final String geometry;

    public QueryPair(String label, String geometry) {
        this.label = label;
        this.geometry = geometry;
    }

    public String getLabel() {
        return label;
    }

    public String getGeometry() {
        return geometry;
    }

    @Override
    public String toString() {
        return "QueryPair{" + "label=" + label + ", geometry=" + geometry + '}';
    }

}
