import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
public class CoreNLPTest {

    private static final String IN_PATH = "raw.csv";
    private static final String OUT_PATH = "Sentiments.csv";
    private static final int ID_INDEX = 23;
    private static final int TSD_INDEX = 81;
    private static final int TWEET_INDEX = 18;
    private static final String IN_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String OUT_DATE_FORMAT = "yyyy-MM-DD-HH.mm";
    private static final String DEFAULT_OUT_DATE = "0000-00-00-00.00";

    // Sat, 24 May 2014 11:44:57 +0000
    SimpleDateFormat dateParser = new SimpleDateFormat(IN_DATE_FORMAT);
    SimpleDateFormat targetDate = new SimpleDateFormat(OUT_DATE_FORMAT);

    public static void main(String[] args) throws Exception {
        new CoreNLPTest().parseCSV();
    }

    public void parseCSV() throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(OUT_PATH));
        CSVReader reader = new CSVReader(new FileReader(IN_PATH));

        String[] entries = { "ID", "TSD", "Tweet", "Sentiment"};
        writer.writeNext(entries);

        // skip header
        String [] nextLine = reader.readNext();
        while ((nextLine = reader.readNext()) != null) {
            entries[0] = nextLine[ID_INDEX];
            entries[1] = nextLine[TSD_INDEX];

            entries[2] = nextLine[TSD_INDEX];
            try {
                entries[2] = targetDate.format(dateParser.parse(nextLine[TSD_INDEX]));
            } catch (ParseException e) {
                entries[2] = DEFAULT_OUT_DATE;
            }
            entries[3] = findSentiment(nextLine[TSD_INDEX]).toString();
            writer.writeNext(entries);
        }
        reader.close();
        writer.close();
    }

    private Integer findSentiment(String line) throws Exception {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        return mainSentiment;
    }
}
