/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package gr.uoa.di.rdf.Geographica.experiments;

import gr.uoa.di.rdf.Geographica.queries.MacroMapSearchQueriesSet;
import gr.uoa.di.rdf.Geographica.queries.QueriesSet.QueryStruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import org.apache.log4j.Logger;
import geosparql_benchmarking.experiments.TestSystem;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 */
public class MacroMapSearchExperiment extends MacroExperiment {

    public MacroMapSearchExperiment(TestSystem sut, int repetitions, int timeoutSecs, int runTimeInMinutes, String logPath) throws IOException {
        super(sut, repetitions, timeoutSecs, runTimeInMinutes, logPath);
        logger = Logger.getLogger(MacroMapSearchExperiment.class.getSimpleName());
        queriesSet = new MacroMapSearchQueriesSet(sut);
        this.runTimeInMinutes = runTimeInMinutes;
    }

    public MacroMapSearchExperiment(TestSystem sut, int repetitions, int timeoutSecs, int runTimeInMinutes, int[] queriesToRun, String logPath) throws IOException {
        super(sut, repetitions, timeoutSecs, runTimeInMinutes, logPath);
        logger = Logger.getLogger(MacroMapSearchExperiment.class.getSimpleName());
        queriesSet = new MacroMapSearchQueriesSet(sut);
        this.runTimeInMinutes = runTimeInMinutes;
        this.queriesToRun = queriesToRun;
    }

    @Override
    public void run() {

        QueryStruct queryStruct;
        long time;

        measurements = new long[4];

        // It's pointless to consider warm and cold caches about updates
        sut.clearCaches();
        sut.initialize();
        logger.info("Clear caches and initialize sut before updates");

        int repetitionI = 0;
        int queryI = 0;
        long t1 = System.currentTimeMillis();

        if (queriesToRun == null) {
            queriesToRunN = queriesSet.getQueriesN();
        } else {
            queriesToRunN = queriesToRun.length;
        }

        while (true) {
            try {
                for (int i = 0; i < queriesToRunN; i++) {
                    if (queriesToRun != null) {
                        queryI = queriesToRun[i];
                    } else {
                        queryI = i;
                    }

                    queryStruct = queriesSet.getQuery(queryI, repetitionI);

                    logger.info("Executing query (" + queryI + ", " + repetitionI + "): " + queryStruct.getQuery());

                    measurements = sut.runQueryWithTimeout(queryStruct.getQuery(), timeoutSecs);

                    if (queryI == 0) {
                        HashMap<String, String> firstResult = sut.getFirstResult();
                        String geo = firstResult.get("wkt");
                        ((MacroMapSearchQueriesSet) queriesSet).setCurrentPoint(geo);
                    }

                    logger.info("Query executed ("
                            + queryI + ", " + repetitionI + "): "
                            + measurements[0] + " + "
                            + measurements[1] + " = "
                            + measurements[2] + ", "
                            + measurements[3] + "\n");

                    try {
                        printStatisticsPerQuery(this.getClass().getSimpleName(), queryI, repetitionI, queryStruct, measurements);
                    } catch (IOException e) {
                        logger.error("While printing statistics (cold, " + queryI + ")");
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String stacktrace = sw.toString();
                        logger.error(stacktrace);
                    }
                }

                time = System.currentTimeMillis() - t1;
                logger.info("Executed " + repetitionI + " - " + time + "/" + runTimeInMinutes * 60000);
                if (time > runTimeInMinutes * 60000) {
                    logger.info("Finish at: " + time);
                    break;
                }
                repetitionI++;
            } catch (Exception e) {
                logger.error("While evaluating query(cold, "
                        + queryI + ", " + repetitionI + ")");
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stacktrace = sw.toString();
                logger.error(stacktrace);
                sut.close();
                sut.clearCaches();
                sut.initialize();
            }
        }

        try {
            printStatisticsAll(this.getClass().getSimpleName(), repetitionI, time);
        } catch (IOException e) {
            logger.error("While printing statistics (cold, " + queryI + ")");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            logger.error(stacktrace);
        }

        sut.close();
        sut.clearCaches();

    }
}
