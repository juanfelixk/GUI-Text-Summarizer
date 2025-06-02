import org.tartarus.snowball.ext.PorterStemmer;

public class SnowballStemmer implements Stemmer {
    private final PorterStemmer stemmer = new PorterStemmer();

    public String stem(String word) {
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}