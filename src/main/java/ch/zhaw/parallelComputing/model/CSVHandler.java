package ch.zhaw.parallelComputing.model;

import au.com.bytecode.opencsv.CSVReader;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper Methods in CSV File Handling
 *
 * Created by Max Schrimpf
 */
public class CSVHandler {

    /**
     * Parses a CSV File into a dataset for plotting.
     * @param filename The filename to be parsed. By convention it has
     *                 - 1st Column: A Time representation
     *                 - 2nd Column: Numerical Data value
     * @param  timeFormat The time format of the files first column
     * @return A Dataset containing the values of the CSV
     */
    public static XYDataset getDataset(String filename, SimpleDateFormat timeFormat) {
        TimeSeries s2 = new TimeSeries(filename, "Domain", "Range", FixedMillisecond.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        try {
            CSVReader reader = new CSVReader(new FileReader(filename));

            String [] nextLine;

            // HEADER
            nextLine = reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                // Timestamp
                try {
                    Date tsd = timeFormat.parse(nextLine[0]);
                    Float content = Float.parseFloat(nextLine[1]);
                    s2.add(new FixedMillisecond(tsd), content);
                } catch (ParseException e) {
                    e.printStackTrace(System.out);
                    System.out.println("Can't parse " + nextLine);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Can't open " + filename);
        }
        dataset.addSeries(s2);
        return dataset;
    }

    public static List<String> getHeaders(String input) {
        List<String> returnList = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(input));
            int i = 0;
            String [] nextLine = reader.readNext() ;

            for(String curLine : nextLine) {
                returnList.add(i, curLine);
                i++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return returnList;
    }
}
