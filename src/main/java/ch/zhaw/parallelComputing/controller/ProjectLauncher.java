package ch.zhaw.parallelComputing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.parallelComputing.model.DateShuffleFactory;
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

	/**
	 * Startet die Applikation und ruft die benötigten Aufgaben
	 * 
	 * @param args
	 *            Wenn ein Parameter übergeben wird, ein fix definierter Stop-Wert. Wenn zwei
	 *            Parameter übergeben werden: Erster Wert fix definierter Stop-Wert, zweiter Wert
	 *            fest definierter Start-Wert.
	 */
	public static void main(String[] args) {
		new ProjectLauncher();
	}

	public ProjectLauncher() {

		// Pfad, an dem die Output Files gespeichert werden
		String path = System.getProperty("java.io.tmpdir");
		File outDirectory = null;

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("parallelComputing.properties"));

			path = prop.getProperty("path");
		} catch (IOException e) {
			// konnten nicht geladen werden - weiter mit oben definierten defaults
		}
		
		LOG.log(Level.INFO, "Computation Config: Path={0} ", new Object[]{path });

		try {
			outDirectory = new File(path);
			if (!outDirectory.exists()) {
				if (!outDirectory.mkdirs()) {
					throw new IllegalArgumentException(outDirectory
							+ " does not exist and is not writable.");
				}
			} else if (!outDirectory.isDirectory()) {
				throw new IllegalArgumentException(outDirectory + " exists but is not a directory");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		MapReduceFactory.getMapReduce().start();

		Computation validator = new Computation(new DateShuffleFactory(outDirectory));
		validator.addObserver(new ConsoleObserver(outDirectory));


		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
