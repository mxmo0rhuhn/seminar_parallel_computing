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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.zhaw.parallelComputing.view;

import ch.zhaw.parallelComputing.model.sentiment.SentimentComputation;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Logging for the sentiment computation MAP REDUCE task. Loggs the System streams into a file and optionally into a
 * GUI. Exceptions are redirected into the SEVERE LOG.
 *
 * @author Max Schrimpf
 */
public class ConsoleObserver implements Observer {

    private static final Logger LOG = Logger.getLogger(ConsoleObserver.class.getName());

    private final DateFormat logTsdFormat = new SimpleDateFormat("hh:mm:ss:SS");
    private final File outFile;
    private Date startTSD;
    private final GUI activeWindow;
    private final SentimentComputation sentimentComputation;

    public ConsoleObserver(String out, SentimentComputation sentimentComputation, GUI activeWindow) {
        startTSD = new Date();

        outFile = new File(out);
        this.activeWindow = activeWindow;
        this.sentimentComputation = sentimentComputation;

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

        if (sentimentComputation.hasResults()) {
            if (ConsoleObserver.this.activeWindow != null) {
                ConsoleObserver.this.activeWindow.enableStartButton();
            }
            Date stopTSD = new Date();
            long difference = stopTSD.getTime() - startTSD.getTime();
            long diffSeconds = difference / 1000 % 60;
            long diffMinutes = difference / (60 * 1000) % 60;
            long diffHours = difference / (60 * 60 * 1000) % 24;

            Map<String, List<String>> results = (Map<String, List<String>>) arg;

            for (String key : results.keySet()) {
                printStreams("Key = " + key + " Value = " + results.get(key).toString());
            }

            DecimalFormat df = new DecimalFormat("00");
            printStreams(String.format("Elapsed time ~ %s:%s:%s h", df.format(diffHours), df.format(diffMinutes), df.format(diffSeconds)));
            printStreams("----------------------------------------------------------------");
        } else {
            startTSD = new Date();
            printStreams("----------------------------------------------------------------");
            printStreams("SentimentComputation Started");
        }
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                printStreams(String.valueOf((char) b), false);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                printStreams(new String(b, off, len), false);
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        OutputStream log = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                LOG.severe(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                LOG.severe(new String(b, off, len));
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
        printStreams(line, true);
    }

    public void printStreams(String line, boolean newLine) {
        String tsd = logTsdFormat.format(Calendar.getInstance().getTime());

        line = (tsd + " " + line);
        if (activeWindow != null) {
            activeWindow.println(line, newLine);
        }
        fileWriteLn(line);
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
