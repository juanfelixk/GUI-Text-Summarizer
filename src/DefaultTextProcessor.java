import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultTextProcessor implements TextProcessor {
    private final Stemmer stemmer;
    private final Set<String> stopWords;
    private SentenceSplitter splitter = new SentenceSplitter();

    public DefaultTextProcessor(Stemmer stemmer) {
        this.stemmer = stemmer;
        this.stopWords = loadStopWords();
    }

    public static Set<String> loadStopWords() {
        return new HashSet<>(Arrays.asList("the", "is", "in", "and", "a", "to", "of", 
        "for", "on", "by", "with", "at", "as", "from"));
    }

    public List<List<String>> preprocess(String text) {
        List<String> sentences = List.of(splitter.splitIntoSentences(text));
        return sentences.stream()
            .map(s -> Arrays.stream(
                    s.replaceAll("[^a-zA-Z\\s]", "") // Remove non alphabetical characters
                     .toLowerCase() // Convert the text to lowercase
                     .split("\\s+")) // Split the sentence into words
                .filter(tok -> !tok.isEmpty() && !stopWords.contains(tok)) // Filter out empty tokens and stop words
                .map(stemmer::stem) // Apply stemming
                .collect(Collectors.toList())) // Collect processed words as a list
            .collect(Collectors.toList()); // Collect all sentence lists into a list of sentences
    }
}
