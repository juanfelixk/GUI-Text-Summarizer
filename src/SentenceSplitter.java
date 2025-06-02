import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SentenceSplitter {
    private static final Set<String> ABBREVIATIONS = new HashSet<>(Set.of(
        "Mr.", "Mrs.", "Dr.", "Ms.", "Jr.", "Sr.", "Prof."
    ));

    // ALGORITHM: (1) skipping closing quotes, (2) checking uppercase next, (3) checking a fixed list of abbreviations
    public String[] splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return new String[0];
        }

        text = text.replace("•", "");

        StringBuilder current = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            current.append(c);

            // If we see a potential sentence‐ending punctuation:
            if (c == '.' || c == '!' || c == '?') {
                // 1) Look backward to capture the word or token ending at this punctuation:
                int k = i;
                while (k >= 0 && (Character.isLetter(text.charAt(k)) || text.charAt(k) == '.')) {
                    k--;
                }
                // The token is from k+1 up to i (inclusive), e.g. "Mr." or "requirement."
                String token = text.substring(k + 1, i + 1);

                // If that token is a known abbreviation, do NOT split here:
                if (ABBREVIATIONS.contains(token)) {
                    continue;
                }

                // 2) Move forward past any spaces **and** any closing‐quote characters:
                int j = i + 1;
                while (j < length && (
                          Character.isWhitespace(text.charAt(j))
                       || text.charAt(j) == '"' 
                       || text.charAt(j) == '”'
                     )) {
                    j++;
                }

                // 3) Now, if j is at end‐of‐text OR the next character is uppercase or an opening quote,
                //    treat this as a real sentence break:
                if (j >= length
                        || Character.isUpperCase(text.charAt(j))
                        || text.charAt(j) == '"' 
                        || text.charAt(j) == '“') {
                    // Add the sentence we've built so far
                    sentences.add(current.toString().trim());
                    current.setLength(0);

                    // Advance i to j-1 (so the for-loop’s i++ lands us at j)
                    i = j - 1;
                }
            }
        }

        // Add any remaining text as the final sentence (if non-empty)
        if (current.length() > 0) {
            sentences.add(current.toString().trim());
        }

        return sentences.toArray(new String[0]);
    }
}
