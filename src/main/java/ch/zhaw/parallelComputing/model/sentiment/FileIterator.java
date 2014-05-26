package ch.zhaw.parallelComputing.model.sentiment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        return toReturn.toString();
	}

	/**
	 * Dieser Iterator ist read-only.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Iterator is readonly");
	}
}
