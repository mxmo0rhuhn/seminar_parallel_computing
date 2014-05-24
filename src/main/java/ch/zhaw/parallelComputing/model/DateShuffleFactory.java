/**
 * 
 */
package ch.zhaw.parallelComputing.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ch.zhaw.parallelComputing.view.HTMLFormatter;
import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ShuffleProcessorFactory;

/**
 * @author Max
 *
 */
public class DateShuffleFactory implements ShuffleProcessorFactory {

    private static final String IN_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String OUT_DATE_FORMAT = "yyyy-MM-dd-HH.mm";
    private static final String DEFAULT_OUT_DATE = "0000-00-00-00.00";

    // Sat, 24 May 2014 11:44:57 +0000
    SimpleDateFormat dateParser = new SimpleDateFormat(IN_DATE_FORMAT);
    SimpleDateFormat targetDate = new SimpleDateFormat(OUT_DATE_FORMAT);

	public DateShuffleFactory(File outDirectory) {
	}

	@Override
	public Runnable getNewRunnable(Iterator<Entry<String, List<KeyValuePair>>> results) {
        return null;
	}
}
