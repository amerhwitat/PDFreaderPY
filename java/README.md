# Java PDF ingestion adapter

This Java 17+ module ports the PDF-page rendering and provenance-record portion of the Python `ancient_script_ingest.py` workflow. It uses Apache PDFBox and preserves the evidence schema `ancient-script-pdf-ingest/v1`.

## Build and run

From this directory:

```sh
mvn package
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java \
  -Dexec.mainClass=io.amerhwitat.pdfreader.PdfPageIngest \
  -Dexec.args="inscription.pdf --page 1 --image page-1.png --output scan.json"
```

## Native GUI

Launch the desktop reader with a JDK 17+ and Maven:

```sh
mvn package
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java \\
  -Dexec.mainClass=io.amerhwitat.pdfreader.PdfReaderGui
```

The Swing UI opens a PDF using a file chooser, lets the user select a page, and renders the page asynchronously with PDFBox. The GUI is a native desktop application; it does not run inside static GitHub Pages.

## Scope and parity boundary

- Validates the requested one-based page number and input PDF.
- Renders at 144 DPI to PNG and records source document, page number, total page count, image path and provenance note.
- Emits `scanner_result.status = not-run` intentionally. OCR/segmentation remains a separate scanner adapter; this class does not claim to execute the Python scanner.
- Keeps original document bytes and publication/license provenance outside the rendered image.

## Status

Initial Java port added; compile and image-parity tests still need to be run in a JDK/Maven environment. The Python implementation remains the reference until shared fixture tests establish parity.
