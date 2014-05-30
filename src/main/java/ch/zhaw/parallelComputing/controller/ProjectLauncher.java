package ch.zhaw.parallelComputing.controller;

import ch.zhaw.mapreduce.MapReduceFactory;
import ch.zhaw.parallelComputing.model.sentiment.FileIterator;
import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;
import ch.zhaw.parallelComputing.view.ConsoleObserver;
import ch.zhaw.parallelComputing.view.GUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class is starting the map-reduce application
 *
 * @author Max
 */
public class ProjectLauncher {

    private static final Logger LOG = Logger.getLogger(ProjectLauncher.class.getName());
    private final ConsoleObserver observer;

    public static void main(String[] args) {
        new ProjectLauncher();
    }

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
            offset = Long.parseLong(prop.getProperty("offset", "" + offset));
            tweetTimestampID = Integer.parseInt(prop.getProperty("tweetTimestampID", "" + tweetTimestampID));
            tweetTextID = Integer.parseInt(prop.getProperty("tweetTextID", "" + tweetTextID));
            logfile = prop.getProperty("logfile", logfile);
            activeWindow = Boolean.valueOf(prop.getProperty("window", "" + activeWindow));

            partResultPath = prop.getProperty("partResultPath");
            List<String> strList = Arrays.asList(prop.getProperty("partResultLogIDs"));
            if (strList != null) {
                partResultLogIDs = new ArrayList<>();
                for (String s : strList) {
                    partResultLogIDs.add(Integer.valueOf(s));
                }
            }
        } catch (IOException e) {
            // Properties could not be load - proceed with defaults
        }

        LOG.log(Level.INFO, "SentimentComputation Config: Path={0} Offset={1} Logfile={2} Window={3}", new Object[]{inputPath, offset, logfile, activeWindow});
        MapReduceFactory.getMapReduce().start();
        SentimentComputation sentimentComputation = new SentimentComputation();
        GUI gui = null;

        FileIterator it = new FileIterator(offset, tweetTimestampID, tweetTextID, inputDateFormat, outputDateFormat,
                partResultPath, partResultLogIDs);
        if (activeWindow) {
            gui = new GUI(it, inputPath, sentimentComputation, this);
        }

        observer = new ConsoleObserver(logfile, sentimentComputation, gui);
        sentimentComputation.addObserver(observer);

        if (!activeWindow) {
            sentimentComputation.start(it);
            exit();
        }
    }

    public void exit() {
        observer.printStreams("Exiting");
        MapReduceFactory.getMapReduce().stop();
        System.exit(0);
    }
}
