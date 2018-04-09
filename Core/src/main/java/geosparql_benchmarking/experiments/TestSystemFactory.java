/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.io.File;

/**
 *
 * @author Gerg
 */
public interface TestSystemFactory {
    
    public TestSystem getTestSystem();
    
    public String getTestSystemName();
    
    public File getResultsFolder();
}