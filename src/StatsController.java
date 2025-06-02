import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StatsController {
    private Stage window;

    public void setWindow(Stage window) {
        this.window = window;
    }

    @FXML private Label sentenceStats;
    @FXML private Label wordStats;
    @FXML private Label reductionStats;

    public void setStats(int originalSentence, int summarizedSentence, int originalWord, int summarizedWord) {
        sentenceStats.setText(originalSentence + " to " + summarizedSentence);
        wordStats.setText(originalWord + " to " + summarizedWord);
        reductionStats.setText((int) (((originalWord - summarizedWord) / (double) originalWord) * 100) + " %");
    }
}
