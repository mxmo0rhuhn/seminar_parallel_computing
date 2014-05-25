package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.CombinerInstruction;
import ch.zhaw.mapreduce.KeyValuePair;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Max
 */
public class DateAvgCombiner implements CombinerInstruction {

    private static final Logger LOG = Logger.getLogger(DateAvgCombiner.class.getName());

    @Override
    public List<KeyValuePair> combine(Iterator<KeyValuePair> keyValuePairIterator) {
        LOG.entering(getClass().getName(), "combine");

        Map<String, Double[]> kvPairs = new HashMap<>();
        List<KeyValuePair> returnList = new ArrayList<>();
        String key;
        Double[] tmp;

        while(keyValuePairIterator.hasNext()) {
           KeyValuePair pair = keyValuePairIterator.next();

            if(kvPairs.containsKey(pair.getKey())) {
                tmp = kvPairs.get(pair.getKey());
                tmp[0]++;
                tmp[1] += Integer.parseInt(pair.getValue());
                kvPairs.put(pair.getKey(), tmp);
            } else {
                kvPairs.put(pair.getKey(), new Double[]{1.0, Double.parseDouble(pair.getValue())});
            }
        }
        Iterator mapIterator = kvPairs.keySet().iterator();

        while(mapIterator.hasNext()) {
            key = (String) mapIterator.next();
            tmp = kvPairs.get(key);
            LOG.info("Combined " + tmp[0] + " tweets at " + key);
            returnList.add(new KeyValuePair(key, "" + (tmp[1]/tmp[0])));
        }
        return returnList;
    }
}
