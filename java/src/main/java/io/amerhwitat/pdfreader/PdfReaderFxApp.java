package io.amerhwitat.pdfreader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * JavaFX desktop front-end for the PDFreaderPY Java text tools.
 * Scanned-page OCR is deliberately not advertised or performed.
 */
public final class PdfReaderFxApp extends Application {
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "pdf-reader-worker");
        thread.setDaemon(true);
        return thread;
    });
    private final TextArea documentText = new TextArea();
    private final TextField query = new TextField();
    private final Label status = new Label("Open a PDF to begin. Selectable text only; OCR is not included.");
    private final ProgressIndicator busy = new ProgressIndicator();
    private Path selectedPdf;

    @Override
    public void start(Stage stage) {
        stage.setTitle("PDFreaderPY · JavaFX");
        documentText.setEditable(false);
        documentText.setWrapText(false);
        documentText.setPromptText("Extracted PDF text appears here…");
        query.setPromptText("Search selectable text");
        busy.setMaxSize(22, 22);
        busy.setVisible(false);

        Button open = new Button("Open PDF…");
        open.setOnAction(event -> choosePdf(stage));
        Button search = new Button("Search");
        search.setDefaultButton(true);
        search.setOnAction(event -> searchPdf());
        query.setOnAction(event -> searchPdf());
        Button extract = new Button("Extract text");
        extract.setOnAction(event -> extractPdf());

        HBox toolbar = new HBox(8, open, query, search, extract, busy);
        HBox.setHgrow(query, Priority.ALWAYS);
        toolbar.setPadding(new Insets(12));
        VBox top = new VBox(6, toolbar, status);
        top.setPadding(new Insets(0, 12, 8, 12));

        BorderPane root = new BorderPane(documentText);
        root.setTop(top);
        root.setPadding(new Insets(8));
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    private void choosePdf(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open PDF document");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF documents", "*.pdf"));
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;
        selectedPdf = file.toPath();
        status.setText("Selected: " + selectedPdf.getFileName());
        extractPdf();
    }

    private void extractPdf() {
        if (selectedPdf == null) {
            showError("Choose a PDF file first.");
            return;
        }
        Path input = selectedPdf;
        runTask("Extracting selectable text…", () -> {
            try (PDDocument pdf = Loader.loadPDF(input.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                return "File: " + input.getFileName() + "\nPages: " + pdf.getNumberOfPages()
                    + "\n\n" + stripper.getText(pdf);
            }
        }, text -> documentText.setText(text));
    }

    private void searchPdf() {
        if (selectedPdf == null) {
            showError("Choose a PDF file first.");
            return;
        }
        String term = query.getText();
        if (term == null || term.isBlank()) {
            showError("Enter a non-empty search phrase.");
            return;
        }
        Path input = selectedPdf;
        runTask("Searching selectable text…", () -> {
            List<PdfTextSearch.Match> matches = PdfTextSearch.search(input, term, false);
            if (matches.isEmpty()) return "No matches found for: " + term;
            StringBuilder output = new StringBuilder("Matches for: ").append(term)
                .append("\nCount: ").append(matches.size()).append("\n\n");
            for (PdfTextSearch.Match match : matches) {
                output.append("Page ").append(match.page())
                    .append(", line ").append(match.line())
                    .append(", column ").append(match.column())
                    .append("\n").append(match.text()).append("\n\n");
            }
            return output.toString();
        }, text -> documentText.setText(text));
    }

    private void runTask(String message, IoSupplier<String> action, java.util.function.Consumer<String> success) {
        status.setText(message);
        busy.setVisible(true);
        Task<String> task = new Task<>() {
            @Override protected String call() throws Exception { return action.get(); }
        };
        task.setOnSucceeded(event -> {
            busy.setVisible(false);
            success.accept(task.getValue());
            status.setText("Done · " + (selectedPdf == null ? "" : selectedPdf.getFileName())
                + " · selectable text only; OCR not performed");
        });
        task.setOnFailed(event -> {
            busy.setVisible(false);
            Throwable error = task.getException();
            status.setText("Operation failed.");
            showError(error == null ? "Unknown PDF processing error." : error.getMessage());
        });
        worker.submit(task);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("PDFreaderPY");
        alert.setHeaderText("Could not complete the operation");
        alert.setContentText(message == null || message.isBlank() ? "Unexpected error." : message);
        alert.show();
    }

    @Override
    public void stop() {
        worker.shutdownNow();
        Platform.exit();
    }

    @FunctionalInterface
    private interface IoSupplier<T> { T get() throws IOException; }

    public static void main(String[] args) {
        launch(args);
    }
}
