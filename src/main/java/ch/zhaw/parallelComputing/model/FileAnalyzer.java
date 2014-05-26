package ch.zhaw.parallelComputing.model;

import au.com.bytecode.opencsv.CSVReader;
import ch.zhaw.parallelComputing.view.ConsoleObserver;
import ch.zhaw.parallelComputing.view.GUI;

import java.io.FileReader;
import java.text.DecimalFormat;

/**
 * Created by Max Schrimpf
 */
public class FileAnalyzer {
    private final GUI consoleObserver;

    public FileAnalyzer(GUI consoleObserver) {
       this.consoleObserver = consoleObserver;
    }

    public void printHeaderWithIndex(String input) {
        try {
            CSVReader reader = new CSVReader(new FileReader(input));
            int i = 0;
            String [] nextLine = reader.readNext() ;

            for(String curLine : nextLine) {
                DecimalFormat df = new DecimalFormat("000");
                consoleObserver.println(df.format(i) + " = " + curLine);
                i++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

    }
}
