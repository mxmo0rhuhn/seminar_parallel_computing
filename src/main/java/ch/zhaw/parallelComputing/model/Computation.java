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
    private final Long offset;

    public boolean isResults() {
        return results;
    }

    private boolean results;

	public Computation(Long offset) {
        results = true;
		computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper() , new DateAvgReducer(), new DateAvgCombiner(), null);
        this.offset = offset;
	}

	public void start(String filename) {
        results = false;
        super.setChanged();
        super.notifyObservers();

		Map<String, List<String>> result = computer.runMapReduceTask(new FileIterator(filename, offset));

        results = true;
	    super.setChanged();
 	    super.notifyObservers(result);
	}
}
