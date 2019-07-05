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
package io.github.galbiston.geosparql_benchmarking.execution;

import io.github.galbiston.geosparql_benchmarking.results_validation.QueryResultSeverity;
import io.github.galbiston.geosparql_benchmarking.results_validation.QueryResultType;
import io.github.galbiston.geosparql_benchmarking.results_validation.QueryResultsValidator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.*;
/**
 *
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.example.org/querycase")
//@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
public class QueryCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @XmlElement
    private String queryName;
    @XmlElement
    private String queryType;
    @XmlElement
    //@XmlJavaTypeAdapter(AdapterCDATA.class) 
    private List<String> queryStrings;

    @XmlElement
    private List<String> expectedResultsFileNames;
    @XmlElement
    private List<String> queryResultsFileNames;  
    @XmlElement
    private List<String> datasetFileNames;
    @XmlElement
    private List<String> queryOrderBys; 
    @XmlElement
    private List<QueryResultType> queryResultType;
    @XmlElement
    private List<QueryResultSeverity> queryResultSeverity;

    public QueryCase(){
        this.queryStrings = new ArrayList<>();
        this.queryName = null;
        this.queryType = null;
        this.expectedResultsFileNames = new ArrayList<>();
        this.expectedResultsFileNames = new ArrayList<>();
        this.datasetFileNames = new ArrayList<>();
        this.queryOrderBys = new ArrayList<>();
    }
    
     public QueryCase(String queryName, String queryType, String queryString,
             String expectedResFile, String data, String resultsFile, String order, 
             QueryResultType validator, QueryResultSeverity qrSeverity) throws Exception {
        this.queryStrings = new ArrayList<>();
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryStrings.add(marshal(queryString));  
        this.expectedResultsFileNames = new ArrayList<>();
        this.expectedResultsFileNames.add(expectedResFile);
        this.queryResultsFileNames = new ArrayList<>();
        this.queryResultsFileNames.add(resultsFile);
        this.datasetFileNames = new ArrayList<>();
        this.datasetFileNames.add(data);
        this.queryOrderBys = new ArrayList<>();
        this.queryOrderBys.add(order);
        this.queryResultType = new ArrayList<>();
        this.queryResultType.add(validator);
        this.queryResultSeverity = new ArrayList<>();
        this.queryResultSeverity.add(qrSeverity);
    }
     
    public QueryCase(String queryName, String queryType, String queryString) {
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryStrings = new ArrayList<>();
        this.queryStrings.add(queryString);
    }

    public QueryCase(String queryName, String queryType, List<String> queryStrings) {
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryStrings = queryStrings;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryString() {
        return queryStrings.get(0);
    }

    public void addQueryString(String queryString) {
        queryStrings.add(queryString);
    }

    public String getQueryString(int index) throws Exception {
        if (queryStrings.size() == 1) {
            return queryStrings.get(0);
        } else {
            if (index < queryStrings.size()) {
                return queryStrings.get(index);
            } else {
                throw new Exception("Index greater than query string: " + index + " vs " + queryStrings.size());
            }
        }
    }

    public void setQueryStrings(List<String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public void setExpectedResultsFileNames(List<String> expectedResults) {
        this.expectedResultsFileNames = expectedResults;
    }
    
    public void setQueryResultsFileNames(List<String> queryResults) {
        this.queryResultsFileNames = queryResults;
    }
    
    public void setDatasetFileNames(List<String> data) {
        this.datasetFileNames = data;
    }

    public void setQueryOrderBys(List<String> queryOrderBys) {
        this.queryOrderBys = queryOrderBys;
    }
    
    public void setQueryOrderBy(List<String>  queryOrderBy) {
        this.queryOrderBys = queryOrderBy;
    }

    public List<String> getQueryStrings() {
        return queryStrings;
    }

    public List<String> getExpectedResultsFileNames() {
        return expectedResultsFileNames;
    }
    
    public List<String> setQueryResultsFileNames() {
        return queryResultsFileNames;
    }

    public List<String> getQueryResultsFileNames() {
        return queryResultsFileNames;
    }
        
    public List<String>  getQueryOrderBys() {
        return queryOrderBys;
    }

    public List<String> getDatasetFileNames() {
        return datasetFileNames;
    }

    public List<QueryResultType> getQueryResultType() {
        return queryResultType;
    }

    public void setQueryResultType(List<QueryResultType> queryResultsValidators) {
        this.queryResultType = queryResultsValidators;
    }
  
        public List<QueryResultSeverity> getQueryResultSeverity() {
        return queryResultSeverity;
    }

    public void setQueryResultSeverity(List<QueryResultSeverity> queryResultsSeverity) {
        this.queryResultSeverity = queryResultsSeverity;
    }
    
    @Override
    public String toString() {
        return "QueryCase{" + "queryName=" + queryName + ", queryType=" + queryType + ", queryStrings=" + queryStrings + '}';
    }

    private static final String PADDING_STRING = "########################################";

    public static final void writeQueryFile(File runResultsFolder, QueryCase queryCase, String testSystemName, String testTimestamp) {

        runResultsFolder.mkdir();
        String filename = "querycases-" + testSystemName + "-" + testTimestamp + ".txt";
        File queryCaseFile = new File(runResultsFolder, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(queryCaseFile, true))) {

            writer.write(PADDING_STRING);
            writer.newLine();
            writer.write("#" + queryCase.queryName + " - " + queryCase.queryType);
            writer.newLine();
            writer.newLine();

            boolean isMultipleStrings = queryCase.queryStrings.size() > 1;
            int i = 1;
            for (String queryString : queryCase.queryStrings) {
                if (isMultipleStrings) {
                    writer.write("#Iteration: " + i);
                    writer.newLine();
                }
                i++;
                writer.write(queryString);
                writer.newLine();
                writer.newLine();
            }
            writer.newLine();

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }

    }

        public String marshal( String v ) throws Exception
    {
        return v;//"<![CDATA[\"" + v + "\"]]>";
    }
}
