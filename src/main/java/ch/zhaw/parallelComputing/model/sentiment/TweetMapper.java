package ch.zhaw.parallelComputing.model.sentiment;

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
import org.apache.xerces.impl.dv.util.Base64;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import java.util.logging.Logger;

public class TweetMapper implements MapInstruction {

    /**
     * Protocol:
     * [ KEY_ID
     * , IN Date format
     * , OUT Date format
     * , TWEET_ID
     * , Log name
     * , [IDs for logging]
     * , Payload
     * ]
     *
     * If a Log name is given the calculated sentiment will be also logged
     *
     *  Sample
     *  { "81"
     // Sat, 24 May 2014 11:44:57 +0000
     *  , new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
     *  , new SimpleDateFormat("yyyy-MM-dd-HH.mm")
     *  , "18"
     *  , "Sentiments.csv"
     *  , ["23", "81", "18"]
     *  , "CSV"
     *  }
     */

    private static final Logger LOG = Logger.getLogger(TweetMapper.class.getName());

    private int TSD_INDEX = 0;
    private SimpleDateFormat dateParser = null;
    private SimpleDateFormat targetDate = null;

    private int TWEET_INDEX = 0;

    private boolean logging = false;
    private String OUT_PATH = null;
    private List<Integer> logIDs = null;

    private String payload = null;

    private static final String DEFAULT_OUT_DATE = "0000-00-00-00.00";

    @Override
    public void map(MapEmitter emitter, String input) {
        LOG.entering(getClass().getName(), "map");

        try {
            parseProtokollFromString(input);

            CSVWriter writer = null;
            CSVReader reader = new CSVReader(new StringReader(payload));


            String[] entries = null;
            if(logging) {
                writer = new CSVWriter(new FileWriter(OUT_PATH, true));
                // +1 for the sentiment
                entries = new String[logIDs.size() + 1];
            }

            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String key;
                try {
                    key = targetDate.format(dateParser.parse(nextLine[TSD_INDEX]));
                } catch (ParseException e) {
                    key = DEFAULT_OUT_DATE;
                }
                String sentiment = findSentiment(nextLine[TWEET_INDEX]).toString();

                if(logging) {
                    int i = 0;
                    for(Integer id : logIDs) {
                       entries[i] = nextLine[id];
                        i++;
                    }
                    entries[i] = sentiment;
                    writer.writeNext(entries);
                }

                emitter.emitIntermediateMapResult(key, sentiment);
            }
            reader.close();

            if(logging) {
                writer.close();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
            LOG.severe("ERROR in MAP step during input parsing");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            LOG.severe("ERROR in MAP step during I/O");
        }
    }

    /**
     * Read the object from Base64 string.
     */
    private void parseProtokollFromString( String s ) throws IOException , ClassNotFoundException {
        byte [] data = Base64.decode(s);
        ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  data ) );
        Object[] inputArray  = (Object[]) ois.readObject();
        ois.close();

        if(inputArray.length != 7) {
            throw new ClassNotFoundException();
        }

        TSD_INDEX = (Integer) inputArray[0];
        TWEET_INDEX = (Integer) inputArray[3];
        dateParser = (SimpleDateFormat) inputArray[1];
        targetDate = (SimpleDateFormat) inputArray[2];

        OUT_PATH = (String) inputArray[4];
        logIDs = (List<Integer>) inputArray[5];

        if(OUT_PATH == null || logIDs == null ) {
            logIDs = null;
            logging = false;
        } else {
            logging = true;
        }

        payload = (String) inputArray[6];
    }

    private Integer findSentiment(String line) {

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
