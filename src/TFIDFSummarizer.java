import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TFIDFSummarizer extends AbstractSummarizer{
    private final TextProcessor textProcessor;
    private SentenceSplitter splitter = new SentenceSplitter();

    public TFIDFSummarizer(Supplier<Map<String, Double>> doubleMapSupplier, Supplier<Map<String, Integer>> intMapSupplier, 
    TextProcessor textProcessor) {
        super(doubleMapSupplier, intMapSupplier);
        this.textProcessor = textProcessor;
    }

    @Override
    public String summarize(String text, double ratio) {
        List<List<String>> preprocessed = textProcessor.preprocess(text);
        List<String> sentences = List.of(splitter.splitIntoSentences(text));
        int sentenceSize = sentences.size();

        Map<String, Integer> sentenceFrequency = intMapSupplier.get();
        Map<String, Double> idf = doubleMapSupplier.get();

        // Calculate IDF
        for (List<String> sentence : preprocessed) {
            Set<String> uniqueWords = new HashSet<>(sentence);
            for (String word : uniqueWords) {
                sentenceFrequency.put(word, sentenceFrequency.getOrDefault(word, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : sentenceFrequency.entrySet()) {
            idf.put(entry.getKey(), Math.log((double)sentenceSize / entry.getValue()));
        }

        // Calculate TF and sentence score
        List<Double> scoreList = new ArrayList<>();
        for (List<String> sentence : preprocessed) {
            Map<String, Double> tf = doubleMapSupplier.get();
            for (String word : sentence) {
                tf.put(word, tf.getOrDefault(word, 0.0) + 1.0);
            }
            double score = 0.0;
            for (String word : sentence) {
                double tfValue = tf.get(word) / sentence.size(); 
                double idfValue = idf.getOrDefault(word, 0.0);
                score += tfValue * idfValue;
            }
            scoreList.add(score);
        }

        int summarySize = Math.max(1, (int) Math.ceil((sentenceSize * ratio) / Math.pow(Math.log10(sentenceSize + 1), 2.5)));
        List<Integer> rank = IntStream.range(0, scoreList.size()) // Generates stream from index 0 to size of scoreList
        .boxed() // Box primitive int into wrapper class Integer
        .sorted((i, j) -> Double.compare(scoreList.get(j), scoreList.get(i))) // Sort sentence indices by their score descending
        .limit(summarySize) // Take top summarySize indices
        .sorted() // Restore original sentence order
        .collect(Collectors.toList());

        return rank.stream().map(sentences::get).collect(Collectors.joining(" "));
    }
}
