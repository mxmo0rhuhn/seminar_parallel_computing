package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.CombinerInstruction;
import ch.zhaw.mapreduce.KeyValuePair;
import com.google.inject.internal.util.$SourceProvider;

import java.util.*;

/**
 * Created by Max
 */
public class DateAvgCombiner implements CombinerInstruction {

    @Override
    public List<KeyValuePair> combine(Iterator<KeyValuePair> keyValuePairIterator) {

        Map<String, Double[]> kvPairs = new HashMap<String, Double[]>();
        List<KeyValuePair> returnList = new ArrayList<KeyValuePair>();
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
            returnList.add(new KeyValuePair(key, "" + (tmp[1]/tmp[0])));
        }
        return returnList;
    }
}
