package ch.zhaw.parallelComputing.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.mapreduce.KeyValuePair;

/**
 * @author Max
 * 
 */
public class HTMLFormatter implements Runnable {

	private final File outDirectory;
	private final Iterator<Entry<String, List<KeyValuePair>>> results;

	public HTMLFormatter(File outDirectory, Iterator<Entry<String, List<KeyValuePair>>> results) {
		this.outDirectory = outDirectory;

		// Sollte die Struktur Iterator<Entry<Restklasse , List<KeyValuePair<Restklasse,
		// Permutationen >> haben
		this.results = results;
	}

	@Override
	public void run() {

		int counter = 0;
		File outFile = null;

		if (results.hasNext()) {
			while (results.hasNext()) {

				Entry<String, List<KeyValuePair>> curEntry = results.next();

				if (counter == 0) {
					outFile = new File(outDirectory, "AnzahlElemente" + curEntry.getKey() + ".html");
					fileWriteLn("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html>", outFile);
					fileWriteLn("<html><body> ", outFile);

				} else {
					// Darf eigentlich nicht vorkommen
					System.out.println("HILFE: Es ist mehr als eine Anzahl an Elementen nach MAP Phase vorhanden");
					fileWriteLn(
							"<p><strong>HILFE: Es ist mehr als eine Anzahl an Elementen nach MAP Phase vorhanden</p></strong>",
							outFile);
				}
				List<KeyValuePair> entryList = curEntry.getValue();

				for (KeyValuePair curPermutation : entryList) {
					formatPermutation(curPermutation, outFile);
				}

				counter++;
			}
			fileWriteLn("</body></html>", outFile);
		} else {

			Logger.getLogger(HTMLFormatter.class.getName()).log(Level.INFO,
					"HTMLFormatter: Sollte HTML aufbereiten aber Ergebnis der MAP-Phase war leer");
		}

	}

	/**
	 * Bereitet eine Permutation als HTML auf
	 */
	private void formatPermutation(KeyValuePair curPermutation, File outFile) {

		BigInteger aPerm = new BigInteger(curPermutation.getValue().split(",")[0]);
		int iPerm = Integer.parseInt(curPermutation.getValue().split(",")[1]);
		int neut = Integer.parseInt(curPermutation.getValue().split(",")[2]);

		int mod = Integer.parseInt(curPermutation.getKey());

		fileWriteLn("<h1>" + curPermutation.getValue() + "</h1>", outFile);

		// neutrales element
		fileWriteLn("<h2>Neutrales Element</h2>", outFile);
		fileWriteLn("<p>E" + neut + "</p>", outFile);

		printInverse(outFile, iPerm, mod);

		fileWriteLn(generateAdditionHTML(aPerm, mod),outFile);

	}

	String generateAdditionHTML(BigInteger aPerm, int mod) {
		String returnString = "<h2>Verknüpfungstabelle</h2>";
		returnString += "<table border='1'><tr><td></td>";

		// Header
		for (int i = 0; i < mod; i++) {
			returnString += "<td><strong>E" + i + "</strong></td>";
		}

		// Zeile
		for (int y = 0; y < mod; y++) {
			returnString += "</tr><tr><td><strong>E" + y + "</strong></td>";

			for (int x = 0; x < mod; x++) {

			}
		}
		returnString += "</tr></table><hr>";
		
		return returnString;
	}

	private void printInverse(File outFile, int iPerm, int mod) {
		// Inverse
		fileWriteLn("<h2>Inverse</h2>", outFile);
		fileWriteLn("<table border='1'><tr>", outFile);

		// Header
		for (int i = 0; i < mod; i++) {
			fileWriteLn("<td><strong>E" + i + "</strong></td>", outFile);
		}
		fileWriteLn("</tr><tr>", outFile);

		// Zeile
		for (int x = 0; x < mod; x++) {
		}
		fileWriteLn("</tr></table>", outFile);
	}

	public void fileWriteLn(String line, File outFile) {

		BufferedWriter curFW = null;
		try {
			try {
				curFW = new BufferedWriter(new FileWriter(outFile, true));
				curFW.write(line);
				curFW.newLine();
			} finally {
				if (curFW != null) {
					curFW.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
