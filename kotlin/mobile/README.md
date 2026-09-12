# PDFreaderPY Kotlin Mobile

Android Kotlin mobile entry point for the PDF/document ingestion application. It preserves the existing Python/PyMuPDF and ancient-object bridge while adding a native mobile boundary.

The starter module is deliberately dependency-light. Future document parsing can call the existing service/library layer through a secure API or a shared Kotlin implementation; arbitrary document contents are never executed.
