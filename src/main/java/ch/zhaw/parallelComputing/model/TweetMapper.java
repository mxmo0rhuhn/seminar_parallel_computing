package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.MapEmitter;
import ch.zhaw.mapreduce.MapInstruction;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import java.util.logging.Logger;

public class TweetMapper implements MapInstruction {

    private static final Logger LOG = Logger.getLogger(TweetMapper.class.getName());

    private static final int ID_INDEX = 23;
    private static final int TSD_INDEX = 81;
    private static final int TWEET_INDEX = 18;

    private static final String OUT_PATH = "Sentiments.csv";

    private static final String IN_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static final String OUT_DATE_FORMAT = "yyyy-MM-dd-HH.mm";
    private static final String DEFAULT_OUT_DATE = "0000-00-00-00.00";

    // Sat, 24 May 2014 11:44:57 +0000
    SimpleDateFormat dateParser = new SimpleDateFormat(IN_DATE_FORMAT);
    SimpleDateFormat targetDate = new SimpleDateFormat(OUT_DATE_FORMAT);

    @Override
    public void map(MapEmitter emitter, String input) {
        LOG.entering(getClass().getName(), "map");

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(OUT_PATH,true));
            CSVReader reader = new CSVReader(new StringReader(input));
            String[] entries = { "ID", "TSD", "Tweet", "Sentiment"};

            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // ID
                entries[0] = nextLine[ID_INDEX];
                // Timestamp
                try {
                    entries[1] = targetDate.format(dateParser.parse(nextLine[TSD_INDEX]));
                } catch (ParseException e) {
                    entries[1] = DEFAULT_OUT_DATE;
                }
                // Sentiment
                entries[2] = findSentiment(nextLine[TWEET_INDEX]).toString();
                // Tweet
                entries[3] = nextLine[TWEET_INDEX];

                writer.writeNext(entries);
                emitter.emitIntermediateMapResult(entries[1], entries[2]);
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            LOG.severe("ERROR in MAP step!!!");
        }
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
