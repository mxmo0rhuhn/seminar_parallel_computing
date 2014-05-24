package ch.zhaw.parallelComputing.model;

import java.util.Iterator;
import java.util.logging.Logger;

import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ReduceEmitter;
import ch.zhaw.mapreduce.ReduceInstruction;

public class DateAvgReducer implements ReduceInstruction {

	@Override
	public void reduce(ReduceEmitter emitter, String key, Iterator<KeyValuePair> values) {
        Average avg = new Average();

        while(values.hasNext()) {
            avg.add(Integer.parseInt(values.next().getValue()));
        }

        emitter.emit(avg.getAvg());
    }

    private class Average {
        int num;
        int sum;

        public  Average() {
            num = 0;
        }

        public  Average(int init) {
            num = 1;
            sum = init;
        }

        public Average add(int i) {
            num ++;
            sum += i;
            return this;
        }

        public String getAvg() {
            return Integer.toString(sum/num);
        }
    }
}

