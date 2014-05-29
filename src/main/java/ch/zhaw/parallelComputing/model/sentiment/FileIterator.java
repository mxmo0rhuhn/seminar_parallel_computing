package ch.zhaw.parallelComputing.model.sentiment;

import org.apache.mina.util.Base64;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Slices the input file for processing
 * 
 * @author Max Schrimpf
 * 
 */
public class FileIterator implements Iterator<String> {

    private static final Logger LOG = Logger.getLogger(FileIterator.class.getName());

    // Mandatory input fields
    private final BufferedReader reader;
	//  The size of the slices
	private final Long offset;
    private final Integer keyID;
    private final SimpleDateFormat sourceFormat;
    private final SimpleDateFormat targetFormat;
    private final Integer tweetID;

    // Optional fields
    private String logFileName = "Sentiments.csv";
    private List<Integer> logFields = null;

    // Fields for processing
    private boolean hasNext;

	public FileIterator(String filename, Long offset, Integer keyID, Integer tweetID,
                        SimpleDateFormat sourceFormat, SimpleDateFormat targetFormat) {
        hasNext = true;
        BufferedReader tmp = null;
		this.offset = offset;
        try {
            tmp = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
            LOG.severe("Can't read input file");
        }

        this.keyID = keyID;
        this.tweetID = tweetID;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.reader = tmp;

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
        Object[] send = new Object[] { keyID, sourceFormat, targetFormat, tweetID, logFileName, logFields,
                                        toReturn.toString()};
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
	 * This iterator is read-only.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Iterator is readonly");
	}

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public List<Integer> getLogFields() {
        return logFields;
    }

    public void setLogFields(List<Integer> logFields) {
        this.logFields = logFields;
    }
}
