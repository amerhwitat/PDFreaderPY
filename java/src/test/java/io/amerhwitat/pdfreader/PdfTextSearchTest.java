package io.amerhwitat.pdfreader;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PdfTextSearchTest {
 @TempDir Path tempDir;
 @Test void searchesAcrossPagesWithStableOneBasedLocations() throws Exception {
  Path pdf=tempDir.resolve("search.pdf");
  try(PDDocument doc=new PDDocument()) { addPage(doc,"Alpha inscription"); addPage(doc,"Second ALPHA record"); addPage(doc,"No matching text"); doc.save(pdf.toFile()); }
  var matches=PdfTextSearch.search(pdf,"alpha",false);
  assertEquals(2,matches.size());
  assertEquals(new PdfTextSearch.Match(1,1,1,"Alpha inscription"),matches.get(0));
  assertEquals(new PdfTextSearch.Match(2,1,8,"Second ALPHA record"),matches.get(1));
  assertEquals(1,PdfTextSearch.search(pdf,"Alpha",true).size());
 }
 @Test void validatesQueryAndInput() {
  assertThrows(IllegalArgumentException.class,()->PdfTextSearch.search(tempDir.resolve("missing.pdf"),"x",false));
  assertThrows(IllegalArgumentException.class,()->PdfTextSearch.search(Path.of("missing.pdf"),"  ",false));
 }
 @Test void jsonLinesEscapeTextAndRetainPageMetadata() {
  String jsonl=PdfTextSearch.toJsonLines(java.util.List.of(new PdfTextSearch.Match(2,3,4,"a \"quote\"\\path")));
  assertEquals("{\"page\":2,\"line\":3,\"column\":4,\"text\":\"a \\\"quote\\\"\\\\path\"}\n",jsonl);
 }
 private static void addPage(PDDocument doc,String text) throws IOException {
  PDPage page=new PDPage(); doc.addPage(page);
  try(PDPageContentStream stream=new PDPageContentStream(doc,page)) {
   stream.beginText(); stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),12);
   stream.newLineAtOffset(50,700); stream.showText(text); stream.endText();
  }
 }
}
