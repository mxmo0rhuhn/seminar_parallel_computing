/**
 * 
 */
package ch.zhaw.parallelComputing.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Some logging while the computation is running
 *
 * @author Max
 * 
 */
public class ConsoleObserver implements Observer {

	private final DateFormat logTsdFormat = new SimpleDateFormat("hh:mm:ss:SS");
	private final File outFile;
	private final ConsoleOutput outConsole;
	private Date startTSD;

	public ConsoleObserver(String out ) {
		startTSD = new Date();

		outFile = new File(out);
		outConsole = new ConsoleOutput();

		printStreams("Seminar Parallel Computing - ZHAW FS 2014 - Max Schrimpf");
		redirectSystemStreams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		Map<String, List<String>> results = (Map<String, List<String>>) arg;

		printStreams("----------------------------------------------------------------");

        for(String key : results.keySet()) {
            printStreams("Key = "+ key + " Value = " + results.get(key).toString());
        }

		Date stopTSD = new Date();
		long difference = stopTSD.getTime() - startTSD.getTime();

		long diffSeconds = difference / 1000 % 60;
		long diffMinutes = difference / (60 * 1000) % 60;
		long diffHours = difference / (60 * 60 * 1000) % 24;

		startTSD = new Date();
		printStreams(String.format("Elapsed time ~ %02d:%02d:02d",diffHours, diffMinutes, diffSeconds));
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				printStreams(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				printStreams(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	public void printStreams(String line) {
		String tsd = logTsdFormat.format(Calendar.getInstance().getTime());

		outConsole.println(tsd + " " + line);
		fileWriteLn(tsd + " " + line);
	}

	public void fileWriteLn(String line) {
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
