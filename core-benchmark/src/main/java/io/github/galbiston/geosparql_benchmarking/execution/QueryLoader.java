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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class QueryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);

    public static final String readFile(String filepath) {

        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }
    }

    public static final List<QueryCase> readQuery(String filepath) {
        return Arrays.asList(readQuery(filepath, 1, null));
    }

    public static final QueryCase readQuery(String filepath, int count, String groupName) {

        String[] fileName = new File(filepath).getName().split("-");
        String queryName;
        if (fileName.length > 1) {
            queryName = fileName[0];
        } else {
            queryName = "UQ" + count;
        }

        String queryGroupName;
        if (groupName != null) {
            queryGroupName = groupName;
        } else {
            queryGroupName = "UserQuery";
        }

        return new QueryCase(queryName, queryGroupName, readFile(filepath));
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static final QueryCase readQueryXMLFile(String filepath, int count, String groupName) {
        QueryCase queryCase = null;

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(QueryCase.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

//            System.out.println("Output from XML File: " + filepath);
            Unmarshaller um = jaxbContext.createUnmarshaller();

            try {
                queryCase = (QueryCase) um.unmarshal(new FileReader(filepath));
//               System.out.println(" queryName: " + queryCase.getQueryName());
//               System.out.println(" queryType: " + queryCase.getQueryType());
//               
//               Integer i = 1; 
//               for(String q : queryCase.getQueryStrings()) {
//                 System.out.println(" queryStrings : " + i++ + " : " + q);
//               }
//               
//               i = 1;
//               for(String q : queryCase.getExpectedResultsFileNames()) {
//                 System.out.println(" ExpectedResultsFileNames : " + i++ + " : " + q);
//               }         
//               
//               i = 1;
//               for(String q : queryCase.setQueryResultsFileNames()) {
//                 System.out.println(" QueryResultsFileNames : " + i++ + " : " + q);
//               }               
//               
//               i = 1;
//               for(String q : queryCase.getDatasetFileNames()) {
//                 System.out.println(" DatasetFileNames : " + i++ + " : " + q);
//               }
//
//               i = 1;
//               for(String q : queryCase.getQueryOrderBys()) {
//                 System.out.println(" QueryOrderBys : " + i++ + " : " + q);
//               }
//               
//               i = 1;
//               for(QueryResultType q : queryCase.getQueryResultType()) {
//                 System.out.println(" QueryResultType : " + i++ + " : " + q.name());
//               }
//               
//               i = 1;
//               for(QueryResultSeverity q : queryCase.getQueryResultSeverity()) {
//                 System.out.println(" QueryResultSeverity : " + i++ + " : " + q.name());
//               }

                return queryCase;
            } catch (FileNotFoundException ex) {
                LOGGER.error("Could not open query file: {}#{}", queryCase, ex.getMessage());
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final List<QueryCase> readFolder(File directory) {
        return readFolder(directory, 0);
    }

    public static final List<QueryCase> readFolder(File directory, int count) {

        List<QueryCase> queryCases = new ArrayList<>();
        File[] files = directory.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {
                List<QueryCase> folderQueryCases = readFolder(file, count);
                count += folderQueryCases.size();
                queryCases.addAll(folderQueryCases);
            } else {
                count++;
                String filepath = file.getAbsolutePath();
                QueryCase queryCase = readQuery(filepath, count, directory.getName());
                checkIteration(queryCases, queryCase);
            }
        }
        return queryCases;
    }

    public static final List<QueryCase> readFolderXML(File directory) {
        return readFolderXML(directory, 0);
    }

    public static final List<QueryCase> readFolderXML(File directory, int count) {

        List<QueryCase> queryCases = new ArrayList<>();
        File[] files = directory.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {
                //List<QueryCase> folderQueryCases = readFolderXML(file, count);
                //count += folderQueryCases.size();
                //queryCases.addAll(folderQueryCases);
            } else {
                count++;
                String filepath = file.getAbsolutePath();
                File pf = new File(filepath);

                if (pf.isFile() && getFileExtensionName(pf).indexOf("xml") != -1) {
                    QueryCase queryCase = readQueryXMLFile(filepath, count, directory.getName());
                    checkIteration(queryCases, queryCase);
                }
            }
        }
        return queryCases;
    }

    public static String getFileExtensionName(File f) {
        if (f.getName().indexOf(".") == -1) {
            return "";
        } else {
            return f.getName().substring(f.getName().length() - 3, f.getName().length());
        }
    }

    private static void checkIteration(List<QueryCase> queryCases, QueryCase queryCase) {

        boolean isNoMatch = true;
        for (QueryCase existingCase : queryCases) {

            String queryName = existingCase.getQueryName();
            if (queryName.equals(queryCase.getQueryName())) {
                existingCase.addQueryString(queryCase.getQueryString());
                isNoMatch = false;
                break;
            }
        }

        if (isNoMatch) {
            queryCases.add(queryCase);
        }
    }

    public static final List<QueryPair> readQueryPairs(String filepath) {

        List<QueryPair> queryPairs = new ArrayList<>();
        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            while (buffer.ready()) {
                String line = buffer.readLine();
                String[] parts = line.split("\t");
                QueryPair queryPair = new QueryPair(parts[0], parts[1]);
                queryPairs.add(queryPair);
            }

        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }

        return queryPairs;
    }

}
