import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SentenceSplitter {
    private static final Set<String> HONORIFICS = new HashSet<>(Set.of(
        "Mr.", "Mrs.", "Dr.", "Ms.", "Jr.", "Sr.", "Prof."
    ));

    // ALGORITHM: (1) skipping closing quotes, (2) checking uppercase next, (3) checking a fixed list of abbreviations
    public String[] splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return new String[0];
        }

        // Clean bullet points
        text = text.replace("•", "");

        StringBuilder current = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            current.append(c);
            // Check for a potential ending punctuation
            if (c == '.' || c == '!' || c == '?') {
                // Look backward to capture the word or token ending at this punctuation
                int k = i;
                while (k >= 0 && (Character.isLetter(text.charAt(k)) || text.charAt(k) == '.')) {
                    k--;
                }
                String token = text.substring(k + 1, i + 1);
                // If token is a known honorific, do not split
                if (HONORIFICS.contains(token)) {
                    continue;
                }
                // Move forward past any spaces and any closing quotes
                int j = i + 1;
                while (j < length && (
                          Character.isWhitespace(text.charAt(j))
                       || text.charAt(j) == '"' 
                       || text.charAt(j) == '”'
                     )) {
                    j++;
                }
                // If j is at end or the next character is uppercase or an opening quote, split the sentence
                if (j >= length
                        || Character.isUpperCase(text.charAt(j))
                        || text.charAt(j) == '"' 
                        || text.charAt(j) == '“') {
                    sentences.add(current.toString().trim());
                    current.setLength(0);
                    // Advance i to j-1 (so the for loop’s i++ lands at j)
                    i = j - 1;
                }
            }
        }

        // Add any remaining text as the final sentence (if not empty)
        if (current.length() > 0) {
            sentences.add(current.toString().trim());
        }

        return sentences.toArray(new String[0]);
    }
}
