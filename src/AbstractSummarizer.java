import java.util.function.Supplier;
import java.util.Map;

public abstract class AbstractSummarizer {
    protected final Supplier<Map<String, Double>> doubleMapSupplier;
    protected final Supplier<Map<String, Integer>> intMapSupplier;

    protected AbstractSummarizer(Supplier<Map<String, Double>> doubleMapSupplier, Supplier<Map<String, Integer>> intMapSupplier) {
        this.doubleMapSupplier = doubleMapSupplier;
        this.intMapSupplier = intMapSupplier;
    }

    public abstract String summarize(String text, double ratio);
}
