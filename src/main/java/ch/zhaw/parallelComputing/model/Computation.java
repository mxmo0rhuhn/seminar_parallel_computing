package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.MapReduce;
import ch.zhaw.mapreduce.MapReduceFactory;

import java.util.Observable;

/**
 * @author Max
 * 
 */
public class Computation extends Observable{

	private final MapReduce computer;
	

	public Computation(DateShuffleFactory residueProcessorFactory ) {
		computer = MapReduceFactory.getMapReduce().newMRTask(new TweetMapper() , new DateAvgReducer(), null, residueProcessorFactory);
	}

	private void start() {
		Map<Restklasse, Liste<Permutationen>> mit Permutationen, für die der Satz nicht gilt (Erwartungswert: Keine)
		Map<String, List<String>> results = computer.runMapReduceTask(new FileIterator(residue, permoffset));
		
	    super.setChanged(); 

 	    super.notifyObservers(results);
	}
}
