/*
 * Copyright (c) 2014 Max Schrimpf
 *
 * This file is part of the parallel computing term paper for the Zurich university of applied sciences.
 *
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.zhaw.parallelComputing.model.sentiment;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Slices the input file for processing.
 * The Iterator also contains all information that is needed for the MAP step and is responsible for the packaging
 * of them into the MAP jobs input.
 *
 * @author Max Schrimpf
 */
public class FileIterator implements Iterator<String> {

    private static final Logger LOG = Logger.getLogger(FileIterator.class.getName());

    // Mandatory input fields
    private final Long offset;
    private final Integer keyID;
    private final String sourceFormat;
    private final String targetFormat;
    private final Integer tweetID;

    // Optional fields
    private final String logFileName;
    private final List<Integer> logFields;

    // Fields for processing
    private boolean hasNext;
    private BufferedReader reader;

    public FileIterator(Long offset, Integer keyID, Integer tweetID,
                        String sourceFormat, String targetFormat,
                        String logFileName, List<Integer> logFields) {

        if(logFields != null) {
            this.logFields = logFields;
        } else {
            this.logFields = new ArrayList<>();
        }

        this.logFileName = logFileName;

        hasNext = true;
        this.offset = offset;
        this.keyID = keyID;
        this.tweetID = tweetID;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    public FileIterator(Long offset, Integer keyID, Integer tweetID,
                        String sourceFormat, String targetFormat) {
        this(offset, keyID, tweetID, sourceFormat, targetFormat, null, null);
    }

    public boolean setFile(String filename) {
        try {
            reader = new BufferedReader(new FileReader(filename));
            // read over header line
            testAndGetLine();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
            LOG.severe("Can't read input file");
        }
        return false;
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
        Object[] send = new Object[]{keyID, sourceFormat, targetFormat, tweetID, logFileName, logFields,
                toReturn.toString()};
        return encode(send);
    }

    private String encode(Object o) {
        String toReturn = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            toReturn = DatatypeConverter.printBase64Binary(baos.toByteArray());
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

    public static boolean isValidDateFormat(String st) {
        try {
            new SimpleDateFormat(st, Locale.ENGLISH);
            return true;
        } catch (IllegalArgumentException e) {
            // invalid pattern
        }
        return false;
    }

    public List<Integer> getLogFields() {
        return logFields;
    }

    public Long getOffset() {
        return offset;
    }

    public Integer getKeyID() {
        return keyID;
    }

    public String getSourceFormatString() {
        return sourceFormat;
    }

    public String getTargetFormatString() {
        return targetFormat;
    }

    public Integer getTweetID() {
        return tweetID;
    }

    public String getLogFileName() {
        return logFileName;
    }
}
