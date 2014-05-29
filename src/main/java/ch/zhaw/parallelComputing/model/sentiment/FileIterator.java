package ch.zhaw.parallelComputing.model.sentiment;

import org.apache.mina.util.Base64;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Slices the input file for processing
 * 
 * @author Max
 * 
 */
public class FileIterator implements Iterator<String> {

    private static final Logger LOG = Logger.getLogger(FileIterator.class.getName());
	//  The size of the slices
	private final Long offset;
    private final BufferedReader reader;
    private boolean hasNext;

	public FileIterator(String filename, Long offset) {
        hasNext = true;
        BufferedReader tmp = null;
		this.offset = offset;
        try {
            tmp = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
            LOG.severe("ERROR in MAP step!!!");
        }

        reader = tmp;
        // read over header line
        testAndGetLine();
    }

    private String testAndGetLine() {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            LOG.severe("File-Iterator can't read line");
        }

        if (line == null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                LOG.severe("File-Iterator can't close file");
            }
            hasNext = false;
            return null;
        }
        return line;
    }

	@Override
	public boolean hasNext() {
        return hasNext;
	}

	@Override
	public String next() {
        StringBuilder toReturn = new StringBuilder();
        Long i = 1L;
        String line;
		while ((line = testAndGetLine()) != null) {
            i++;
            toReturn.append(line);
            toReturn.append(System.getProperty("line.separator"));
            if (i > offset) {
                break;
            }
		}
        Object[] send = new Object[] { 81
                // Sat, 24 May 2014 11:44:57 +0000
                  , new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
                  , new SimpleDateFormat("yyyy-MM-dd-HH.mm")
                  , 18
                  , "Sentiments.csv"
                  , Arrays.asList(23, 81, 18)
              , Arrays.asList("Tweet ID", "Tweet TSD", "Tweet Text")
              , toReturn.toString()};

        return toString(send);
	}
    private String toString( Object o ) {
        String toReturn = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( o );
            oos.close();
            toReturn = new String( Base64.encodeBase64(baos.toByteArray()) );
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return toReturn;
    }
	/**
	 * Dieser Iterator ist read-only.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Iterator is readonly");
	}
}
