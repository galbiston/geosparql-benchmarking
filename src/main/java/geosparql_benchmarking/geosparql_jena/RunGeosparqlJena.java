package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.Main;
import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunGeosparqlJena extends RunSystemUnderTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunGeosparqlJena.class);

    @Override
    protected void initSystemUnderTest() throws Exception {
        sut = new GeosparqlJenaSUT(Main.GEOSPARQL_JENA_TDB_FOLDER);
    }

    public static void main(String[] args) throws Exception {
        RunSystemUnderTest runGeosparql = new RunGeosparqlJena();

        runGeosparql.run(args);
    }
}
