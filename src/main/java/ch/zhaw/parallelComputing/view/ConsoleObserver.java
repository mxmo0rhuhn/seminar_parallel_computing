/**
 * 
 */
package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.model.TweetMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Some logging while the computation is running
 *
 * @author Max
 * 
 */
public class ConsoleObserver implements Observer {

    private static final Logger LOG = Logger.getLogger(ConsoleObserver.class.getName());

	private final DateFormat logTsdFormat = new SimpleDateFormat("hh:mm:ss:SS");
	private final File outFile;
	private final ConsoleOutput outConsole;
	private Date startTSD;

	public ConsoleObserver(String out, boolean activeWindow) {
		startTSD = new Date();

		outFile = new File(out);
		outConsole = new ConsoleOutput(activeWindow);

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
            printStreams("Key = " + key + " Value = " + results.get(key).toString());
        }

		Date stopTSD = new Date();
		long difference = stopTSD.getTime() - startTSD.getTime();
        startTSD = new Date();

        DecimalFormat df = new DecimalFormat("00");
		long diffSeconds = difference / 1000 % 60;
		long diffMinutes = difference / (60 * 1000) % 60;
		long diffHours = difference / (60 * 60 * 1000) % 24;

		printStreams(String.format("Elapsed time ~ %s:%s:%s h",df.format(diffHours), df.format(diffMinutes), df.format(diffSeconds)));
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

        OutputStream log = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                LOG.fine(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                LOG.fine(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(log, true));
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
