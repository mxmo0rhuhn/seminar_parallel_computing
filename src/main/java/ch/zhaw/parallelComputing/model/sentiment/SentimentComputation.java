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

package ch.zhaw.parallelComputing.model.sentiment;

import au.com.bytecode.opencsv.CSVWriter;
import ch.zhaw.mapreduce.MapReduce;
import ch.zhaw.mapreduce.MapReduceFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * @author Max
 */
public class SentimentComputation extends Observable {

    private final MapReduce computer;
    private boolean hasResults;

    public SentimentComputation() {
        hasResults = true;
        computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper(), new DateAvgReducer(), new DateAvgCombiner(), null);
    }

    public void start(Iterator it, String outFile) {
        hasResults = false;
        super.setChanged();
        super.notifyObservers();

        Map<String, List<String>> result = computer.runMapReduceTask(it);
        writeResults(result, outFile);

        hasResults = true;
        super.setChanged();
        super.notifyObservers(result);
    }

    public boolean hasResults() {
        return hasResults;
    }

    private void writeResults(Map<String, List<String>> resultListMap, String filename) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filename, false));
            String[] entries;

            for (String key : resultListMap.keySet()) {
                // + 1 for key
                List<String> results = resultListMap.get(key);
                entries = new String[results.size() + 1];
                entries[0] = key;
                int i = 1;
                for (String result : results) {
                    entries[i] = result;
                    i++;
                }
                writer.writeNext(entries);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
