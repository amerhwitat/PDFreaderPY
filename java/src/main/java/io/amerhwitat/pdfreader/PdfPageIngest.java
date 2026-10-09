package io.amerhwitat.pdfreader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Java counterpart to ancient_script_ingest.py's PDF page rendering step.
 * It creates a provenance-bearing evidence JSON record without pretending an
 * OCR scanner ran; scanner integration is an explicit separate adapter.
 */
public final class PdfPageIngest {
    private PdfPageIngest() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || has(args, "--help")) {
            usage();
            return;
        }

        Path pdf = Path.of(args[0]);
        int page = 1;
        Path output = Path.of("ancient_script_evidence.json");
        Path image = null;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--page" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--page requires a number");
                    page = Integer.parseInt(args[i]);
                }
                case "--output" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--output requires a path");
                    output = Path.of(args[i]);
                }
                case "--image" -> {
                    if (++i >= args.length) throw new IllegalArgumentException("--image requires a path");
                    image = Path.of(args[i]);
                }
                default -> throw new IllegalArgumentException("unknown argument: " + args[i]);
            }
        }
        if (page < 1) throw new IllegalArgumentException("page must be >= 1");
        if (!Files.isRegularFile(pdf)) throw new IOException("input PDF is not a regular file: " + pdf);

        if (image == null) {
            String base = pdf.getFileName() == null ? "document" : pdf.getFileName().toString();
            int dot = base.lastIndexOf('.');
            if (dot > 0) base = base.substring(0, dot);
            image = output.toAbsolutePath().resolveSibling(base + "-page-" + page + ".png");
        }

        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Files.createDirectories(image.toAbsolutePath().normalize().getParent());
        int totalPages;
        try (PDDocument document = Loader.loadPDF(pdf.toFile())) {
            totalPages = document.getNumberOfPages();
            if (page > totalPages) {
                throw new IllegalArgumentException("page " + page + " exceeds PDF page count " + totalPages);
            }
            BufferedImage rendered = new PDFRenderer(document).renderImageWithDPI(page - 1, 144, ImageType.RGB);
            if (!ImageIO.write(rendered, "png", image.toFile())) {
                throw new IOException("no PNG image writer is available");
            }
        }

        String json = "{\n"
            + "  \"schema\": \"ancient-script-pdf-ingest/v1\",\n"
            + "  \"source_document\": \"" + escape(pdf.toString()) + "\",\n"
            + "  \"source_page\": " + page + ",\n"
            + "  \"source_page_count\": " + totalPages + ",\n"
            + "  \"rendered_image\": \"" + escape(image.toString()) + "\",\n"
            + "  \"scanner_result\": { \"status\": \"not-run\", \"reason\": \"Configure a scanner adapter to run OCR separately.\" },\n"
            + "  \"provenance_note\": \"Rendered from the specified PDF page; retain the original PDF and publication/license metadata.\"\n"
            + "}\n";
        Files.writeString(output, json, StandardCharsets.UTF_8);
        System.out.println("Created evidence record: " + output);
    }

    private static boolean has(String[] args, String target) {
        for (String arg : args) if (target.equals(arg)) return true;
        return false;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static void usage() {
        System.out.println("Usage: PdfPageIngest <pdf> [--page N] [--image page.png] [--output evidence.json]");
    }
}
