package ch.zhaw.parallelComputing.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Slices the input file for processing
 * 
 * @author Max
 * 
 */
public class FileIterator implements Iterator<String> {

	//  The size of the slices
	private final Long offset;
    private final BufferedReader reader;
    private boolean hasNext;

	public FileIterator(String filename, Long offset) {
        BufferedReader tmp = null;
		this.offset = offset;
        try {
            tmp = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        }

        if (line == null) {
            try {
                reader.close();
            } catch (IOException e) {
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
        StringBuffer toReturn = null;
		if (hasNext()) {
            for (Long i = 0L ; i<offset; i++) {
                toReturn.append(testAndGetLine());
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
