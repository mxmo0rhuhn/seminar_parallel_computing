package ch.zhaw.parallelComputing.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;
import ch.zhaw.parallelComputing.view.ConsoleObserver;
import ch.zhaw.mapreduce.MapReduceFactory;
import ch.zhaw.parallelComputing.view.GUI;


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
        String path = "raw.csv";
        // Number of tweets per MAP task
        Long offset = 10L;
        // Default logfile
        String logfile = new Date().getTime() + " log.txt";
        // with or without GUI
        boolean activeWindow = true;

        Properties prop = new Properties();
        try {
			prop.load(new FileInputStream("parallelComputing.properties"));
            path = prop.getProperty("path", path);
            offset = Long.parseLong(prop.getProperty("offset", "" + offset));
            logfile = prop.getProperty("logfile", logfile);
            activeWindow = Boolean.valueOf(prop.getProperty("window", "" + activeWindow));
		} catch (IOException e) {
			// Properties could not be load - proceed with defaults
		}

		LOG.log(Level.INFO, "SentimentComputation Config: Path={0} Offset={1} Logfile={2} Window={3}", new Object[]{path, offset, logfile, activeWindow});
		MapReduceFactory.getMapReduce().start();
        SentimentComputation sentimentComputation = new SentimentComputation();
        GUI gui = null;

        if(activeWindow) {
            gui = new GUI(path, sentimentComputation, this);
        }

        observer = new ConsoleObserver(logfile, sentimentComputation, gui);
		sentimentComputation.addObserver(observer);

        if(!activeWindow) {
//            sentimentComputation.start(offset, path);
            exit();
        }
    }

    public void exit() {
        observer.printStreams("Exiting");
		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
