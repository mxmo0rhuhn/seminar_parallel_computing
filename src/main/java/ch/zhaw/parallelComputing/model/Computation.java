package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.MapReduce;
import ch.zhaw.mapreduce.MapReduceFactory;

import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * @author Max
 * 
 */
public class Computation extends Observable{

	private final MapReduce computer;


	public Computation() {
		computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper() , new DateAvgReducer(), new DateAvgCombiner(), null);
	}

	public void start(String filename, int offset) {
		Map<String, List<String>> results = computer.runMapReduceTask(new FileIterator(filename, new Long(offset)));

	    super.setChanged();
 	    super.notifyObservers(results);

        for(String key : results.keySet()) {
            System.out.println("Key = "+ key + " Value = " + results.get(key));
        }
	}
}
