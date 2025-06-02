import java.io.IOException;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Controller {
    private Stage window;

    public void setWindow(Stage window) {
        this.window = window;
    }

    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Button summarizeBtn;
    @FXML private Label inputCount;
    @FXML private Label outputCount;
    @FXML private Slider lengthSlider;
    @FXML private Button paragraphBtn;
    @FXML private Button bulletBtn;
    @FXML private Button copyBtn;
    @FXML private Button pasteBtn;
    @FXML private Button clearBtn;
    @FXML private Button statsBtn;

    private double summaryRatio;
    private boolean paragraphFormat;
    private SentenceSplitter splitter = new SentenceSplitter();

    @FXML
    public void initialize() {
        TFIDFSummarizer summarizer = new TFIDFSummarizer(TreeMap::new, TreeMap::new, new DefaultTextProcessor(new SnowballStemmer()));

        paragraphFormat = true;
        paragraphBtn.setStyle("-fx-border-color: #007ACC; -fx-border-width: 2px;"); 
        bulletBtn.setStyle("-fx-border-color: transparent;");

        paragraphBtn.setOnAction((ActionEvent ae) -> {
            paragraphFormat = true;
            paragraphBtn.setStyle("-fx-border-color: #007ACC; -fx-border-width: 2px;");
            bulletBtn.setStyle("-fx-border-color: transparent;");
        });

        bulletBtn.setOnAction((ActionEvent ae) -> {
            paragraphFormat = false;
            bulletBtn.setStyle("-fx-border-color: #007ACC; -fx-border-width: 2px;");
            paragraphBtn.setStyle("-fx-border-color: transparent;");
        });

        inputArea.textProperty().addListener((obs, oldText, newText) -> {
            inputCount.setText(countWord(newText) + " words");
            if (newText == null || newText.isEmpty()) {
                outputArea.clear();
                outputCount.setText("0 words");
            }
        });
        
        summaryRatio = 0.3;
        lengthSlider.valueProperty().addListener((obs, oldV, newV) -> {
            int sliderVal = newV.intValue();      
            switch (sliderVal) {
                case 0:
                    summaryRatio = 0.2;
                    break;
                case 1:
                    summaryRatio = 0.3;
                    break;
                case 2:
                    summaryRatio = 0.4;
                    break;
            }
    });

        summarizeBtn.setOnAction((ActionEvent ae) -> {
            String summary = summarizer.summarize(inputArea.getText(), summaryRatio);
            if (!paragraphFormat) {
                String[] sents = splitter.splitIntoSentences(summary);
                String formatted = IntStream.range(0, sents.length).mapToObj(i -> "• " + sents[i].trim()).collect(Collectors.joining("\n"));
                outputArea.setText(formatted);
            } else {
                outputArea.setText(summary);
            }
            outputCount.setText(countWord(summary) + " words");
        });

        copyBtn.setOnAction(e -> {
            String textToCopy = outputArea.getText();
            if (textToCopy != null && !textToCopy.isEmpty()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(textToCopy);
                clipboard.setContent(content);
            }
        });

        pasteBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String clipboardText = clipboard.getString();
            if (clipboardText != null) {
                inputArea.setText(clipboardText);
            }
        });

        clearBtn.setOnAction(e -> {
            inputArea.clear();
        });

        statsBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("stats.fxml"));
                Parent root = loader.load();
                StatsController statscontroller = loader.getController();
                Stage statsStage = new Stage();
                statscontroller.setWindow(statsStage);
                statsStage.initModality(Modality.WINDOW_MODAL);
                statsStage.initOwner(window);
                statsStage.setTitle("Statistics");
                statsStage.setScene(new Scene(root, 500, 300));
                statscontroller.setStats(splitter.splitIntoSentences(inputArea.getText()).length, splitter.splitIntoSentences(outputArea.getText()).length, countWord(inputArea.getText()), countWord(outputArea.getText()));
                statsStage.showAndWait();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private int countWord(String text) {
        String trimmed = (text == null ? "" : text.replace("•", "").trim());
        if (trimmed.isEmpty()) {
            return 0;
        } else {
            return trimmed.split("\\s+").length;
        }
    }
}