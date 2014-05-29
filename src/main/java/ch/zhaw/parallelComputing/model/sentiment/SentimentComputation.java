package ch.zhaw.parallelComputing.model.sentiment;

import ch.zhaw.mapreduce.MapReduce;
import ch.zhaw.mapreduce.MapReduceFactory;

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
    private boolean results;

	public SentimentComputation() {
        results = true;
		computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper() , new DateAvgReducer(), new DateAvgCombiner(), null);
	}

	public void start(Iterator it) {
        results = false;
        super.setChanged();
        super.notifyObservers();

		Map<String, List<String>> result = computer.runMapReduceTask(it);

        results = true;
	    super.setChanged();
 	    super.notifyObservers(result);
	}

    public boolean hasResults() {
        return results;
    }
}
