package io.amerhwitat.pdfreader;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class PdfPageIngestTest {
    @TempDir Path tempDir;

    @Test
    void rendersSelectedPageAndWritesProvenanceEvidence() throws Exception {
        Path pdf = tempDir.resolve("fixture.pdf");
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.addPage(new PDPage());
            document.save(pdf.toFile());
        }

        Path image = tempDir.resolve("page-2.png");
        Path evidence = tempDir.resolve("evidence.json");
        PdfPageIngest.main(new String[] {
            pdf.toString(), "--page", "2", "--image", image.toString(), "--output", evidence.toString()
        });

        assertTrue(Files.isRegularFile(image));
        assertTrue(Files.size(image) > 0);
        String json = Files.readString(evidence);
        assertTrue(json.contains("\"schema\": \"ancient-script-pdf-ingest/v1\""));
        assertTrue(json.contains("\"source_page\": 2"));
        assertTrue(json.contains("\"source_page_count\": 2"));
        assertTrue(json.contains("\"status\": \"not-run\""));
    }

    @Test
    void rejectsPageOutsideDocument() throws Exception {
        Path pdf = tempDir.resolve("single-page.pdf");
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(pdf.toFile());
        }

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> PdfPageIngest.main(new String[] {pdf.toString(), "--page", "2"}));
        assertTrue(error.getMessage().contains("exceeds PDF page count"));
    }

    @Test
    void rejectsNonPdfRegularFile() {
        Path missing = tempDir.resolve("missing.pdf");
        assertThrows(java.io.IOException.class,
            () -> PdfPageIngest.main(new String[] {missing.toString()}));
    }
    @Test
    void escapesSpecialCharactersInEvidencePaths() throws Exception {
        Path pdf = tempDir.resolve("quoted\"file.pdf");
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(pdf.toFile());
        }

        Path image = tempDir.resolve("quoted-page.png");
        Path evidence = tempDir.resolve("quoted-evidence.json");
        PdfPageIngest.main(new String[] {
            pdf.toString(), "--image", image.toString(), "--output", evidence.toString()
        });

        String json = Files.readString(evidence);
        assertTrue(json.contains("quoted\\\"file.pdf"));
    }

}

