# PDFreaderPY JavaFX desktop app

The Java module includes a JavaFX front-end backed by Apache PDFBox and the existing `PdfTextSearch` API.

## Run locally

Requirements: JDK 17+ and Maven 3.9+.

```bash
cd java
mvn --batch-mode clean verify
mvn javafx:run
```

Features:
- Open a local PDF using the native file chooser.
- Extract embedded/selectable text without blocking the UI thread.
- Search selectable text and show page, line, and column metadata.
- Display clear errors for unreadable documents and empty search queries.

**OCR is not implemented.** Scanned pages without an embedded text layer will not yield searchable text. The JavaFX app runs on a local JVM; the companion GitHub Pages UI remains a browser-native app and does not execute JavaFX bytecode.
