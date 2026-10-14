package io.amerhwitat.pdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/** Deterministic PDF text extraction for selectable-text documents; this is not OCR. */
public final class PdfTextExtract {
    private PdfTextExtract() {}

    public static String extract(Path pdf, int firstPage, int lastPage) throws IOException {
        if (pdf == null) throw new IllegalArgumentException("PDF path is required");
        if (firstPage < 1 || lastPage < firstPage) {
            throw new IllegalArgumentException("page range must satisfy 1 <= firstPage <= lastPage");
        }
        if (!Files.isRegularFile(pdf)) throw new IOException("input PDF is not a regular file: " + pdf);
        try (PDDocument document = Loader.loadPDF(pdf.toFile())) {
            int pageCount = document.getNumberOfPages();
            if (lastPage > pageCount) {
                throw new IllegalArgumentException("last page " + lastPage + " exceeds PDF page count " + pageCount);
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(firstPage);
            stripper.setEndPage(lastPage);
            stripper.setSortByPosition(true);
            return stripper.getText(document).replace("\r\n", "\n").replace('\r', '\n');
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || has(args, "--help")) {
            System.out.println("Usage: PdfTextExtract <input.pdf> [--from N] [--to N] [--output output.txt]");
            System.out.println("Extracts embedded selectable text only; scanned-page OCR requires a configured OCR provider.");
            return;
        }
        Path input = Path.of(args[0]);
        Path output = null;
        int first = 1;
        int last = Integer.MAX_VALUE;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--from" -> first = Integer.parseInt(requireValue(args, ++i, "--from"));
                case "--to" -> last = Integer.parseInt(requireValue(args, ++i, "--to"));
                case "--output" -> output = Path.of(requireValue(args, ++i, "--output"));
                default -> throw new IllegalArgumentException("unknown argument: " + args[i]);
            }
        }
        if (last == Integer.MAX_VALUE) {
            try (PDDocument document = Loader.loadPDF(input.toFile())) {
                last = document.getNumberOfPages();
            }
        }
        String text = extract(input, first, last);
        if (output == null) System.out.print(text);
        else {
            Path parent = output.toAbsolutePath().normalize().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(output, text, StandardCharsets.UTF_8);
            System.out.println("Extracted selectable text to " + output);
        }
    }

    private static String requireValue(String[] args, int index, String option) {
        if (index >= args.length) throw new IllegalArgumentException(option + " requires a value");
        return args[index];
    }

    private static boolean has(String[] args, String target) {
        for (String arg : args) if (target.equals(arg)) return true;
        return false;
    }
}
