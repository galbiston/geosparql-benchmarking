Geosparql Benchmarking

This framework is for the testing the performance and feature set of three RDF spatial databases: GeosparqlJena, Parliament and Strabon.

--GeosparqlJena

Setup: There is one step to follow in setting up GeosparqlJena. That is running the project or calling GeoSPARQLSupport.loadFunctions() in a Java application. This is already done by the test system.

geopsarql_vocab_all.rdf - This file provides the schema for Geosparql 1.0. The file is loaded into the dataset prior to performing RDFS inferencing. Replacing this file and re-running the dataset clearing and loading will provide the latest version of the Geosparql, or a customised, schema. It is available from: TODO

stats (reference).opt - Apache Jena TDB has a TDB Optimizer that provides counts of resources and properties in the dataset. In the benchmarking framework this would be regenerated each time the dataset is loaded and included in the load timings. This file is provided for reference of the counts in the whole benchmarking dataset should there be an issue with future optimising. It should be placed in the geosparql_jena_tdb folder. More information on the TDB Optimizer is available from: https://jena.apache.org/documentation/tdb/optimizer.html#running-tdbstats

geosparql_jena_tdb - The GeosparqlJena datasets are stored in this folder. It is a Apache Jena TDB2 folder. Deleting this folder will require reloading of the datasets.

--Parliament

Setup: There are several steps to follow in setting up Parliament. These have been followed to setup the benchmarking framework in a Windows 10 x64 environment. Every effort has been made to ensure the benchmarking framework is platfrom agnostic or easily adaptable. The Parliament distributions contain a more upto date User Guide than provided on the main page. More information and downloads from: http://parliament.semwebcentral.org/ and http://semwebcentral.org/frs/?group_id=159

Unsatisfied Link Error - Parlaiment documentation has a sectin on resolving this problem. Key points are: Try to run ParliamentAdmin. Set Environment Variables PATH for the Parliament DLL folder and PARLIAMENT_CONFIG_PATH for ParliamentConfig.txt filepath so that they are available on the java.library.path property.

Apache Jena version - Parliament uses a legacy version of Jena (jena-arq:2.9.4) that has Classpath conflicts with later versions. This is not a problem provided GeosparqlJena (which is dependent on jena:3.7.0) and Parliament are in separate projects and the Core project has no dependencies on Apache Jena.

ParliamentConfig.txt - Configuration of Parliament is through this file in the parliament-release folder. It includes the the knowledge base name and location (kbDirectoryPath) and RDFS inferencing (runAllRulesAtStartup, inferRDFSClass, inderRdfsResource). More information is contained in the Parliament user guide contained in the BenchmarkingDocs folder.

parliament_kb - The Parliament knowledge base contains the loaded datasets and their indexes. Deleting this folder will require reloading of the datasets.

--Strabon

Setup: There are several steps to follow in setting up Strabon. These have been followed to setup the benchmarking framework in a Windows 10 x64 environment. Every effort has been made to ensure the benchmarking framework is platfrom agnostic or easily adaptable. A key manual step is the creation of the PostGIS template database called "template_postgis" which is used during dataset loading trials. The single dataset loading and query benchmarking assume that a "endpoint" database has been created using this template (see pgAdmin instructions) with port=5432, host="localhost", user="postgres" and password="postgres". These are all the same values as provided in the Strabon setup instructions and can be modified in the Strabon project Main class. Setup steps are avaiable from: http://www.strabon.di.uoa.gr/

postgresql.conf - Tuning of Postgres requires modification of this file in PostrgreSQL/version/data. A copy of the file used is provided in the Strabon project folder. The appended values need to occur once in the file so existing values may need commenting out (e.g. shared_buffers, max_connections). More information is available in the Strabon distribution README available from: http://hg.strabon.di.uoa.gr/Strabon and https://github.com/esarbanis/strabon


Benchmarking Versions

GeosparqlJena - 1.0.0 (Apache Jena 3.7.0)
Parliament - 2.7.10
Strabon - 3.3.2-SNAPSHOT (PostgreSQL 10.3, PostGIS 2.4) 