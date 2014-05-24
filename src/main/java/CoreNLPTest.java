import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
/*
import static org.apache.uima.fit.factory.AnalysisEngineFactory.*;
import static org.apache.uima.fit.factory.CollectionReaderFactory.*;
import static org.apache.uima.fit.pipeline.SimplePipeline.*;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.CasDumpWriter;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
*/

public class CoreNLPTest {

    private static final String IN_PATH = "raw.csv";
    private static final String OUT_PATH = "Sentients.csv";
    private static final int ID_INDEX = 1;
    private static final int TWEET_INDEX = 1;
    private static final int TSD_INDEX = 1;

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
            entries[0] = nextLine[23];
            entries[1] = nextLine[81];
            entries[2] = nextLine[18];
            entries[3] = findSentiment(nextLine[18]).toString();
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
