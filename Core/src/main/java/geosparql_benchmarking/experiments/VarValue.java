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
public class VarValue {

    private final String var;
    private final String value;

    public VarValue(String var, String value) {
        this.var = var;
        this.value = value;
    }

    public String getVar() {
        return var;
    }

    public String getValue() {
        return value;
    }

    public Boolean hasVar(String label) {
        return var.equals(label);
    }

    @Override
    public String toString() {
        return "VarValue{" + "var=" + var + ", value=" + value + '}';
    }

}
