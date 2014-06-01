/*
 * Copyright (c) 2014 Max Schrimpf
 *
 * This file is part of the parallel computing term paper for the Zurich university of applied sciences.
 *
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.zhaw.parallelComputing.controller;

import ch.zhaw.mapreduce.MapReduceFactory;
import ch.zhaw.parallelComputing.model.CSVHandler;
import ch.zhaw.parallelComputing.model.sentiment.FileIterator;
import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;
import ch.zhaw.parallelComputing.view.ConsoleObserver;
import ch.zhaw.parallelComputing.view.GUI;
import ch.zhaw.parallelComputing.view.Plotter;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provides the entrance point for the application.
 * Reads the configuration and launches either the GUI or executes the program without GUI
 *
 * @author Max Schrimpf
 */
public class ProjectLauncher {

    private static final Logger LOG = Logger.getLogger(ProjectLauncher.class.getName());
    private final ConsoleObserver observer;

    public ProjectLauncher() {

        // Input file
        String inputPath = "raw.csv";
        // file for part results while the computation
        String partResultPath = null;
        // Output file
        String outputPath = "result.csv";
        // File that the output should be compared with
        String comparisonPath = "compare.csv";

        // Number of tweets per MAP task
        Long offset = 10L;

        String inputDateFormat = "EEE, dd MMM yyyy HH:mm:ss Z";
        String outputDateFormat = "yyyy-MM-dd-HH.mm";
        String comparisonDateFormat = "yyyy-MM-dd";

        // ID of the key of the MAP job
        int tweetTimestampID = 81;
        // ID of the text for sentiment analysis
        int tweetTextID = 18;
        // IDs for part result logging
        List<Integer> partResultLogIDs = null;

        // with or without GUI
        boolean activeWindow = true;

        // Default logfile
        String logfile = new Date().getTime() + " log.txt";

        Properties prop = new Properties();
        try {

            prop.load(new FileInputStream("parallelComputing.properties"));
            inputPath = prop.getProperty("inputPath", inputPath);
            outputPath = prop.getProperty("outputPath", outputPath);
            comparisonPath = prop.getProperty("comparisonPath", comparisonPath);
            inputDateFormat = prop.getProperty("inputDateFormat", inputDateFormat);
            outputDateFormat = prop.getProperty("outputDateFormat", outputDateFormat);
            comparisonDateFormat = prop.getProperty("comparisonDateFormat", comparisonDateFormat);
            offset = Long.parseLong(prop.getProperty("offset", "" + offset));
            tweetTimestampID = Integer.parseInt(prop.getProperty("tweetTimestampID", "" + tweetTimestampID));
            tweetTextID = Integer.parseInt(prop.getProperty("tweetTextID", "" + tweetTextID));
            logfile = prop.getProperty("logfile", logfile);
            activeWindow = Boolean.valueOf(prop.getProperty("window", "" + activeWindow));

            partResultPath = prop.getProperty("partResultPath");
            String listAsStr = prop.getProperty("partResultLogIDs");

            if (listAsStr != null) {
                List<String> strList = Arrays.asList(listAsStr.split("\\s*,\\s*"));
                partResultLogIDs = new ArrayList<>();
                for (String s : strList) {
                    partResultLogIDs.add(Integer.valueOf(s));
                }
            }
        } catch (IOException e) {
            // Properties could not be load - proceed with defaults
        }

        LOG.log(Level.INFO, "SentimentComputation Config: ");
        LOG.log(Level.INFO, "inputPath = " + inputPath);
        LOG.log(Level.INFO, "partResultPath = " + partResultPath);
        LOG.log(Level.INFO, "outputPath = " + outputPath);
        LOG.log(Level.INFO, "comparisonPath = " + comparisonPath);
        LOG.log(Level.INFO, "offset = " + offset);
        LOG.log(Level.INFO, "inputDateFormat  = " + inputDateFormat);
        LOG.log(Level.INFO, "outputDateFormat = " + outputDateFormat);
        LOG.log(Level.INFO, "comparisonDateFormat = " + comparisonDateFormat);
        LOG.log(Level.INFO, "tweetTimestampID = " + tweetTimestampID);
        LOG.log(Level.INFO, "tweetTextID = " + tweetTextID);
        LOG.log(Level.INFO, "partResultLogIDs = " + partResultLogIDs);
        LOG.log(Level.INFO, "activeWindow = " + activeWindow);
        LOG.log(Level.INFO, "logfile = " + logfile);

        MapReduceFactory.getMapReduce().start();
        SentimentComputation sentimentComputation = new SentimentComputation();
        GUI gui = null;

        FileIterator it = new FileIterator(offset, tweetTimestampID, tweetTextID, inputDateFormat, outputDateFormat,
                partResultPath, partResultLogIDs);
        if (activeWindow) {
            gui = new GUI(it, inputPath, outputPath, comparisonPath, comparisonDateFormat, sentimentComputation, this);
        }

        observer = new ConsoleObserver(logfile, sentimentComputation, gui);
        sentimentComputation.addObserver(observer);

        if (!activeWindow) {
            sentimentComputation.start(it, outputPath);
            Plotter.getPlotAsPNG("Comparison",
                    CSVHandler.getDataset(outputPath, new SimpleDateFormat(outputDateFormat)),
                    CSVHandler.getDataset(comparisonPath, new SimpleDateFormat(comparisonDateFormat)));
            exit();
        }
    }

    public static void main(String[] args) {
        new ProjectLauncher();
    }

    public void exit() {
        observer.printStreams("Exiting");
        MapReduceFactory.getMapReduce().stop();
        System.exit(0);
    }
}
