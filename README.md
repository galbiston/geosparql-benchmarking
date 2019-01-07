# Geosparql Benchmarking

This Java framework is for testing the performance of three RDF spatial databases: Geosparql-Jena, Parliament and Strabon.
The framework tests a subset of the GeoSPARQL standard using the queries and datasets based on the Geographica project (http://geographica.di.uoa.gr/) and the paper "Geographica: A Benchmark
for Geospatial RDF Stores". 
A core library is used for the benchmark with two interfaces needing to be implemented to add additional test systems.

The dependencies of each test system being benchmarked must be independently satisfied.
The GeoSPARQL-Jena library is a Maven dependency and so does not have any external dependencies (more information at https://github.com/galbiston/geosparql-jena).

## Command Line Parameters
Parameters can be provided through the command line and use the JCommander library. 
Details on parameters can be found using '--help' or '-h' option (or examining `execution.cli.ExecutionParameters`).

GeoSPARQL-Jena has an additional parameter '--systemType' or '-st' to specify the system configuration. Options are TDB, TDB_UNION, MEMORY, MEMORY_UNION or NO_INDEX.

##Benchmark Test Systems
The framework is a Gradle multi-part project. Each element of the framework is a separate project to avoid Classpath conflicts between test systems.

### Core
Contains the classes and interfaces to execute the benchmark. New test systems can be incorporated by implementing the `execute.TestSystem` and `execute.TestSystemFactory` interfaces.
The `execute.TestSystem` interface provides the individual instances for each iteration of the benchmark queries. 
The `execute.TestSystemFactory` interface handles the dataset loading, building instances of `execute.TestSystem` and results location. (Dataset loading is only required to benchmark dataset loading and can be undertaken externally.)
Benchmarks can be executed using the API or via the command line.

### Geosparql-Jena

Setup: There is one step to follow in setting up Geosparql-Jena. That is running the project or calling GeoSPARQLSupport.loadFunctions() in a Java application. This is already done by the test system.

geopsarql_vocab_all.rdf - This file provides the schema for Geosparql 1.0. The file is loaded into the dataset prior to performing RDFS inferencing. Replacing this file and re-running the dataset clearing and loading will provide the latest version of the Geosparql, or a customised, schema. It is available from: https://github.com/galbiston/geosparql-jena

stats.opt - Apache Jena TDB has a TDB Optimizer that provides counts of resources and properties in the dataset. In the benchmarking framework this would be regenerated each time the dataset is loaded and included in the load timings. This file is provided for reference of the counts in the whole benchmarking dataset should there be an issue with future optimising then it should be placed in the geosparql_jena_tdb folder. More information on the TDB Optimizer is available from: https://jena.apache.org/documentation/tdb/optimizer.html#running-tdbstats

geosparql_jena_tdb - The GeoSPARQL-Jena datasets are stored in this folder. It is a Apache Jena TDB2 folder. Deleting this folder will require reloading of the datasets.

### Parliament

Setup: There are several steps to follow in setting up Parliament. These have been followed to setup the benchmarking framework in a Windows 10 x64 environment. Every effort has been made to ensure the benchmarking framework is platform agnostic or easily adaptable. The Parliament distributions contain a more up-to date User Guide than provided on the main website page. More information and downloads from: http://parliament.semwebcentral.org/ and http://semwebcentral.org/frs/?group_id=159

Unsatisfied Link Error - The ParliamentBenchmarking project build.gradle file sets up the two required environment variables (in the "run.first" command) to the appropriate paths in the project folder. Parliament documentation has a section with more advice on resolving this problem. Key points are: Try to run ParliamentAdmin. Set Environment Variables PATH for the Parliament DLL folder and PARLIAMENT_CONFIG_PATH for ParliamentConfig.txt file path so that they are available on the java.library.path property (this can be done in the ParliamentBenchmarking build.gradle file).

Apache Jena version - Parliament uses a legacy version of Jena (jena-arq:2.9.4) that has Classpath conflicts with later versions. This is not a problem when using Geosparql-Jena (which is dependent on jena:3.7.0) and Parliament in separate projects. The Core project has no dependencies on Apache Jena.

ParliamentConfig.txt - Configuration of Parliament is through this file in the parliament-release folder. It includes the the knowledge base name and location (kbDirectoryPath) and RDFS inferencing (runAllRulesAtStartup, inferRDFSClass, inderRdfsResource). More information is contained in the Parliament user guide contained in the BenchmarkingDocs folder.

parliament_kb - The Parliament knowledge base contains the loaded datasets and their indexes. Deleting this folder will require reloading of the datasets.

### Strabon

Setup: There are several steps to follow in setting up Strabon. These have been followed to setup the benchmarking framework in a Windows 10 x64 environment. Every effort has been made to ensure the benchmarking framework is platform agnostic or easily adaptable. A key manual step is the creation of the PostGIS template database called "template_postgis" which is used during dataset loading trials. The single dataset loading and query benchmarking assume that a "endpoint" database has been created using this template (see pgAdmin instructions) with port=5432, host="localhost", user="postgres" and password="postgres". These are all the same values as provided in the Strabon setup instructions and can be modified in the Strabon project Main class. Setup steps are available from: http://www.strabon.di.uoa.gr/

postgresql.conf - Tuning of Postgres requires modification of this file in PostrgreSQL/version/data. A copy of the file used is provided in the Strabon project folder. The appended values need to occur once in the file so existing values may need commenting out (e.g. shared_buffers, max_connections). The Strabon suggested values do not align with those on the "Tuning Your PostgreSQL Server" wiki page (https://wiki.postgresql.org/wiki/Tuning_Your_PostgreSQL_Server) or exceed maximums to start PostgreSQL10 (on Windows at least). Adjustment of the memory values resulted in repeated issues with Strabon running out of shared memory. More information is available in the Strabon distribution README available from: http://hg.strabon.di.uoa.gr/Strabon and https://github.com/esarbanis/strabon

## Geographica Datasets & Queries
The benchmark queries and datasets have been based on the Geographica project (http://geographica.di.uoa.gr/).
The datasets use the pre-GeoSPARQL 1.0 coordinate reference system of CRS84 and should be converted to WGS84 as warnings and errors were encountered with CRS84. 
Code to convert between the CRS84 and WGS84 are provided in the GeoSPARQL-Jena project using the GeoSPARQL-Jena library.

The queries published for the Geographica project are included in the framework but have been corrected for namespace errors etc.
The Geographica splits queries into standalone Micro benchmark queries and related Macro benchmark queries.
In the Macro benchmark, the framework selects a different random element for each iteration but re-uses for the whole iteration across the multiple queries.

The Geographica benchmark is not a full conformance test of the GeoSPARQL benchmark and some work has been undertaken to test the wider functionality of the standard.
This would provide easier comparison between test systems as to the functionality they provide but is an area of future work.
Another area of future work is confirming and comparing the results produced by each test system. Currently only performance is measured with checks for queries not producing errors or timing out.
This is mainly due to the Geographica benchmark not providing expected results for the queries on the datasets.

The queries can be loaded as a full or sub set of queries for each query set.
The Geographica benchmark assumes that each dataset is in a separate named graph and so each query contain `GRAPH` clauses.
A set of queries without these `GRAPH` clauses is provided and are termed "union" queries.

### Geographica Micro Benchmark
* All
* Non-Topological
* Spatial Joins
* Spatial Selections

### Geographica Macro Benchmark
* All
* Map Search and Browsing
* Rapid Mapping
* Reverse Geocoding

### GeoSPARQL Conformance (Incomplete)
* All
* Geometry Extension
* Geometry Extension Properties
* Geometry Extension Query Functions
* Geometry Topology Extension      
* Topology Vocabulary
* Query Rewrite Extension

## Benchmarking Versions
The following versions of the test systems were successfully utilised with the framework.

* GeoSPARQL-Jena: 1.0.0 (Apache Jena 3.7.0)
* Parliament: 2.7.10 released 2016-01-06
* Strabon: 3.3.2-SNAPSHOT on 2018-03-22 (PostgreSQL 10.3, PostGIS 2.4) - No releases could be found.

## TODO
* Conformance testing of GeoSPARQL standard: partial implementation of queries has been undertaken but needs finalising.
* Results comparison: automatic comparison of tables of generated results need comparing between test systems and ideal datasets.
