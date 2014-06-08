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

package ch.zhaw.parallelComputing.model;

import au.com.bytecode.opencsv.CSVReader;
import ch.zhaw.mapreduce.KeyValuePair;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Helper Methods for CSV File Handling
 *
 * @author Max Schrimpf
 */
public class CSVHandler {

    public static XYDataset getDataset(String filename, SimpleDateFormat timeFormat) {
        return getDataset(0,1,filename,timeFormat);
    }
    /**
     * Parses a CSV File into a dataset for plotting.
     *
     * @param filename   The filename to be parsed. By convention it has
     *                   - 1st Column: A Time representation
     *                   - 2nd Column: Numerical Data value
     * @param timeFormat The time format of the files first column
     * @return A Dataset containing the values of the CSV
     */
    public static XYDataset getDataset(int TSDIndex, int valueIndex, String filename, SimpleDateFormat timeFormat) {
        TimeSeries s2 = new TimeSeries(filename, "Domain", "Range", FixedMillisecond.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        List<KeyValuePair> keyValuePairs = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(filename));
            String[] nextLine;

            // HEADER
            nextLine = reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                try {
                    String key = timeFormat.format(timeFormat.parse(nextLine[TSDIndex]));
                    keyValuePairs.add(new KeyValuePair(key, nextLine[valueIndex]));
                } catch (ParseException e) {
                    e.printStackTrace(System.out);
                    System.out.println("Can't parse = " + nextLine[TSDIndex]);
                    System.out.println("to format   = " + timeFormat.toPattern());
                    System.out.println("returning");
                    return new TimeSeriesCollection();
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Can't open " + filename);
            return new TimeSeriesCollection();
        }

        keyValuePairs = getAverages(keyValuePairs.iterator());

        for (KeyValuePair pair : keyValuePairs) {
            // Timestamp
            try {
                Date tsd = timeFormat.parse(pair.getKey());
                Float content = Float.parseFloat(pair.getValue());
                s2.add(new FixedMillisecond(tsd), content);
            } catch (ParseException e) {
                e.printStackTrace(System.out);
                System.out.println("Can't parse = " + pair.getKey());
                System.out.println("to format   = " + timeFormat.toPattern());
                System.out.println("returning");
                return new TimeSeriesCollection();
            }
        }
        dataset.addSeries(s2);
        return dataset;
    }

    /**
     * Rerurnes the first row of the CSV file as a list
     *
     * @param input The filename
     * @return A list of column names
     */
    public static List<String> getHeaders(String input) {
        List<String> returnList = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(input));
            int i = 0;
            String[] nextLine = reader.readNext();

            for (String curLine : nextLine) {
                returnList.add(i, curLine);
                i++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return returnList;
    }
    Map<String, Double[]> kvPairs = new HashMap<>();
    List<KeyValuePair> returnList = new ArrayList<>();
    String key;
    Double[] tmp;

    public static List<KeyValuePair> getAverages(Iterator<KeyValuePair> keyValuePairIterator) {
        Map<String, Double[]> kvPairs = new HashMap<>();
        List<KeyValuePair> returnList = new ArrayList<>();
        String key;
        Double[] tmp;

        while (keyValuePairIterator.hasNext()) {
            KeyValuePair pair = keyValuePairIterator.next();

            if (kvPairs.containsKey(pair.getKey())) {
                tmp = kvPairs.get(pair.getKey());
                tmp[0]++;
                tmp[1] +=  Float.parseFloat(pair.getValue());
                kvPairs.put(pair.getKey(), tmp);
            } else {
                kvPairs.put(pair.getKey(), new Double[]{1.0, Double.parseDouble(pair.getValue())});
            }
        }
        Iterator mapIterator = kvPairs.keySet().iterator();

        while (mapIterator.hasNext()) {
            key = (String) mapIterator.next();
            tmp = kvPairs.get(key);
            returnList.add(new KeyValuePair(key, "" + (tmp[1] / tmp[0])));
        }
        return returnList;
    }
}
