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
 * 
 */
public class SentimentComputation extends Observable{

	private final MapReduce computer;
    private boolean hasResults;

	public SentimentComputation() {
        hasResults = true;
		computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper() , new DateAvgReducer(), new DateAvgCombiner(), null);
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

            for(String key : resultListMap.keySet()) {
                // + 1 for key
                List<String> results = resultListMap.get(key);
                entries = new String[results.size()+1];
                entries[0] = key;
                int i = 1;
                for (String result : results) {
                    entries[i]  = result;
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
