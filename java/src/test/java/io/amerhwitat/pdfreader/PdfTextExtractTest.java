package io.amerhwitat.pdfreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfTextExtractTest {
    @TempDir Path tempDir;

    @Test
    void extractsSelectableTextFromRequestedPageRange() throws Exception {
        Path pdf = tempDir.resolve("sample.pdf");
        try (PDDocument doc = new PDDocument()) {
            addTextPage(doc, "first page text");
            addTextPage(doc, "second page text");
            doc.save(pdf.toFile());
        }
        assertEquals("second page text" + System.lineSeparator(),
            PdfTextExtract.extract(pdf, 2, 2).replace("\n", System.lineSeparator()));
    }

    @Test
    void rejectsInvalidRangesAndMissingFiles() {
        Path missing = tempDir.resolve("missing.pdf");
        assertThrows(IllegalArgumentException.class, () -> PdfTextExtract.extract(missing, 0, 1));
        assertThrows(IOException.class, () -> PdfTextExtract.extract(missing, 1, 1));
    }

    @Test
    void rejectsPagePastDocumentEnd() throws Exception {
        Path pdf = tempDir.resolve("one-page.pdf");
        try (PDDocument doc = new PDDocument()) {
            addTextPage(doc, "only page");
            doc.save(pdf.toFile());
        }
        assertThrows(IllegalArgumentException.class, () -> PdfTextExtract.extract(pdf, 1, 2));
    }

    private static void addTextPage(PDDocument doc, String text) throws IOException {
        PDPage page = new PDPage();
        doc.addPage(page);
        try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
            stream.beginText();
            stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            stream.newLineAtOffset(50, 700);
            stream.showText(text);
            stream.endText();
        }
    }
}
