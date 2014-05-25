package ch.zhaw.parallelComputing.model;

import java.util.Iterator;
import java.util.logging.Logger;

import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ReduceEmitter;
import ch.zhaw.mapreduce.ReduceInstruction;

public class DateAvgReducer implements ReduceInstruction {
    private static final Logger LOG = Logger.getLogger(DateAvgReducer.class.getName());

	@Override
	public void reduce(ReduceEmitter emitter, String key, Iterator<KeyValuePair> values) {
        LOG.entering(getClass().getName(), "reduce");

        Double[] tmp = new Double[]{0.0,0.0};

        while(values.hasNext()) {
            KeyValuePair pair = values.next();
            tmp[0]++;
            tmp[1] += Double.parseDouble(pair.getValue());
        }

        emitter.emit("" + tmp[1] / tmp[0]);
    }
}

