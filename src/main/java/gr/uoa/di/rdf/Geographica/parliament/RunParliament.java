package gr.uoa.di.rdf.Geographica.parliament;

import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;

import org.apache.log4j.Logger;

public class RunParliament extends RunSystemUnderTest {
	static Logger logger = Logger
			.getLogger(RunParliament.class.getSimpleName());

	// @Override
	// protected void addOptions() {
	// super.addOptions();
	// }

	// @Override
	// protected void logOptions() {
	// super.logOptions();
	// }

	protected void initSystemUnderTest() throws Exception {
		sut = new ParliamentSUT();
	}

	public static void main(String[] args) throws Exception {
		RunSystemUnderTest runParliament = new RunParliament();
		
		runParliament.run(args);
	}
}
