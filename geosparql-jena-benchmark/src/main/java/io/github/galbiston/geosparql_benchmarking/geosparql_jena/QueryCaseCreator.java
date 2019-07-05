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
package io.github.galbiston.geosparql_benchmarking.geosparql_jena;
import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.results_validation.QueryResultSeverity;
import io.github.galbiston.geosparql_benchmarking.results_validation.QueryResultType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class QueryCaseCreator {

    public void createQueryCaseFile(String filename, String queryName, String queryType,
            String queryString, String expectedResFile, String data, String resultsFile, String order, 
            QueryResultType validator, QueryResultSeverity qrSeverity) {

        QueryCase queryCase = createQuery(queryName, queryType, queryString, expectedResFile, data, resultsFile, order, validator, qrSeverity);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(QueryCase.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //This has not worked - for saving the QueryString as CDATA
            //            jaxbMarshaller.setProperty( "com.sun.xml.internal.bind.characterEscapeHandler", 
            //                    new CharacterEscapeHandler() {
            //                @Override
            //                public void escape( char[] ac, int i, int j, boolean flag, Writer writer ) throws IOException
            //                {
            //                    writer.write( ac, i, j );// do not escape
            //                }
            //               });

            //Print XML String to Console
            jaxbMarshaller.marshal(queryCase, System.out);
            // Write to File            
            jaxbMarshaller.marshal(queryCase, new File(filename));


        } catch (JAXBException e) {
            e.printStackTrace();
        } 

         readQueryXMLFile(filename);

    }
    
    public QueryCase readQueryXMLFile (String filename) {
        QueryCase queryCase;       

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(QueryCase.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            System.out.println("Output from XML File: ");
            Unmarshaller um = jaxbContext.createUnmarshaller();

            try {
               queryCase = (QueryCase) um.unmarshal(new FileReader(filename));
               System.out.println(" queryName: " + queryCase.getQueryName());
               System.out.println(" queryType: " + queryCase.getQueryType());
               
               Integer i = 1;
               for(String q : queryCase.getQueryStrings()) {
                 System.out.println(" queryStrings : " + i++ + " : " + q);
               }
               
               i = 1;
               for(String q : queryCase.getExpectedResultsFileNames()) {
                 System.out.println(" ExpectedResultsFileNames : " + i++ + " : " + q);
               }         
               
               i = 1;
               for(String q : queryCase.setQueryResultsFileNames()) {
                 System.out.println(" QueryResultsFileNames : " + i++ + " : " + q);
               }               
               
               i = 1;
               for(String q : queryCase.getDatasetFileNames()) {
                 System.out.println(" DatasetFileNames : " + i++ + " : " + q);
               }

               i = 1;
               for(String q : queryCase.getQueryOrderBys()) {
                 System.out.println(" QueryOrderBys : " + i++ + " : " + q);
               }
               
               i = 1;
               for(QueryResultType q : queryCase.getQueryResultType()) {
                 System.out.println(" QueryResultType : " + i++ + " : " + q.name());
               }
               
               i = 1;
               for(QueryResultSeverity q : queryCase.getQueryResultSeverity()) {
                 System.out.println(" QueryResultSeverity : " + i++ + " : " + q.name());
               }              
               return queryCase;
            } catch (FileNotFoundException ex) {
               Logger.getLogger(QueryCaseCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        } catch (JAXBException e) {
             Logger.getLogger(QueryCaseCreator.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;//caught by exception
    }

    public String marshal( String v ) throws Exception
    {
        return v;//"<![CDATA[\"" + v + "\"]]>";
    }

    public QueryCase createQuery(String queryName, String queryType, 
            String queryString, String expectedResFile, String data, String resultsFile, String order,
            QueryResultType validator, QueryResultSeverity qrSeverity) {
        QueryCase queryCase;
        try {
            queryCase = new QueryCase(queryName, queryType, marshal(queryString), expectedResFile, data, resultsFile, order, validator, qrSeverity);
            return queryCase;
        } catch (Exception ex) {
            Logger.getLogger(QueryCaseCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
