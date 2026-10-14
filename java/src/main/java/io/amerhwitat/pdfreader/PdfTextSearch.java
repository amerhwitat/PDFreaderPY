package io.amerhwitat.pdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/** Literal search over embedded selectable PDF text; this does not perform OCR. */
public final class PdfTextSearch {
    public record Match(int page, int line, int column, String text) {}
    private PdfTextSearch() {}

    public static List<Match> search(Path pdf, String query, boolean caseSensitive) throws IOException {
        if (pdf == null) throw new IllegalArgumentException("PDF path is required");
        if (query == null || query.isBlank()) throw new IllegalArgumentException("search query must not be blank");
        if (!Files.isRegularFile(pdf)) throw new IOException("input PDF is not a regular file: " + pdf);
        String needle = caseSensitive ? query : query.toLowerCase(Locale.ROOT);
        List<Match> matches = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(pdf.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String text = stripper.getText(document).replace("\r\n", "\n").replace('\r', '\n');
                String[] lines = text.split("\\n", -1);
                for (int index = 0; index < lines.length; index++) {
                    String line = lines[index];
                    String haystack = caseSensitive ? line : line.toLowerCase(Locale.ROOT);
                    int column = haystack.indexOf(needle);
                    if (column >= 0) matches.add(new Match(page, index + 1, column + 1, line));
                }
            }
        }
        return List.copyOf(matches);
    }

    public static String toJsonLines(List<Match> matches) {
        StringBuilder output = new StringBuilder();
        for (Match match : matches) {
            output.append("{\"page\":").append(match.page())
                .append(",\"line\":").append(match.line())
                .append(",\"column\":").append(match.column())
                .append(",\"text\":\"").append(escape(match.text())).append("\"}\n");
        }
        return output.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || has(args, "--help")) {
            System.out.println("Usage: PdfTextSearch <input.pdf> <query> [--case-sensitive] [--output matches.jsonl]");
            System.out.println("Searches embedded selectable text only; scanned-page OCR is not performed.");
            return;
        }
        if (args.length < 2) throw new IllegalArgumentException("input PDF and search query are required");
        Path input = Path.of(args[0]);
        String query = args[1];
        boolean caseSensitive = false;
        Path output = null;
        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case "--case-sensitive" -> caseSensitive = true;
                case "--output" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--output requires a path");
                    output = Path.of(args[i]);
                }
                default -> throw new IllegalArgumentException("unknown argument: " + args[i]);
            }
        }
        String jsonl = toJsonLines(search(input, query, caseSensitive));
        if (output == null) System.out.print(jsonl);
        else {
            Path parent = output.toAbsolutePath().normalize().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(output, jsonl, StandardCharsets.UTF_8);
            System.out.println("Wrote PDF text search results to " + output);
        }
    }

    private static boolean has(String[] args, String target) {
        for (String arg : args) if (target.equals(arg)) return true;
        return false;
    }
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
