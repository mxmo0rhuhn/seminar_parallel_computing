package ch.zhaw.parallelComputing.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.parallelComputing.model.Computation;
import ch.zhaw.parallelComputing.view.ConsoleObserver;
import ch.zhaw.mapreduce.MapReduceFactory;

/**
 * This class is starting the map-reduce application
 * 
 * @author Max
 */
public class ProjectLauncher {
	
	private static final Logger LOG = Logger.getLogger(ProjectLauncher.class.getName());

	public static void main(String[] args) {
		new ProjectLauncher();
	}

	public ProjectLauncher() {

        // Input file
        String path = "raw.csv";
        // Number of tweets per MAP task
        int offset = 10;
        String logfile = new Date().getTime() + " log.txt";
        boolean activeWindow = true;

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("parallelComputing.properties"));
            path = prop.getProperty("path", path);
            offset = Integer.parseInt(prop.getProperty("offset", "" + offset));
            logfile = prop.getProperty("logfile", logfile);
            activeWindow = Boolean.valueOf(prop.getProperty("window", "" + activeWindow));
		} catch (IOException e) {
			// Properties could not be load - proceed with defaults
		}

		LOG.log(Level.INFO, "Computation Config: Path={0} Offset={1} Logfile={2}", new Object[]{path, offset, logfile});

		MapReduceFactory.getMapReduce().start();

		Computation validator = new Computation();
		validator.addObserver(new ConsoleObserver(logfile, activeWindow));
        validator.start(path, offset);

		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
