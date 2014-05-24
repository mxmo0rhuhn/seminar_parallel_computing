package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.CombinerInstruction;
import ch.zhaw.mapreduce.KeyValuePair;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Max
 */
public class DateAvgCombiner implements CombinerInstruction {

    private static final String IN_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String OUT_DATE_FORMAT = "yyyy-MM-dd-HH.mm";
    private static final String DEFAULT_OUT_DATE = "0000-00-00-00.00";

    // Sat, 24 May 2014 11:44:57 +0000
    SimpleDateFormat dateParser = new SimpleDateFormat(IN_DATE_FORMAT);
    SimpleDateFormat targetDate = new SimpleDateFormat(OUT_DATE_FORMAT);

    @Override
    public List<KeyValuePair> combine(Iterator<KeyValuePair> keyValuePairIterator) {
        Map<String, Average> kvPairs = new HashMap<String, Average>();
        List<KeyValuePair> returnList = null;
        String key;

        while(keyValuePairIterator.hasNext()) {
           KeyValuePair pair = keyValuePairIterator.next();

            try {
                key = targetDate.format(new Timestamp(dateParser.parse(pair.getKey()).getTime()).toString());
            } catch (ParseException e) {
                key = DEFAULT_OUT_DATE;
            }

            if(kvPairs.containsKey(key)) {
                kvPairs.put(key, kvPairs.get(key).add(Integer.parseInt(pair.getValue())));
            } else {
                kvPairs.put(key, new Average(Integer.parseInt(pair.getValue())));
            }
        }
        Iterator mapIterator = kvPairs.keySet().iterator();
        while(mapIterator.hasNext()) {
           Map.Entry<String, Average> entry = (Map.Entry)mapIterator.next();
            returnList.add(new KeyValuePair(entry.getKey(), entry.getValue().getAvg()));
        }
        return returnList;
    }

    private class Average {
        int num;
        int sum;

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
