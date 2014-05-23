import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

import com.sun.rmi.rmid.ExecPermission;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
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

    private static final String PATH = "text.txt";

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        String line;
        while ((line = br.readLine()) != null) {
            findSentiment(line);
        }
        br.close();
    }
/*
        CollectionReaderDescription cr = createReaderDescription(
                TextReader.class,
                TextReader.PARAM_PATH, "src/test/resources/*.txt",
                TextReader.PARAM_LANGUAGE, "en");

        AnalysisEngineDescription seg = createEngineDescription(BreakIteratorSegmenter.class);

        AnalysisEngineDescription tagger = createEngineDescription(OpenNlpPosTagger.class);

        AnalysisEngineDescription cc = createEngineDescription(
                CasDumpWriter.class,
                CasDumpWriter.PARAM_OUTPUT_FILE, "target/output.txt");

        runPipeline(cr, seg, tagger, cc);
    }
*/
        public static void findSentiment(String line) throws Exception{

            String newLine = System.getProperty("line.separator");
        FileWriter fos = new FileWriter("output.txt", true);
            BufferedWriter bw = new BufferedWriter(fos);


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
            bw.write("line: '" + line + "' sentiment: " + mainSentiment + newLine);
            bw.close();
    }
}
