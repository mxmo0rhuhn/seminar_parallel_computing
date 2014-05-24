package ch.zhaw.parallelComputing.model;

import ch.zhaw.mapreduce.MapEmitter;
import ch.zhaw.mapreduce.MapInstruction;

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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import java.util.logging.Logger;

public class TweetMapper implements MapInstruction {

    private static final String IN_PATH = "raw.csv";
    private static final String OUT_PATH = "Sentients.csv";
    private static final int ID_INDEX = 1;
    private static final int TWEET_INDEX = 1;
    private static final int TSD_INDEX = 1;
    private static final Logger LOG = Logger.getLogger(TweetMapper.class.getName());
    String[] categories;

    @Override
    public void map(MapEmitter emitter, String input) {
        LOG.entering(getClass().getName(), "map", new Object[]{emitter, input});

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(OUT_PATH,true));
            CSVReader reader = new CSVReader(new StringReader(input));

            String[] entries = { "ID", "TSD", "Tweet", "Sentiment"};
//            writer.writeNext(entries);

            // skip header
            String [] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                // ID
                entries[0] = nextLine[23];
                // Timestamp
                entries[1] = nextLine[81];
                // Tweet
                entries[2] = nextLine[18];
                // Sentiment
                entries[3] = findSentiment(nextLine[18]).toString();
                writer.writeNext(entries);
                emitter.emitIntermediateMapResult(entries[1], entries[3]);
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
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
