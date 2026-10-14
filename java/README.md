# Java PDF ingestion adapter

This Java 17+ module ports selectable-text extraction, PDF-page rendering, and provenance-bearing evidence records from the Python workflow. It uses Apache PDFBox and keeps OCR/scanner execution explicitly separate.

## Build and test

From this directory:

```sh
mvn --batch-mode --no-transfer-progress clean verify
```

## Selectable text extraction and search

Extract embedded text from a one-based page range:

```sh
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=io.amerhwitat.pdfreader.PdfTextExtract -Dexec.args='document.pdf --from 1 --to 3 --output extracted.txt'
```

Search selectable text across all pages and emit JSON Lines with page, line, column, and matching line:

```sh
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=io.amerhwitat.pdfreader.PdfTextSearch -Dexec.args='document.pdf inscription --output matches.jsonl'
```

Use `--case-sensitive` for exact case matching. Search returns one result per matching line with one-based page/line/column coordinates; this is literal text search, not OCR or semantic search.

## Page rendering and provenance

`PdfPageIngest` renders a selected page at 144 DPI to PNG and records source document, page number, total page count, image path, and provenance note in `ancient-script-pdf-ingest/v1`. The `scanner_result.status = not-run` value is intentional: OCR/segmentation requires a separately configured scanner adapter.

## GUI

The Swing `PdfReaderGui` is a native desktop application; it does not run inside static GitHub Pages. The Java module is not a full port of every Python workflow. Keep original document bytes and publication/license provenance with the derived output.